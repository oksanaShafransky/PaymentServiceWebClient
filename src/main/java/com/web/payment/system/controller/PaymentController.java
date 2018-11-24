package com.web.payment.system.controller;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.beans.PaymentMethod;
import com.payment.service.dto.beans.User;
import com.payment.service.dto.beans.UserCredentials;
import com.payment.service.dto.utils.CurrencyList;
import com.web.payment.system.client.PaymentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

//TODO: add payment validator
//TODO: add security for login
//TODO: add logout user
//TODO: report payments

@Controller
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PaymentClient paymentClient;
    @Autowired
    MessageSource messageSource;

    /**
     * The method prepares and load loginuser.jsp
     * @return model of loginuser
     */
    @RequestMapping(value = "/loginuser", method = RequestMethod.GET)
    public ModelAndView getLoginInitialView() {
        User user = new User();
        ModelAndView mav = new ModelAndView("loginuser", "command", user);
        mav.addObject("usermail", "usermail");
        mav.addObject("userpassword", "userpassword");
        mav.addObject("userid", user.getUserid());

        return mav;
    }

    /**
     * The method verifies the username and password fit.
     * If it fits, it loads payment.jsp.
     * Otherwise, it returns to loginuser.jsp.
     * @param user
     * @param result
     * @param model
     * @return payment.jsp/loginuser.jsp
     */
    @RequestMapping(value = "/authenticatelogin", method = RequestMethod.POST)
    public String submitLogin(@ModelAttribute("payerid") User user, BindingResult result, Model model) {
        User validateduser = paymentClient.loginUser(user.getUsermail(), user.getUserpassword());
        if (result.hasErrors()) {
            logger.error("Log in failed for user {}", user.getUsermail());
            return "loginuser";
        }
        logger.info("The user {} logged in successfully", user.getUsermail());
        Payment payment = new Payment();
        payment.setPayerid(validateduser.getUserid());
        model.addAttribute("command", payment);
        model.addAttribute("payerid", validateduser.getUserid());
        List<String> payeeIdList = getPayees(validateduser.getUserid());
        model.addAttribute("payeeid", payeeIdList);
        String userid = model.asMap().get("payerid").toString();
        List<String> userCredentialsList = getPaymentNumberForUser(userid);
        model.addAttribute("paymentnumber",userCredentialsList);
        return "payment";
    }

    /**
     * Returns list of registered users to pay except the logged one
     * @param currentPayerId
     * @return list of payees
     */
    private List getPayees(String currentPayerId){
        List<String> payeeIdList = new ArrayList<String>();
        List<User> users = paymentClient.getAllUsersList();
        for (User u:users) {
            if(!u.getUserid().equals(currentPayerId)){
                payeeIdList.add(u.getUsermail());
            }
        }
        return payeeIdList;
    }

    /**
     * load payment.jsp on new payment request from result.jsp/error.jsp
     * @param payment
     * @param result
     * @param model
     * @return payment.jsp
     */
    @RequestMapping(value = "/newpayment", method = RequestMethod.POST)
    public String newPayment(@ModelAttribute("payment") Payment payment, BindingResult result, Model model) {
        String payerId = payment.getPayerid();
        model.addAttribute("command", new Payment());
        model.addAttribute("payerid", payerId);
        List<String> userCredentialsList = getPaymentNumberForUser(payerId);
        model.addAttribute("paymentnumber",userCredentialsList);
        List<String> payeeIdList = getPayees(payerId);
        model.addAttribute("payeeid", payeeIdList);

        return "payment";
    }

    /**
     * returns list of paymentmethodname:(*paymentnumber) for specific user id
     * @param id
     * @return list of paymentmethods
     */
    private List<String> getPaymentNumberForUser(String id){
        List<String> userCredentialsList = new ArrayList<String>();
        List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserId(id);
        for (UserCredentials credential:userCredentials) {
            PaymentMethod paymentmethod = paymentClient.getPaymentMethodById(credential.getPaymentmethodid());
            int length = credential.getPaymentnumber().length();
            //prepare the payment method to expose on the web page compound of paymentmethod name and
            //4 last numbers of payment number for this payment method for specific user followed by (*...)
            userCredentialsList.add(paymentmethod.getMethodname()+":(*" +
                    credential.getPaymentnumber().substring(length-4,length)+")");
        }
        return userCredentialsList;
    }

    /**
     * The method populates the model with list of currencies available at enum CurrencyList
     * @return list of currencies
     */
    @ModelAttribute("currencyList")
    private List<String> getCurrencyList(){
        List currency = new ArrayList();
        CurrencyList[] currencies = CurrencyList.values();
        for (CurrencyList cur:currencies) {
            currency.add(cur);
        }
        return currency;
    }

    /**
     * The method sends new payment to the server.
     * The payment is performed asynchronously.
     * First the payment is sent to the kafka queue and risk engine verification.
     * Then the method verifies if the payment was added to the database by getById API.
     * If it was found the success page will be returned.
     * Otherwise, the error page will be returned.
     * @param payment
     * @param result
     * @param model
     * @return appropriate response page - result or error
     */
    @RequestMapping(value = "/add_payment", method = RequestMethod.POST)
    public String addPayment(@ModelAttribute("SpringWeb")@Validated Payment payment,
                             BindingResult result, ModelMap model) {
        logger.info("Received request to perform new payment for user {}", payment.getPayerid());
        User payee = paymentClient.getUserByMail(payment.getPayeeid());
        String [] method = payment.getPaymentnumber().split(":");
        PaymentMethod paymentMethod = paymentClient.getPaymentMethodByName(method[0]);
        List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserId(payment.getPayerid());
        String paymentnumber = findPaymentNumber(paymentMethod.getPaymentmethodid(), method[1], userCredentials);
        payment.setPayeeid(payee.getUserid());
        payment.setPaymentmethodid(paymentMethod.getPaymentmethodid());
        payment.setPaymentnumber(paymentnumber);

        model.addAttribute("command", new Payment());
        model.addAttribute("paymentid", payment.getPaymentid());
        model.addAttribute("payerid", payment.getPayerid());
        model.addAttribute("payeeid", payee.getUserid());
        model.addAttribute("currency", payment.getCurrency());
        model.addAttribute("paymentmethodid", payment.getPaymentmethodid());
        model.addAttribute("paymentdescription", payment.getPaymentdescription());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("paymentnumber", payment.getPaymentnumber());
        ResponseEntity<Payment> responseEntity = new ResponseEntity<Payment>(paymentClient.addPayment(payment), HttpStatus.CREATED);

        //verify the response
        if(responseEntity.getStatusCode()==HttpStatus.CREATED) {
            return paymentClient.validateIfPaymentSuccessful(responseEntity.getBody().getPaymentid())==null?"error":"result";
        } else {
            logger.error("The payment failed: {}", responseEntity.toString());
            return "error";
        }
    }

    /**
     * The method extracts the payment number of the payer from methodname:(*paymentnumber) string.
     * @param paymentmethodid
     * @param paymentnumber
     * @param userCredentials
     * @return payment number of the payer
     */
    private String findPaymentNumber(String paymentmethodid, String paymentnumber, List<UserCredentials> userCredentials) {
        for (UserCredentials cred:userCredentials) {
            int length1 = paymentnumber.length();
            int length2 = cred.getPaymentnumber().length();
            if(cred.getPaymentmethodid().equals(paymentmethodid) &&
                    cred.getPaymentnumber().substring(length2-4,length2).equals(paymentnumber.substring(2, length1-1 ))){
                logger.info("The full payment number is {}",cred.getPaymentnumber());
                return cred.getPaymentnumber();
            }
        }
        logger.warn("No payment number was found");
        return null;
    }
}
