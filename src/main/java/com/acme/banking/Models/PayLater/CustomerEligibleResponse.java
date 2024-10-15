package com.acme.banking.Models.PayLater;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerEligibleResponse {
private String customerId;
private boolean eligible;
private String creditScore;
private double approvedLimit;
private TransactionRequest transactionRequest;
}
