package com.abnamro.dailysummaryreport;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class DailySummaryReportApplication {

	public static void main(String[] args) {

		SpringApplication.run(DailySummaryReportApplication.class, args);
	}

}
