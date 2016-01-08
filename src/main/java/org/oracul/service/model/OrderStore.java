package org.oracul.service.model;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 28.12.2015.
 */
@Service
public class OrderStore {

    private Map<UUID, Order> orderStoreMap;

    public OrderStore() {
        orderStoreMap = new HashMap<>();
    }

    public void put(Order order) {
        orderStoreMap.put(order.getId(), order);
    }

    public Order remove(UUID id) {
        return orderStoreMap.remove(id);
    }

    public Order peek(UUID id) {
        return orderStoreMap.get(id);
    }

    public boolean containsOrderID(UUID id) {
        return orderStoreMap.containsKey(id);
    }
}
