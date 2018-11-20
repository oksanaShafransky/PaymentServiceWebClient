package com.payment.system.client.paymentclient.validation;

import com.payment.service.dto.beans.Payment;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PaymentValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Payment.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Payment payment = (Payment) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "required.amount");

        if ("NONE".equals(payment.getPayerid())) {
            errors.rejectValue("payerid", "required.payerid");
        }
        if ("NONE".equals(payment.getPayeeid())) {
            errors.rejectValue("payeeid", "required.payeeid");
        }
        if ("NONE".equals(payment.getPaymentmethodid())) {
            errors.rejectValue("paymentmethodid", "required.paymentmethodid");
        }
    }

}
