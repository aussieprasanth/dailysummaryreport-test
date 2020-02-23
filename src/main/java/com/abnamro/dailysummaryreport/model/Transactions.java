package com.abnamro.dailysummaryreport.model;

import java.math.BigDecimal;

public class Transactions {

    private String clientType;
    private String clientNumber;
    private String accountNumber;
    private String subAccountNumber;

    private String productGroupCode;
    private String exchangeCode;
    private String symbol;
    private String expiryDate;

    private BigDecimal quantityLong;
    private BigDecimal quantityShort;


    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    public String getProductGroupCode() {
        return productGroupCode;
    }

    public void setProductGroupCode(String productGroupCode) {
        this.productGroupCode = productGroupCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }


    public BigDecimal getQuantityLong() {
        return quantityLong;
    }

    public void setQuantityLong(BigDecimal quantityLong) {
        this.quantityLong = quantityLong;
    }


    public BigDecimal getQuantityShort() {
        return quantityShort;
    }

    public void setQuantityShort(BigDecimal quantityShort) {
        this.quantityShort = quantityShort;
    }


}
