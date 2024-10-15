package com.acme.banking.Models.PayLater;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentPlanResponse {
private String transactionId;
private String status;
private PaymentPlan paymentPlan;
private OrderConfirmRequest orderConfirmRequest;

}
