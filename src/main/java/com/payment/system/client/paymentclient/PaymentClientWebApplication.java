package com.payment.system.client.paymentclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class PaymentClientWebApplication {
	private static final Logger logger = LoggerFactory.getLogger(PaymentClientWebApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PaymentClientWebApplication.class, args);
	}
}
