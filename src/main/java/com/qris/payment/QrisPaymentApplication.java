package com.qris.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QrisPaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(QrisPaymentApplication.class, args);
    }
}
