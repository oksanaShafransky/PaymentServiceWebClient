package com.web.payment.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebPaymentSystemApplication {
	private static final Logger logger = LoggerFactory.getLogger(WebPaymentSystemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WebPaymentSystemApplication.class, args);
	}
}
