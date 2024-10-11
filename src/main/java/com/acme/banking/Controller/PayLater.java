package com.acme.banking.Controller;

import io.apiwiz.compliance.config.EnableCompliance;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableCompliance
@RestController
@RequestMapping("v1/pay")
public class PayLater {

}
