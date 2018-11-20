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


/*	@Autowired
	private PaymentClient paymentClient;

	@Override
	public void run(String... args) throws Exception {
		Payment payment1 = paymentClient.getPayment("12345");
		logger.info("###############Response: {}", payment1);
		Payment payment2 = paymentClient.getPayment("54321");
		logger.info("###############Response: {}", payment2);
	}*/
}
