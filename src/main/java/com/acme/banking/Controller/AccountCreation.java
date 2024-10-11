package com.acme.banking.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apiwiz.compliance.config.EnableCompliance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@EnableCompliance
@RequestMapping("v1")
public class AccountCreation {
@Value("${api.eKycVerification}")
private String eKycVerification;

@Value("${api.accounts}")
private String accounts;

@Value("${api.cardIssuance}")
private String cardIssuance;

@Value("${api.activation}")
private String activation;

@Value("${api.notification}")
private String notification;

@Autowired
private RestTemplate restTemplate;

@Autowired
private ObjectMapper objectMapper;

@PostMapping("/register")
public ResponseEntity<?> register(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                  @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                  @RequestBody (required = false)Map<String,Object> inputData ) throws JsonProcessingException, URISyntaxException {
    String Data= """
            {
              "status": "SUCCESS",
              "message": "Customer profile created successfully"
            }
            """;
    Map<String, Object> map = objectMapper.readValue(Data, Map.class);
    String customerId = "CUST12345";
    map.put( "customerId","CUST12345");
    if(enableTracing){
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        String url = String.format(eKycVerification,customerId);
        restTemplate.exchange(new URI(url), HttpMethod.POST, httpEntity, Object.class);
    }
    return new ResponseEntity<>(map, HttpStatus.CREATED);
}

@PostMapping("/e-kyc/{custId}")
public ResponseEntity<?> kyc(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                  @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                  @PathVariable (value = "custId") String custId) throws JsonProcessingException, URISyntaxException {
    
    String Data = """
            {
              "kycStatus": "VERIFIED",
              "message": "E-KYC verification successful"
            }
            """;
    Map<String, Object> map = objectMapper.readValue(Data, Map.class);
    map.put("CustomerId",custId);
    if(enableTracing){
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        String url = String.format(eKycVerification,custId);
        restTemplate.exchange(new URI(url), HttpMethod.POST, httpEntity, Object.class);
    }
    return new ResponseEntity<>(map,HttpStatus.OK);
}

@PostMapping("/account/{custId}")
public ResponseEntity<?> account(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                             @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                 @PathVariable (value = "custId") String custId) throws JsonProcessingException, URISyntaxException {
    
    String Data = """
            {
               "accountNumber": "1234567890",
               "accountType": "SAVINGS",
               "currency": "USD",
               "status": "ACTIVE",
               "message": "Bank account created successfully"
             }
            """;
    String errorData = """
            {
               "accountNumber": "1234567890",
               "accountType": "SAVINGS",
               "currency": "USD",
               "status": "INACTIVE",
               "message": "Bank account created successfully"
             }
            """;
    String accountNo ="1234567890";
    if(deviateResponse){
        Map<String, Object> map = objectMapper.readValue(errorData, Map.class);
        map.put("accountNumber","1234567890");
        if(enableTracing){
            HttpHeaders headers = new HttpHeaders();
            headers.add("enableTracing",String.valueOf(Boolean.TRUE));
            headers.add("deviateResponse",String.valueOf(deviateResponse));
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
            restTemplate.exchange(new URI(notification), HttpMethod.POST, httpEntity, Object.class);
        }
        return new ResponseEntity<>(map,HttpStatus.OK);
    }
    Map<String, Object> map = objectMapper.readValue(Data, Map.class);
    map.put("accountNumber","1234567890");
    if(enableTracing){
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        String url = String.format(cardIssuance,"1234567890");
        restTemplate.exchange(new URI(url), HttpMethod.POST, httpEntity, Object.class);
    }
    return new ResponseEntity<>(map,HttpStatus.OK);
}

@PostMapping("/card/issue-card/{accountId}")
public ResponseEntity<?> issueCard(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                   @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                   @PathVariable (value = "accountId") String accountId ) throws JsonProcessingException, URISyntaxException {
    String Data= """
            {
              "cardNumber": "4111111111111111",
              "expiryDate": "12/26",
              "status": "ISSUED",
              "message": "Debit card issued successfully"
            }
            """;
    String cardId = String.valueOf(UUID.randomUUID());
    Map<String, Object> map = objectMapper.readValue(Data, Map.class);
    map.put("CardId",cardId);
    if(enableTracing){
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        String url = String.format(activation,cardId);
        restTemplate.exchange(new URI(activation), HttpMethod.POST, httpEntity, Object.class);
    }
    return new ResponseEntity<>(map,HttpStatus.OK);
}

@PostMapping("/card/activate-card/{cardId}")
        public ResponseEntity<?> activateCard(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                              @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                              @PathVariable (value = "cardId") String cardId) throws JsonProcessingException, URISyntaxException {
    String Data = """
                 {
              "status": "ACTIVE",
              "message": "Card activated successfully"
            }
                """;
    Map<String, Object> map = objectMapper.readValue(Data, Map.class);
    map.put("cardId",cardId);
    if(enableTracing){
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("Approved",true);
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(headers);
        restTemplate.exchange(new URI(notification), HttpMethod.POST, httpEntity,Object.class);
    }
    return new ResponseEntity<>(map,HttpStatus.OK);
}
}

