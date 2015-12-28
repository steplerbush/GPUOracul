package org.oracul.service.model;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.CalculateWorkLoadException;
import org.oracul.service.logic.OrderQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Constants constants;

    public Double calcMeteoOrderWorkload(String type) {
        if (type.equals(constants.defaultCalcOrderTypeName)) {
            return constants.defaultCalcOrderWorkload;
        }
        LOG.error("Can't determine calc order workload");
        throw new CalculateWorkLoadException();
    }

    public Double calcImageOrderWorkload(String type) {
        if (type.equals(constants.defaultImageOrderTypeName)) {
            return constants.defaultImageOrderWorkload;
        }
        LOG.error("Can't determine image order workload");
        throw new CalculateWorkLoadException();
    }

    public Long calcMeteoOrderExecutionTime(String type) {
        if (type.equals(constants.defaultCalcOrderTypeName)) {
            return constants.defaultTimeToExecuteCalcOrder;
        }
        LOG.error("Can't determine calc order execution time");
        throw new CalculateWorkLoadException();
    }

    public Long calcImageOrderExecutionTime(String type) {
        if (type.equals(constants.defaultImageOrderTypeName)) {
            return constants.defaultTimeToExecuteImageOrder;
        }
        LOG.error("Can't determine image order execution time");
        throw new CalculateWorkLoadException();
    }

    public OrderQueue getOrderQueue() {
        return orderQueue;
    }
}
