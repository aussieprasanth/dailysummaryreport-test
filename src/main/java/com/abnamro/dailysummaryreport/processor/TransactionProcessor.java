package com.abnamro.dailysummaryreport.processor;

import com.abnamro.dailysummaryreport.controller.LoadController;
import com.abnamro.dailysummaryreport.model.Transactions;
import com.abnamro.dailysummaryreport.model.TransactionsSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionProcessor implements ItemProcessor<Transactions, TransactionsSummary> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProcessor.class);

    @Override
    public TransactionsSummary process(Transactions transactions) throws Exception {

        TransactionsSummary transactionsSummary = new TransactionsSummary();
        transactionsSummary.setClientInformation(transactions.getClientType() + transactions.getClientNumber() + transactions.getAccountNumber() + transactions.getSubAccountNumber());
        transactionsSummary.setProductInformation(transactions.getExchangeCode() + transactions.getProductGroupCode() + transactions.getSymbol() + transactions.getExpiryDate());
        transactionsSummary.setTotalTransactionAmount(transactions.getQuantityLong().add(transactions.getQuantityShort()));

        LOGGER.info("inside processor " + transactionsSummary.toString());
        return transactionsSummary;
    }
}
