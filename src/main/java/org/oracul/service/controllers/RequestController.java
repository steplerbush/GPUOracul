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
import java.io.File;
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
    public @ResponseBody Map<String, Object> orderPrediction(HttpServletRequest request) throws InterruptedException {

        if (facade.getOrderQueue().size() + 1 >= facade.getOrderQueue().getMaxSize()) {
            LOG.debug("Queue is overloaded. Order is rejected.");
            throw new QueueOverflowException();
        }

        //Map<String, String> calcParamsMap = facade.getParamsMap(Constants.METEO_ORDER, request.getParameterMap());
        //Map<String, String> imageParamsMap = facade.getParamsMap(Constants.IMAGE_ORDER, request.getParameterMap());
        //LOG.debug("received calc params: " + calcParamsMap + "; image params: " + imageParamsMap);
        String calcOrderType = /*calcParamsMap.get(facade.getConstants().calcOrderTypeName);*/facade.getConstants().defaultCalcOrderTypeName;
        String imageOrderType = /*imageParamsMap.get(facade.getConstants().imageOrderTypeName);*/facade.getConstants().defaultImageOrderTypeName;
        //LOG.debug("received calc Order Type: " + calcOrderType + "; image Order Type: " + imageOrderType);

        try {
            MeteoOrder meteoOrder = new MeteoOrder(/*calcParamsMap,*/
                    facade.calcOrderWorkload(Constants.METEO_ORDER, facade.getConstants().defaultCalcOrderTypeName),
                    facade.calcOrderExecutionTime(Constants.METEO_ORDER, facade.getConstants().defaultCalcOrderTypeName));
            /*if (LOG.isDebugEnabled())*/ LOG.debug("MeteoOrder " + meteoOrder.getId() +calcOrderType + " created and ready for queue");
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
    public Object getPredictionImage(@PathVariable("id") UUID id) throws InterruptedException {
        if (facade.getOrderStore().containsOrderID(id)) {
            ImageOrder io = (ImageOrder) facade.getOrderStore().peek(id);
            if (Order.Status.READY_FOR_PICKUP.equals(io.getStatus())) {
                return "IMAGE " + io.getImageURL();
            } else if (Order.Status.IN_PROCESSING.equals(io.getStatus())) {
                return "wait " + io.getExecutionTime();
            } else if (Order.Status.IN_QUEUE.equals(io.getStatus())) {
                //needs more intelligent logic with retrieving time from all orders in queue starting with our order
                return "wait " + io.getExecutionTime()*2;
            } else if (Order.Status.READY_FOR_QUEUE.equals(io.getStatus())) {
                //maybe need to add retrieving time from processor
                return "wait " + facade.getOrderQueue().getFullTimeToExecute() + io.getExecutionTime()*2;
            }
        }
        return null;
    }
}
