package org.oracul.service.model;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.CalculateWorkLoadException;
import org.oracul.service.logic.OrderProcessor;
import org.oracul.service.logic.OrderQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Miramax on 27.12.2015.
 */
@Service
public class IntegrationFacade {

    private static final Logger LOG = Logger.getLogger(IntegrationFacade.class);

    @Autowired
    private OrderQueue orderQueue;

    @Autowired
    private OrderStore orderStore;

    @Autowired
    private Constants constants;

    @Autowired
    private OrderProcessor orderProcessor;

    public Double calcOrderWorkload(String order, String type) {
        if (Constants.METEO_ORDER.equals(order)) {
            if (type.equals(getConstants().defaultCalcOrderTypeName)) {
                return getConstants().defaultCalcOrderWorkload;
            }
            LOG.error("Can't determine calc order workload");
        }
        else if (Constants.IMAGE_ORDER.equals(order)) {
            if (type.equals(getConstants().defaultImageOrderTypeName)) {
                return getConstants().defaultImageOrderWorkload;
            }
            LOG.error("Can't determine image order workload");
        }
        throw new CalculateWorkLoadException();
    }

    public Long calcOrderExecutionTime(String order, String type) {
        if (Constants.METEO_ORDER.equals(order)) {
            if (type.equals(getConstants().defaultCalcOrderTypeName)) {
                return getConstants().defaultTimeToExecuteCalcOrder;
            }
            LOG.error("Can't determine calc order execution time");
        }
        else if (Constants.IMAGE_ORDER.equals(order)) {
            if (type.equals(getConstants().defaultImageOrderTypeName)) {
                return getConstants().defaultTimeToExecuteImageOrder;
            }
            LOG.error("Can't determine image order execution time");
        }
        throw new CalculateWorkLoadException();
    }

    public Map<String,String> getParamsMap(String meteoOrder, Map<String, String[]> parameterMap) {
        Map<String, String> map = new HashMap<>();
        if (parameterMap != null && !parameterMap.isEmpty()) {
            for (String key : parameterMap.keySet()) {
                if (key.toLowerCase().contains(meteoOrder))
                    map.put(key, parameterMap.get(key)[0]);
            }
        }
        return map;
    }

    public OrderStore getOrderStore() {
        return orderStore;
    }

    public OrderQueue getOrderQueue() {
        return orderQueue;
    }

    public Constants getConstants() {
        return constants;
    }

    public OrderProcessor getOrderProcessor() {
        return orderProcessor;
    }
}
