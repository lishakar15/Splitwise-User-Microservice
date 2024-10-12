package com.splitwise.microservices.user_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name ="Activity-Service", url = "http://localhost:8082/")
public interface ActivityClient {

}
