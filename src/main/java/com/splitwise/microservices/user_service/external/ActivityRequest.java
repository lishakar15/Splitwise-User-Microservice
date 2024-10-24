package com.splitwise.microservices.user_service.external;

import com.splitwise.microservices.user_service.enums.ActivityType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityRequest {
    private  Activity activity;
    private List<Long> userIds;
}
