package com.sadlier.sadliermq;

import com.sadlier.sadliermq.consumer.ServiceBReceiver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SadlierMqApplication {

	public static void main(String[] args) {
		ApplicationContext app = SpringApplication.run(SadlierMqApplication.class, args);
	}

}
