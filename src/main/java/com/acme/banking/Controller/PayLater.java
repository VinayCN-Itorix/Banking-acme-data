package com.acme.banking.Controller;

import com.acme.banking.Models.PayLater.*;
import io.apiwiz.compliance.config.EnableCompliance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@EnableCompliance
@RestController
@RequestMapping("v1/pay-later")
public class PayLater {

@Value("${host}")
private String host;
@Value("${api.validateProducts:null}")
private String validateProducts;

@Value("${api.validateCustomer:null}")
private String validateCustomer;

@Value("${api.initiateTransaction:null}")
private String initiateTransaction;

@Value("${api.pay.authenticate:null}")
private String authenticate;

@Value("${api.paymentPlan:null}")
private String paymentPlan;

@Value("${api.confirmOrder:null}")
private String confirmOrder;

@Value("${api.updateOrderStatus:null}")
private String updateOrderStatus;

@Autowired
private RestTemplate restTemplate;

@PostMapping("/validate/products")
public ResponseEntity<?> validateProducts(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                          @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                          @RequestBody(required = false) LoanEligibilityRequest loanEligibilityRequest) throws URISyntaxException {
    LoanEligibilityResponse loanEligibilityResponse = new LoanEligibilityResponse();
    loanEligibilityResponse.setProducts(loanEligibilityRequest.getProducts());
    loanEligibilityResponse.setEligible(true);
    if (enableTracing) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing", String.valueOf(enableTracing));
        headers.add("deviateResponse", String.valueOf(deviateResponse));
        URI validateCustomerUri = new URI(String.format(validateCustomer, loanEligibilityRequest.getCustomerId()));
        HttpEntity<LoanEligibilityResponse> httpEntity = new HttpEntity<>(loanEligibilityResponse, headers);
        OrderStatusResponse customerResponse = restTemplate.exchange(validateCustomerUri, HttpMethod.GET, httpEntity, OrderStatusResponse.class).getBody();
        return new ResponseEntity<>(customerResponse, HttpStatus.OK);
    }
    return new ResponseEntity<>(loanEligibilityResponse, HttpStatus.OK);
}

@GetMapping("/customer/{customerId}")
public ResponseEntity<?> validateCustomer(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                          @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                          @PathVariable(value = "customerId") String customerId) throws URISyntaxException {
    CustomerEligibleResponse customerEligibleResponse = new CustomerEligibleResponse();
    customerEligibleResponse.setCustomerId(customerId);
    customerEligibleResponse.setCreditScore("700");
    customerEligibleResponse.setEligible(true);
    customerEligibleResponse.setApprovedLimit(550.49);
    if(enableTracing){
        HttpHeaders headers = getHttpHeaders(enableTracing, deviateResponse);
        headers.add("Content-Type","application/json");
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setCustomerId(customerId);
        transactionRequest.setAmount(500);
        HttpEntity<TransactionRequest> transactionEntity = new HttpEntity<>(transactionRequest, headers);
        URI initiateTransactionUri = new URI(initiateTransaction);
        OrderStatusResponse transactionResponse = restTemplate.exchange(initiateTransactionUri, HttpMethod.POST, transactionEntity, OrderStatusResponse.class).getBody();
        return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
    }
    return new ResponseEntity<>(customerEligibleResponse, HttpStatus.OK);
}

private HttpHeaders getHttpHeaders(boolean enableTracing, boolean deviateResponse) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("enableTracing", String.valueOf(enableTracing));
    headers.add("deviateResponse", String.valueOf(deviateResponse));
    return headers;
}

@PostMapping("/transaction/initiate")
public ResponseEntity<?> initiateTransaction(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                             @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                             @RequestBody(required = false) TransactionRequest transactionRequest) throws URISyntaxException {
    TransactionResponse transactionResponse = new TransactionResponse();
    transactionResponse.setTransactionId(String.valueOf(UUID.randomUUID()));
    transactionResponse.setStatus("pending");
    transactionResponse.setRedirectUrl(String.format("https://,%s,/auth/authenticate?txn=,%s", host, transactionResponse.getTransactionId()));
    if(enableTracing){
        HttpHeaders headers = getHttpHeaders(enableTracing, deviateResponse);
        headers.add("Content-Type","application/json");
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setTransactionId(transactionResponse.getTransactionId());
        HttpEntity<AuthenticateRequest> authenticateEntity = new HttpEntity<>(authenticateRequest, headers);
        URI authenticateUri = new URI(authenticate);
        OrderStatusResponse authenticateResponse = restTemplate.exchange(authenticateUri, HttpMethod.POST, authenticateEntity, OrderStatusResponse.class).getBody();
        return new ResponseEntity<>(authenticateResponse, HttpStatus.OK);
    }
    return new ResponseEntity<>(transactionResponse, HttpStatus.OK);
}

