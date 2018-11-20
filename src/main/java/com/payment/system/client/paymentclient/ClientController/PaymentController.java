package com.payment.system.client.paymentclient.ClientController;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.beans.PaymentMethod;
import com.payment.service.dto.beans.User;
import com.payment.service.dto.beans.UserCredentials;
import com.payment.system.client.paymentclient.PaymentClient.PaymentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

@Controller
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private PaymentClient paymentClient;

    @RequestMapping(value = "/payment", method = RequestMethod.GET)
    public ModelAndView getdata() {
        List<String> payerIdList = new ArrayList<String>();
        List<User> users = paymentClient.getAllUsersList();
        users.forEach(user->payerIdList.add(user.getUsername()));
        List<String> paymentMethodId = new ArrayList<String>();
        List<PaymentMethod> paymentMethods = paymentClient.getAllPaymenMethods();
        paymentMethods.forEach(paymentMethod->paymentMethodId.add(paymentMethod.getPaymentmethodid()));

        ModelAndView model = new ModelAndView("payment", "command", new Payment());
        model.addObject("payerid", users);
        model.addObject("payeeid", users);
        model.addObject("paymentmethodid", users);
        model.addObject("currency", getCurrencyList());

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

    //filter the list of payment methods for concrete selected payer
    public List<String> filterPaymentMethodList(String payermail) {
        User payer = paymentClient.getUserByMail(payermail);
        List<UserCredentials> userCredentials = paymentClient.getUserCredentialsByUserId(payer.getUserid());
        List<String> paymentMethodIdList = new ArrayList<String>();
        for (UserCredentials userCred:userCredentials) {
            String paymentmethodname = paymentClient.getPaymentMethodById(userCred.getPaymentmethodid()).getMethodname();
            paymentMethodIdList.add(paymentmethodname + "(*" + userCred.getPaymentnumber().substring(0,3) + ")");
        }
        return paymentMethodIdList;
    }

    @RequestMapping(value="/payment", method = RequestMethod.POST)
    public ResponseEntity<Payment> createPayment(@ModelAttribute("Payment") Payment req){
        Payment payment = new Payment();
        User payer = paymentClient.getUserByMail(req.getPayerid());
        User payee = paymentClient.getUserByMail(req.getPayeeid());
        PaymentMethod paymentMethod = paymentClient.getPaymentMethodByName(payment.getPaymentmethodid());

        payment.setPaymentmethodid(paymentMethod.getPaymentmethodid());
        payment.setPaymentid(UUID.randomUUID().toString());
        payment.setPayerid(payer.getUserid());
        payment.setPayeeid(payee.getUserid());
        payment.setCurrency(req.getCurrency());

        payment.setPaymentdescription(req.getPaymentdescription());
        payment.setPaymentmethodid(req.getPaymentmethodid());
        payment.setAmount(Float.valueOf(req.getAmount()));
        return new ResponseEntity<Payment>(paymentClient.addPayment(payment), HttpStatus.CREATED);
    }


    @RequestMapping(value = "/add_payment", method = RequestMethod.POST)
    public String addPayment(@ModelAttribute("SpringWeb")Payment payment, ModelMap model) {
        User payer = paymentClient.getUserByMail(payment.getPayerid());
        User payee = paymentClient.getUserByMail(payment.getPayeeid());
        PaymentMethod paymentMethod = paymentClient.getPaymentMethodByName(payment.getPaymentmethodid());
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
        ResponseEntity<Payment> responseEntity = new ResponseEntity<Payment>(paymentClient.addPayment(payment), HttpStatus.CREATED);
        if(responseEntity.getStatusCode()==HttpStatus.CREATED) {
            return paymentClient.validateIfPaymentSuccessful(payment.getPaymentid())==true?"result":"error";
        } else {
            return "error";
        }
    }
}
