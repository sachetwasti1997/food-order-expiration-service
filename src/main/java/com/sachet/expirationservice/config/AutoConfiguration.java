package com.sachet.expirationservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sachet.expirationservice.model.OrderCancelledEvent;
import com.sachet.expirationservice.model.OrderCreatedEventModel;
import com.sachet.expirationservice.producer.OrderCancelledEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutoConfiguration {
    private final OrderCancelledEventPublisher orderCancelledEventPublisher;

    public AutoConfiguration(OrderCancelledEventPublisher orderCancelledEventPublisher) {
        this.orderCancelledEventPublisher = orderCancelledEventPublisher;
    }

    @EventListener
    public void cancelOrder(RedisKeyExpiredEvent<OrderCreatedEventModel> exp) throws JsonProcessingException {
        var orderCreatedEventModel = (OrderCreatedEventModel) exp.getValue();
        log.info("Expiring: {}", orderCreatedEventModel);
        assert orderCreatedEventModel != null;
        var expiredOrder = new OrderCancelledEvent(orderCreatedEventModel.getId(),
                orderCreatedEventModel.getMenuId());
        orderCancelledEventPublisher.sendOrderCancelEvent(expiredOrder);
    }
}
