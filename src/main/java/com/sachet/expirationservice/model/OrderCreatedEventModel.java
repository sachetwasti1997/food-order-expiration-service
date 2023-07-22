package com.sachet.expirationservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.concurrent.TimeUnit;

@RedisHash("order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@ToString
public class OrderCreatedEventModel {
    @Id
    @Indexed
    private String id;
    private String status;
    private String userId;
    @TimeToLive(unit=TimeUnit.MILLISECONDS)
    private Long expTime;
    private String expiresAt;
    private String menuId;
    private Double menuPrice;
}
