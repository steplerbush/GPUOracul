package org.oracul.service.controllers;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.InternalServiceError;
import org.oracul.service.exceptions.QueueOverflowException;
import org.oracul.service.logic.OrderQueue;
import org.oracul.service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 21.12.2015.
 */
@RestController
@RequestMapping("/predict")
public class RequestController {

    private static final Logger LOG = Logger.getLogger(RequestController.class);

    @Autowired
    private IntegrationFacade facade;

    @Autowired
    private OrderQueue queue;

    @RequestMapping(value = "/order/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    Object orderPredictionGraphic(HttpServletRequest request) throws InterruptedException {

        if (facade.getOrderQueue().size() + 2 >= facade.getOrderQueue().getMaxSize()) {
            LOG.debug("Queue is overloaded. Order is rejected.");
            throw new QueueOverflowException();
        }

        Map<String, String> calcParamsMap = facade.getParamsMap(Constants.METEO_ORDER, request.getParameterMap());
        Map<String, String> imageParamsMap = facade.getParamsMap(Constants.IMAGE_ORDER, request.getParameterMap());

        String calcOrderType = calcParamsMap.get(facade.getConstants().calcOrderTypeName);
        String imageOrderType = imageParamsMap.get(facade.getConstants().imageOrderTypeName);

        try {
            MeteoOrder meteoOrder = new MeteoOrder(/*calcParamsMap,*/
                    facade.calcOrderWorkload(Constants.METEO_ORDER, calcOrderType),
                    facade.calcOrderExecutionTime(Constants.METEO_ORDER, calcOrderType));

            facade.getOrderQueue().putOrder(meteoOrder);
            Map<String, Object> returnParams = new HashMap<>();
            returnParams.put("id", meteoOrder.getId());
            returnParams.put("timeToWait", queue.getFullTimeToExecute()
                    + facade.calcOrderExecutionTime(Constants.IMAGE_ORDER, imageOrderType));

            return returnParams;

        } catch (QueueOverflowException e) {
            LOG.error("GPU Service is overloaded");
            throw e;
        } catch (Exception e) {
            LOG.error(e);
            throw new InternalServiceError();
        }
    }

    @RequestMapping(value = "/getimage/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    Object getPredictionGraphic(@PathVariable("id") UUID id) throws InterruptedException {
        if (facade.getOrderStore().containsOrderID(id)) {
            ImageOrder io = (ImageOrder) facade.getOrderStore().peek(id);
            if (Order.Status.READY_FOR_PICKUP.equals(io.getStatus())) {
                return "IMAGE";
            } else if (Order.Status.IN_PROCESSING.equals(io.getStatus())) {
                return "";
            }
        }
        return null;
    }
}
