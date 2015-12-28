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
    public class OrderDual {
        private MeteoOrder meteoOrder;
        private ImageOrder imageOrder;

        private OrderDual(MeteoOrder meteoOrder, ImageOrder imageOrder) {
            this.imageOrder = imageOrder;
            this.meteoOrder = meteoOrder;
        }

        public MeteoOrder getMeteoOrder() {
            return meteoOrder;
        }

        public ImageOrder getImageOrder() {
            return imageOrder;
        }
    }

    private Map<UUID, OrderDual> orderStoreMap;

    public OrderStore() {
        orderStoreMap = new HashMap<>();
    }

    public void put(MeteoOrder meteoOrder, ImageOrder imageOrder) {
        orderStoreMap.put(meteoOrder.getId(), new OrderDual(meteoOrder,imageOrder));
    }

    public void poll(UUID id) {
        orderStoreMap.remove(id);
    }

    public MeteoOrder getMeteoOrder(UUID id) {
        return orderStoreMap.get(id).getMeteoOrder();
    }

    public ImageOrder getImageOrder(UUID id) {
        return orderStoreMap.get(id).getImageOrder();
    }
}
