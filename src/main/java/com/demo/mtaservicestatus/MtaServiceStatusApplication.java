package com.demo.mtaservicestatus;

import com.demo.mtaservicestatus.domain.DataHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MtaServiceStatusApplication {
	public static void main(String[] args) {
		DataHolder.setServiceStartTime(System.currentTimeMillis()/1000);
		SpringApplication.run(MtaServiceStatusApplication.class, args);
	}

}
