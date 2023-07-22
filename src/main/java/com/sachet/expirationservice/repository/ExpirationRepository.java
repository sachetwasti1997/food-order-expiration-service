package com.sachet.expirationservice.repository;

import com.sachet.expirationservice.model.OrderCreatedEventModel;
import org.springframework.data.repository.CrudRepository;

public interface ExpirationRepository extends CrudRepository<OrderCreatedEventModel, String> {
}
