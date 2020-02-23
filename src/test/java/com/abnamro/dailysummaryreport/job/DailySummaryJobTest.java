package com.abnamro.dailysummaryreport.job;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.abnamro.dailysummaryreport.model.Transactions;
import com.abnamro.dailysummaryreport.model.TransactionsSummary;
import com.abnamro.dailysummaryreport.processor.TransactionProcessor;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.test.*;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@RunWith(SpringRunner.class)
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {DailySummaryJob.class, TransactionProcessor.class, JobLauncherTestUtils.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DailySummaryJobTest {

    private static final String TEST_OUTPUT = "src/test/resources/output/actual-output.csv";

    private static final String EXPECTED_OUTPUT = "src/test/resources/output/expected-output.csv";

    private static final String EXPECTED_OUTPUT_ONE = "src/test/resources/output/expected-output-one.csv";

    private static final String TEST_INPUT = "input/test-input.txt";

    public static final String FILE_NAME_CONTEXT_KEY = "fileName";

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private FlatFileItemReader<Transactions> itemReader;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private FlatFileItemWriter<TransactionsSummary> writer;

    @After
    public void cleanUp() {
        jobRepositoryTestUtils.removeJobExecutions();
    }


    private JobParameters defaultJobParameters() {
        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
        paramsBuilder.addString(FILE_NAME_CONTEXT_KEY, TEST_INPUT);
        paramsBuilder.addString("file.output", TEST_OUTPUT);
        paramsBuilder.addDate("date", new Date());
        return paramsBuilder.toJobParameters();

    }

    @Test
    public void givenReferenceOutput_whenJobExecuted_thenSuccess() throws Exception {
        // given
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(defaultJobParameters());
        JobInstance actualJobInstance = jobExecution.getJobInstance();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualJobInstance.getJobName(), is("dailySummaryReport"));
        assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @Test
    public void givenReferenceOutput_whenStep1Executed_thenSuccess() throws Exception {

        // given
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT);
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("step1", defaultJobParameters());
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

        // then
        assertThat(actualStepExecutions.size(), is(1));
        assertThat(actualJobExitStatus.getExitCode(), is("COMPLETED"));
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

    @Test
    public void givenMockedStep_whenReaderCalled_thenSuccess() throws Exception {

        // given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(defaultJobParameters());

        // when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            Transactions transactions;
            itemReader.open(stepExecution.getExecutionContext());
            while ((transactions = itemReader.read()) != null) {
                // then
                assertThat(transactions.getAccountNumber(), is("0002"));
                assertThat(transactions.getClientType(), is("CL"));
                assertThat(transactions.getClientNumber(), is("4321"));
                assertThat(transactions.getQuantityLong(), is(BigDecimal.ONE));
                assertThat(transactions.getQuantityShort(), is(BigDecimal.ZERO));
                assertThat(transactions.getExpiryDate(), is("20100910"));
                assertThat(transactions.getSymbol(), is("NK"));
                assertThat(transactions.getExchangeCode(), is("FU"));
                assertThat(transactions.getProductGroupCode(), is("SGX"));
            }
            itemReader.close();
            return null;
        });
    }

    @Test
    public void testTransactionProcessor() throws Exception {
        Transactions transactions= new Transactions();
        transactions.setQuantityShort(BigDecimal.ZERO);
        transactions.setQuantityLong(BigDecimal.ONE);
        transactions.setClientType("CL");
        transactions.setClientNumber("4321");
        transactions.setAccountNumber("0002");
        transactions.setSubAccountNumber("0001");
        transactions.setExpiryDate("20100910");
        transactions.setProductGroupCode("SGX");
        transactions.setSymbol("NK");
        transactions.setExchangeCode("FU");
        TransactionsSummary transactionsSummary= transactionProcessor.process(transactions);

        assertThat(transactionsSummary.getClientInformation(), is("CL432100020001"));
        assertThat(transactionsSummary.getProductInformation(), is("FUSGXNK20100910"));
        assertThat(transactionsSummary.getTotalTransactionAmount(), is(BigDecimal.ONE));


    }

    @Test
    public void givenMockedStep_whenWriterCalled_thenSuccess() throws Exception {

        // given
        FileSystemResource expectedResult = new FileSystemResource(EXPECTED_OUTPUT_ONE);
        FileSystemResource actualResult = new FileSystemResource(TEST_OUTPUT);
        TransactionsSummary transactionsSummary = new TransactionsSummary();
        transactionsSummary.setClientInformation("CL432100020001");
        transactionsSummary.setProductInformation("FUSGXNK20100910");
        transactionsSummary.setTotalTransactionAmount(BigDecimal.ONE);

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(defaultJobParameters());

        // when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {

            writer.open(stepExecution.getExecutionContext());
            writer.write(Arrays.asList(transactionsSummary));
            writer.close();
            return null;
        });

        // then
        AssertFile.assertFileEquals(expectedResult, actualResult);
    }

}
