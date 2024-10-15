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
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("v1")
@EnableCompliance
public class BankLoan {

@Value("${api.loan:null}")
private String loan ;

@Value("${api.creditScore:null}")
private String creditScore ;

@Value("${api.income:null}")
private String income ;

@Value("${api.risk:null}")
private String risk ;

@Value("${api.notification:null}")
private String notification ;


@Autowired
private ObjectMapper objectMapper;
@Autowired
private RestTemplate restTemplate;
@PostMapping("/authenticate")
public ResponseEntity<?> authenticate(@RequestHeader (value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader (value = "deviateResponse",required = false) boolean deviateResponse,
                                      @RequestBody(required = false) Map<String,Object> inputData) throws Exception {
    String customerId = (String) inputData.get("customerId");
    if(enableTracing) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(headers);
        String url = String.format(loan,customerId);
        restTemplate.exchange(new URI(url), HttpMethod.POST, httpEntity, Object.class);
    }
    String data = """
                   {
                   "valid":true,
                   "token":"12wefggftyugf56789okh823oa"
                   }
                   """;
    if(deviateResponse){
        String errorData = """
                   {
                   "valid":false,
                   "error":"invalid credentials"
                   }
                   """;
        Map<String,Object>  map = objectMapper.readValue(errorData, Map.class);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    Map<String,Object>  map = objectMapper.readValue(data, Map.class);
    return new ResponseEntity<>(map, HttpStatus.OK);
}
@PostMapping("/loans/approve/{applicationId}")
public ResponseEntity<?> applyForLoan(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                      @PathVariable (value = "applicationId") String applicationId) throws JsonProcessingException, URISyntaxException {
    
    if(enableTracing){
        Map<String,Object> map = null;
        String errorData = """
                                {
                       "status": "Rejected",
                       "AccountNo":"US39RABO1284418839"
                               }
                               """;
        String data = """
                       {
                       "status": "UnderVerification",
                       "AccountNo":"US39RABO1284418839"
                       }
                       """;
        String SSN = String.valueOf(UUID.randomUUID());
        if(deviateResponse){
            map = objectMapper.readValue(errorData,Map.class);
        }else{
            map = objectMapper.readValue(data,Map.class);
            map.put("applicationId",applicationId);
            map.put("SSN",SSN);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(headers);
        String url = String.format(creditScore,SSN);
        restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, Object.class);
    }
    if (deviateResponse) {
        String errorData = """
                               {
                               "error": "Loan application submission failed",
                               "details": "Unable to process your loan application"
                               }
                               """;
        Map<String, Object> errorMap = objectMapper.readValue(errorData, Map.class);
        return new ResponseEntity<>(errorMap, HttpStatus.OK);
    }
    
    String data = """
                       {
                       "applicationId": "LO345XE1",
                       "status": "UnderVerification",
                       "AccountNo":"US39RABO1284418839",
                       "SSN":"615-83-5066"
                       }
                       """;
    Map<String, Object> map = objectMapper.readValue(data, Map.class);
    return new ResponseEntity<>(map, HttpStatus.OK);
}

@GetMapping("/credit-score/{SSN}")
public ResponseEntity<?> fetchCreditScore(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                          @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                          @PathVariable (value = "SSN") String SSN) throws JsonProcessingException, URISyntaxException {
    
    if(enableTracing){
        Map<String,Object> map = null;
        String errorData = """
                               {
                               "creditScore": "500"
                        
                               }
                               """;
        String data = """
                       {
                       "creditScore": 800
                       }
                       """;
        if(deviateResponse){
            map = objectMapper.readValue(errorData,Map.class);
        }else{
            map = objectMapper.readValue(data,Map.class);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(headers);
        String url = String.format(income,SSN);
        restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, Object.class);
    }
    if (deviateResponse) {
        String errorData = """
                               {
                               "creditScore": "500"
                               }
                               """;
        Map<String, Object> errorMap = objectMapper.readValue(errorData, Map.class);
        return new ResponseEntity<>(errorMap, HttpStatus.OK);
    }

    String data = """
                       {
                       "creditScore": 800
                       }
                       """;
    Map<String, Object> map = objectMapper.readValue(data, Map.class);
    map.put("SSN",SSN);
    return new ResponseEntity<>(map, HttpStatus.OK);
}

@GetMapping("/income-verification/{SSN}")
public ResponseEntity<?> verifyIncome(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                      @PathVariable (value = "SSN") String SSN) throws JsonProcessingException, URISyntaxException {
    
    if(enableTracing){
        Map<String,Object> map = null;
        String errorData = """
                               {
                       "annualIncome": 80000,
                       "incomeSource": "Employment",
                       "employerDetails": {
                           "employerName": "ABC Corp",
                           "employerContact": "hr@abccorp.com"
                       },
                       "creditScore": 500
                       }
                               """;
        String data = """
                               {
                          
                       "annualIncome": 120000,
                       "incomeSource": "Employment",
                       "employerDetails": {
                           "employerName": "ABC Corp",
                           "employerContact": "hr@abccorp.com"
                       },
                       "creditScore": 800
                       }
                               """;
        if(deviateResponse){
            map = objectMapper.readValue(errorData,Map.class);
        }else{
            map = objectMapper.readValue(data,Map.class);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        restTemplate.exchange(new URI(risk), HttpMethod.POST, httpEntity, Object.class);
    }
    if (deviateResponse) {
        String errorData = """
                               {
                       "annualIncome": 80000,
                       "incomeSource": "Employment",
                       "employerDetails": {
                           "employerName": "ABC Corp",
                           "employerContact": "hr@abccorp.com"
                       }
                       }
                               """;
        Map<String, Object> errorMap = objectMapper.readValue(errorData, Map.class);
        errorMap.put("SSN",SSN);
        return new ResponseEntity<>(errorMap, HttpStatus.OK);
    }
    String data = """
                               {
                          
                       "annualIncome": 120000,
                       "incomeSource": "Employment",
                       "employerDetails": {
                           "employerName": "ABC Corp",
                           "employerContact": "hr@abccorp.com"
                       }
                       }
                               """;
    Map<String, Object> map = objectMapper.readValue(data, Map.class);
    map.put("SSN",SSN);
    return new ResponseEntity<>(map, HttpStatus.OK);
}

@PostMapping("/risk-assessment")
public ResponseEntity<?> assessRisk(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                    @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                    @RequestBody Map<String, Object> riskData) throws JsonProcessingException, URISyntaxException {
    if(enableTracing){
        Map<String,Object> map = null;
        String errorData = """
                       {
                        "Approved":false
                       }
                       """;
        String data =  """
                       {
                       "Approved":true
                       }
                       """;
        if(deviateResponse){
            map = objectMapper.readValue(errorData,Map.class);
        }else{
            map = objectMapper.readValue(data,Map.class);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("enableTracing",String.valueOf(Boolean.TRUE));
        headers.add("deviateResponse",String.valueOf(deviateResponse));
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        restTemplate.exchange(new URI(notification), HttpMethod.POST, httpEntity, Object.class);
    }
    if (deviateResponse) {
        String data = """
                       {
                       "riskScore": 90,
                       "riskCategory": "High",
                        "Approved":false
                       }
                       """;
        Map<String, Object> errorMap = objectMapper.readValue(data, Map.class);
        return new ResponseEntity<>(errorMap, HttpStatus.OK);
    }
    
    String data = """
                       {
                       "riskScore": 20,
                       "riskCategory": "Low",
                       "recommendation": "Approved",
                       "conditions": [
                           "Interest rate at 3.5%",
                           "Tenure 24 Months"
                       ],
                       "Approved":true
                       }
                       """;
    Map<String, Object> map = objectMapper.readValue(data, Map.class);
    return new ResponseEntity<>(map, HttpStatus.OK);
}

@PostMapping("/notifications")
public ResponseEntity<?> sendNotification(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                          @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                          @RequestBody Map<String, Object> notificationData) throws JsonProcessingException {
    if (deviateResponse) {
        String errorData = """
                               {
                                "status": "Failed"
                               }
                               """;
        Map<String, Object> errorMap = objectMapper.readValue(errorData, Map.class);
        return new ResponseEntity<>(errorMap, HttpStatus.OK);
    }
    String data = """
                       {
                       "status": "Sent"
                       }
                       """;
    Map<String, Object> map = objectMapper.readValue(data, Map.class);
    return new ResponseEntity<>(map, HttpStatus.OK);
}
@PostMapping("/loan/reject/{applicationId}")
public ResponseEntity<?> rejectLoan(@RequestHeader(value = "enableTracing", required = false) boolean enableTracing,
                                      @RequestHeader(value = "deviateResponse", required = false) boolean deviateResponse,
                                      @PathVariable (value = "applicationId") String applicationId) throws JsonProcessingException{
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
}

}
