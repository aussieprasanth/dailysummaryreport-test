package com.abnamro.dailysummaryreport.job;

import com.abnamro.dailysummaryreport.header.DailySummaryReportHeaderCallBack;
import com.abnamro.dailysummaryreport.mapper.TransactionsRowMapper;
import com.abnamro.dailysummaryreport.model.Transactions;
import com.abnamro.dailysummaryreport.model.TransactionsSummary;
import com.abnamro.dailysummaryreport.processor.TransactionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
public class DailySummaryJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailySummaryJob.class);

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private TransactionProcessor transactionProcessor;
    private Resource outputResource = new FileSystemResource("output/outputData.csv");

    @Qualifier(value = "dailySummaryReport")
    @Bean
    public Job dailySummaryReportJob(JobBuilderFactory jobBuilderFactory,
                                     StepBuilderFactory stepBuilderFactory,
                                     ItemReader<Transactions> itemReader,
                                     TransactionProcessor transactionProcessor,
                                     ItemWriter<TransactionsSummary> itemWriter) throws Exception {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionProcessor = transactionProcessor;

        return this.jobBuilderFactory.get("dailySummaryReport")
                .incrementer(new RunIdIncrementer())
                .start(step1GenerateReport())
                .build();
    }

    @Bean
    public Step step1GenerateReport() throws Exception {
        return this.stepBuilderFactory.get("step1")
                .<Transactions, TransactionsSummary>chunk(100)
                .reader(readFixedWidthFile())
                .processor(transactionProcessor)
                .writer(transactionSummaryWriterDefault())
                .build();
    }

    @Bean
    @StepScope
    Resource inputFileResource(@Value("#{jobParameters[fileName]}") final String fileName) throws Exception {
        return new ClassPathResource(fileName);
    }

    @Bean
    @StepScope
    public FixedLengthTokenizer fixedLengthTokenizer() {
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();

        tokenizer.setNames("clientType", "clientNumber", "accountNumber", "subAccountNumber", "exchangeCode", "productGroupCode", "symbol",
                "expiryDate", "quantityLong", "quantityShort","openCloseCode");
        tokenizer.setColumns(new Range(4, 7),
                new Range(8, 11),
                new Range(12, 15),
                new Range(16, 19),
                new Range(26, 27),
                new Range(28, 31),
                new Range(32, 37),
                new Range(38, 45),
                new Range(53, 62),
                new Range(64, 73),
                new Range(176));
        return tokenizer;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Transactions> readFixedWidthFile() throws Exception {
        FlatFileItemReader<Transactions> reader = new FlatFileItemReader<>();
        reader.setResource(inputFileResource(null));
        reader.setLineMapper(new DefaultLineMapper<Transactions>() {{
            setLineTokenizer(fixedLengthTokenizer());
            setFieldSetMapper(new TransactionsRowMapper());
        }});
        return reader;
    }


    @Bean
    public FlatFileItemWriter<TransactionsSummary> transactionSummaryWriterDefault() {
        //Create writer instance
        FlatFileItemWriter<TransactionsSummary> writer = new FlatFileItemWriter<>();

        //Set output file location
        writer.setResource(outputResource);

        DailySummaryReportHeaderCallBack flatFileHeaderCallback = new DailySummaryReportHeaderCallBack("Client_Information,Product_Information,Total_Transaction_Amount");

        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);

        writer.setHeaderCallback(flatFileHeaderCallback);

        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<TransactionsSummary>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<TransactionsSummary>() {
                    {
                        setNames(new String[]{"clientInformation", "productInformation", "totalTransactionAmount"});
                    }
                });
            }
        });
        return writer;
    }


}
