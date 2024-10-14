package com.splitwise.microservices.user_service.clients;

import com.splitwise.microservices.user_service.external.ActivityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ORCHESTRATE-SERVICE")
public interface ActivityClient {

    @PostMapping("/activity/processActivityRequest")
    public void sendActivityRequest(ActivityRequest activityRequest);
}