@PostMapping("/auth/authenticate")
public ResponseEntity<?> authenticate(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                      @RequestBody(required = false) AuthenticateRequest authenticateRequest) throws URISyntaxException {
    AuthenticateResponse authenticateResponse = new AuthenticateResponse();
    authenticateResponse.setStatus("authenticated");
    authenticateResponse.setTransactionId(authenticateResponse.getTransactionId());
    authenticateResponse.setAuthorizationGranted(true);
    if(enableTracing){
        HttpHeaders headers = getHttpHeaders(enableTracing, deviateResponse);
        URI paymentPlanUri = new URI(String.format(paymentPlan, authenticateRequest.getTransactionId()));
        HttpEntity<AuthenticateResponse> paymentPlanEntity = new HttpEntity<>(headers);
        OrderStatusResponse orderStatusResponse=   restTemplate.exchange(paymentPlanUri, HttpMethod.POST, paymentPlanEntity, OrderStatusResponse.class).getBody();
        return new ResponseEntity<>(orderStatusResponse, HttpStatus.OK);
    }
    return new ResponseEntity<>(authenticateResponse, HttpStatus.OK);
}

@PostMapping("/payment/plan/{transactionId}")
public ResponseEntity<?> paymentPlan(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                     @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                     @PathVariable(value = "transactionId") String transactionId) throws URISyntaxException {
    PaymentPlanResponse paymentPlanResponse = new PaymentPlanResponse();
    PaymentPlan paymentPlan = new PaymentPlan();
    paymentPlan.setMonthlyPayment(167.5);
    paymentPlan.setInstallments(3);
    paymentPlan.setInterestRate(0.5);
    paymentPlanResponse.setPaymentPlan(paymentPlan);
    paymentPlanResponse.setTransactionId(transactionId);
    paymentPlanResponse.setStatus("approved");
    if(enableTracing){
        HttpHeaders headers = getHttpHeaders(enableTracing, deviateResponse);
        headers.add("Content-Type","application/json");
        OrderConfirmRequest orderConfirmRequest = new OrderConfirmRequest();
        orderConfirmRequest.setOrderId(UUID.randomUUID().toString());
        HttpEntity<OrderConfirmRequest> confirmOrderEntity = new HttpEntity<>(orderConfirmRequest, headers);
        URI confirmOrderUri = new URI(confirmOrder);
        OrderStatusResponse orderStatusResponse = restTemplate.exchange(confirmOrderUri, HttpMethod.POST, confirmOrderEntity, OrderStatusResponse.class).getBody();
        return new ResponseEntity<>(orderStatusResponse, HttpStatus.OK);
    }
    return new ResponseEntity<>(paymentPlanResponse, HttpStatus.OK);
}

@PostMapping("/order/confirm")
public ResponseEntity<?> confirmOrder(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                      @RequestBody(required = false) OrderConfirmRequest orderConfirmRequest) throws URISyntaxException {
    OrderConfirmResponse orderConfirmResponse = new OrderConfirmResponse();
    orderConfirmResponse.setAcknowledgement(true);
    orderConfirmResponse.setOrderId(orderConfirmRequest.getOrderId());
    orderConfirmResponse.setStatus("ready for fulfillment");
    
    if(enableTracing){
        HttpHeaders headers = getHttpHeaders(enableTracing, deviateResponse);
        headers.add("Content-Type","application/json");
        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.setOrderId(orderConfirmRequest.getOrderId());
        orderStatusRequest.setStatus("ready for fulfillment");
        HttpEntity<OrderStatusRequest> updateOrderEntity = new HttpEntity<>(orderStatusRequest, headers);
        URI updateOrderStatusUri = new URI(updateOrderStatus);
        ResponseEntity<OrderStatusResponse> orderStatusResponse = restTemplate.exchange(updateOrderStatusUri, HttpMethod.PUT, updateOrderEntity, OrderStatusResponse.class);
        return new ResponseEntity<>(orderStatusResponse.getBody(), HttpStatus.OK);
    }
    return new ResponseEntity<>(orderConfirmResponse, HttpStatus.OK);
    
    
}

@PutMapping("/order/status")

public ResponseEntity<?> updateOrderStatus(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                           @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                           @RequestBody(required = false) OrderStatusRequest orderStatusRequest) {
    OrderStatusResponse orderStatusResponse = new OrderStatusResponse();
    orderStatusResponse.setAcknowledgement(true);
    orderStatusResponse.setStatus("fulfilled");
    orderStatusResponse.setOrderId(orderStatusRequest.getOrderId());
    return new ResponseEntity<>(orderStatusResponse, HttpStatus.OK);
}
}
