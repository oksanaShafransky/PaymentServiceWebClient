package com.payment.system.client.paymentclient.client;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.beans.PaymentMethod;
import com.payment.service.dto.beans.User;
import com.payment.service.dto.beans.UserCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class PaymentClient {
    @Autowired
    private RestOperations restOperations;
    private final String url;

    @Autowired
    public PaymentClient(@Value("${payment.service.url}") final String url){
        this.url = url;

    }

    public Payment getPayment(final String paymentId){
        return restOperations.getForObject(url+"/payment/"+paymentId, Payment.class, paymentId);
    }

    public User getUserByMail(final String usermail){
        return restOperations.getForObject(url+"/user/mail/"+usermail, User.class, usermail);
    }

    public PaymentMethod getPaymentMethodByName(final String name){
        return restOperations.getForObject(url+"/paymentmethod/name/"+name, PaymentMethod.class, name);
    }

    public PaymentMethod getPaymentMethodById(final String id){
        return restOperations.getForObject(url+"/paymentmethod/"+id, PaymentMethod.class, id);
    }

    public List<UserCredentials> getUserCredentialsByUserIdAndMethod(final String id, final String method){
        ResponseEntity<List<UserCredentials>> responseEntity = restOperations.exchange(url + "/credentials/user/" + id, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserCredentials>>(){});
        return responseEntity.getBody();
    }

    public List<Payment> getAllPayments(){
        ResponseEntity<List<Payment>> response = restOperations.exchange(
                url+"/payment/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Payment>>(){});
        List<Payment> payments = response.getBody();
        return payments;
    }

    public Payment addPayment(Payment payment){
        Map requestBody = new HashMap();
        requestBody.put("paymentid", payment.getPaymentid());
        requestBody.put("payerid", payment.getPayerid());
        requestBody.put("payeeid", payment.getPayeeid());
        requestBody.put("paymentdescription", payment.getPaymentdescription());
        requestBody.put("paymentmethodid", payment.getPaymentmethodid());
        requestBody.put("amount", payment.getAmount());
        requestBody.put("currency", payment.getCurrency());
        requestBody.put("paymentnumber", payment.getPaymentnumber());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Payment> request = new HttpEntity<>(payment, headers);
        return restOperations.postForEntity(url+"/payment/add_queue", request, Payment.class).getBody();
    }

    //perform this validation asynchroniously, wait till the payment was inserted into the db
    @Async
    public CompletableFuture<Payment> validateIfPaymentSuccessful(String paymentid){
        Payment payment = restOperations.getForObject(url+"/payment/"+paymentid, Payment.class, paymentid);
        return CompletableFuture.completedFuture(payment);
    }

    public List<User> getAllUsersList() {
        ResponseEntity<List<User>> response = restOperations.exchange(
                url + "/user/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                });
        List<User> users = response.getBody();
        return users;
    }

    public List<PaymentMethod> getAllPaymenMethods() {
        ResponseEntity<List<PaymentMethod>> response = restOperations.exchange(
                url + "/paymentmethod/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaymentMethod>>() {
                });
        List<PaymentMethod> paymentMethods = response.getBody();
        return paymentMethods;
    }
}
