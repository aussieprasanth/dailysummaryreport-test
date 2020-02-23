package com.abnamro.dailysummaryreport.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/load")
public class LoadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadController.class);
    private static final String FILE_NAME_CONTEXT_KEY = "fileName";

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;


    @GetMapping
    public BatchStatus load() throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString(FILE_NAME_CONTEXT_KEY, "input.txt");
        jobParametersBuilder.addDate("date", new Date(), true);
        JobExecution jobExecution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());

        LOGGER.info("JobExecution: " + jobExecution.getStatus());

        LOGGER.info("Batch is Running...");
        while (jobExecution.isRunning()) {
            LOGGER.info("...");
        }

        return jobExecution.getStatus();
    }


}
