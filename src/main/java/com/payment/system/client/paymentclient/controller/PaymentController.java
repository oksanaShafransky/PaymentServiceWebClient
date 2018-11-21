package com.payment.system.client.paymentclient.controller;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.beans.PaymentMethod;
import com.payment.service.dto.beans.User;
import com.payment.service.dto.beans.UserCredentials;
import com.payment.system.client.paymentclient.client.PaymentClient;
import com.payment.system.client.paymentclient.validation.PaymentValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@Controller
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PaymentClient paymentClient;
    @Autowired
    private PaymentValidator validator;
    @Autowired
    MessageSource messageSource;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(validator);
    }

    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    public ModelAndView getInitialView() {
        ModelAndView model = new ModelAndView("payment", "command", new Payment());
        model.addObject("payerid", "payerIdList");
        model.addObject("payeeid", "payeeIdList");
        model.addObject("paymentmethodid", "paymentMethodIdList");
        model.addObject("currency", "currencyList");
        model.addObject("paymentnumber","userCredentialsList");

        return model;

    }

    @ModelAttribute("currencyList")
    private List<String> getCurrencyList(){
        List currency = new ArrayList();
        currency.add("USD");
        currency.add("EUR");
        currency.add("ILS");
        currency.add("GBP");
        currency.add("CHY");
        currency.add("RUB");
        return currency;
    }

    @ModelAttribute("payerIdList")
    public List<String> getPayerIdList() {
        List<String> payerIdList = new ArrayList<String>();
        List<User> users = paymentClient.getAllUsersList();
        users.forEach(user->payerIdList.add(user.getUsermail()));
        return payerIdList;
    }

    @ModelAttribute("payeeIdList")
    public List<String> getPayeeIdList() {
        List<String> payeeIdList = new ArrayList<String>();
        List<User> users = paymentClient.getAllUsersList();
        users.forEach(user->payeeIdList.add(user.getUsermail()));
        return payeeIdList;
    }

    @ModelAttribute("paymentMethodList")
    public List<String> getPaymentMethodIdList() {
        List<String> paymentMethodId = new ArrayList<String>();
        List<PaymentMethod> paymentMethods = paymentClient.getAllPaymenMethods();
        paymentMethods.forEach(paymentMethod->paymentMethodId.add(paymentMethod.getMethodname()));
        return paymentMethodId;
    }

    @ModelAttribute("paymentNumberList")
    public List<String> getPaymentNumberList(String id) {
        List<String> userCredentialsList = new ArrayList<String>();
        //List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserId(id);
        //userCredentials.forEach(userCredential->userCredentialsList.add(userCredential.getPaymentnumber()));
        return userCredentialsList;
    }

    //filter the list of payment methods for concrete selected payer
    @RequestMapping(value = "payment/filter", method = RequestMethod.GET)
    public List<String> filterPaymentMethodList() {
        System.out.println("#######Inside filter");
        User payer = paymentClient.getUserByMail("dudu@dudu.com");
        List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserIdAndMethod(payer.getUserid(),"");
        List<String> paymentMethodIdList = new ArrayList<String>();
        for (UserCredentials userCred:userCredentials) {
            String paymentmethodname = paymentClient.getPaymentMethodById(userCred.getPaymentmethodid()).getMethodname();
            paymentMethodIdList.add(paymentmethodname + "(*" + userCred.getPaymentnumber().substring(0,3) + ")");
        }
        return paymentMethodIdList;
    }

    @RequestMapping(value = "/add_payment", method = RequestMethod.POST)
    public String addPayment(@ModelAttribute("SpringWeb")@Validated Payment payment,
                             BindingResult result, ModelMap model) {
        User payer = paymentClient.getUserByMail(payment.getPayerid());
        User payee = paymentClient.getUserByMail(payment.getPayeeid());
        PaymentMethod paymentMethod = paymentClient.getPaymentMethodByName(payment.getPaymentmethodid());
        //List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserId(payer.getUserid());

        payment.setPaymentid(UUID.randomUUID().toString());
        payment.setPayerid(payer.getUserid());
        payment.setPayeeid(payee.getUserid());
        payment.setPaymentmethodid(paymentMethod.getPaymentmethodid());


        model.addAttribute("paymentid", payment.getPaymentid());
        model.addAttribute("payerid", payer.getUserid());
        model.addAttribute("payeeid", payee.getUserid());
        model.addAttribute("currency", payment.getCurrency());
        model.addAttribute("paymentmethodid", payment.getPaymentmethodid());
        model.addAttribute("paymentdescription", payment.getPaymentdescription());
        model.addAttribute("amount", payment.getAmount());
        model.addAttribute("paymentnumber", payment.getPaymentnumber());
        ResponseEntity<Payment> responseEntity = new ResponseEntity<Payment>(paymentClient.addPayment(payment), HttpStatus.CREATED);
        if(responseEntity.getStatusCode()==HttpStatus.CREATED) {
            return paymentClient.validateIfPaymentSuccessful(payment.getPaymentid())==null?"error":"result";
        } else {
            return "error";
        }
    }
}
