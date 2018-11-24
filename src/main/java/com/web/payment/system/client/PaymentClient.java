package com.web.payment.system.client;

import com.payment.service.dto.beans.Payment;
import com.payment.service.dto.beans.PaymentMethod;
import com.payment.service.dto.beans.User;
import com.payment.service.dto.beans.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PaymentClient.class);
    @Autowired
    private RestOperations restOperations;
    private final String url;

    @Autowired
    public PaymentClient(@Value("${payment.service.url}") final String url){
        this.url = url;
    }

    /**
     * The method returns method by provided method id.
     * @param paymentId
     * @return payment
     */
    public Payment getPayment(final String paymentId){
        return restOperations.getForObject(url+"/payment/"+paymentId, Payment.class, paymentId);
    }

    /**
     * The method returns user by provided mail.
     * @param usermail
     * @return user
     */
    public User getUserByMail(final String usermail){
        return restOperations.getForObject(url+"/user/mail/"+usermail, User.class, usermail);
    }

    /**
     * The method returns payment method by provided name
     * @param name
     * @return payment method
     */
    public PaymentMethod getPaymentMethodByName(final String name){
        return restOperations.getForObject(url+"/paymentmethod/name/"+name, PaymentMethod.class, name);
    }

    /**
     * The method returns user credentials by provided user id.
     * @param id
     * @return user credentials
     */
    public List<UserCredentials> getUserCredentialsByUserId(final String id){
        ResponseEntity<List<UserCredentials>> responseEntity = restOperations.exchange(url + "/credentials/userid/" + id, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<UserCredentials>>(){});
        return responseEntity.getBody();
    }

    /**
     * The method returns payment method by provided id.
     * @param id
     * @return payment method or null if not found.
     */
    public PaymentMethod getPaymentMethodById(final String id){
        return restOperations.getForObject(url+"/paymentmethod/"+id, PaymentMethod.class, id);
    }

    /**
     * The method returns all payments found on the db.
     * @return lst of payments.
     */
    public List<Payment> getAllPayments(){
        ResponseEntity<List<Payment>> response = restOperations.exchange(
                url+"/payment/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Payment>>(){});
        List<Payment> payments = response.getBody();
        return payments;
    }

    /**
     * The methods returns all user credentials found on the db.
     * @return list of user credentials.
     */
    public List<UserCredentials> getAllUserCredentials(){
        ResponseEntity<List<UserCredentials>> response = restOperations.exchange(
                url+"/credentials/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserCredentials>>(){});
        List<UserCredentials> credentials = response.getBody();
        return credentials;
    }

    /**
     * The method sends login request to the server with usermail and password
     * and verifies them match.
     * @param usermail
     * @param userpassword
     * @return user logged in in case of success or null if it failed
     */
    public User loginUser(String usermail, String userpassword){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<User> response = restOperations.exchange(
                url + "/user/login/" + usermail + "/ " + userpassword,
                HttpMethod.GET,
                entity,
                User.class);
        return response.getBody();
    }

    /**
     * The method sends payment request to be added to the kafka queue on the server.
     * @param payment
     * @return payment executed
     */
    public Payment addPayment(Payment payment){
        Map requestBody = new HashMap();
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

    /**
     * The method performs this validation asynchronously,
     * wait till the payment was inserted into the db by the risk engine.
     * If the payment returns not null, it means it was found on the db and the payment
     * perfomed successfully, otherwise, it was failed.
     */
    @Async
    public CompletableFuture<Payment> validateIfPaymentSuccessful(String paymentid){
        try {
            logger.debug("going to sleep 1000 millis to wait the payment process will be completed...");
            Thread.sleep(1000);
        } catch (InterruptedException e){
            logger.error("Interrupted exception caught during sleep on validateIfPaymentSuccessful: {}", e.getMessage());
        }
        Payment payment = restOperations.getForObject(url+"/payment/"+paymentid, Payment.class, paymentid);
        return CompletableFuture.completedFuture(payment);
    }

    /**
     * The method returns all users found on the db.
     * @return list of users
     */
    public List<User> getAllUsersList() {
        ResponseEntity<List<User>> response = restOperations.exchange(
                url + "/user/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                });
        List<User> users = response.getBody();
        logger.info("getAllUsersList response: {}", response);
        return users;
    }

    /**
     * The method returns all payment methods found on the db.
     * @return list of paymentmethods
     */
    public List<PaymentMethod> getAllPaymenMethods() {
        ResponseEntity<List<PaymentMethod>> response = restOperations.exchange(
                url + "/paymentmethod/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<PaymentMethod>>() {
                });
        List<PaymentMethod> paymentMethods = response.getBody();
        logger.info("getAllPaymentMethods response: {}", response);
        return paymentMethods;
    }
}
