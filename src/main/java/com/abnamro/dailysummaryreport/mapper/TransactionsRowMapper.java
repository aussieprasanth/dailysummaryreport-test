package com.abnamro.dailysummaryreport.mapper;

import com.abnamro.dailysummaryreport.model.Transactions;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class TransactionsRowMapper implements FieldSetMapper<Transactions> {

    @Override
    public Transactions mapFieldSet(FieldSet fieldSet) {
        Transactions transactions = new Transactions();

        transactions.setClientType(fieldSet.readString("clientType"));
        transactions.setClientNumber(fieldSet.readString("clientNumber"));
        transactions.setAccountNumber(fieldSet.readString("accountNumber"));
        transactions.setSubAccountNumber(fieldSet.readString("subAccountNumber"));


        transactions.setProductGroupCode(fieldSet.readString("productGroupCode"));
        transactions.setExchangeCode(fieldSet.readString("exchangeCode"));
        transactions.setSymbol(fieldSet.readString("symbol"));
        transactions.setExpiryDate(fieldSet.readString("expiryDate"));

        transactions.setQuantityLong(fieldSet.readBigDecimal("quantityLong"));
        transactions.setQuantityShort(fieldSet.readBigDecimal("quantityShort"));

        return transactions;

    }
}
