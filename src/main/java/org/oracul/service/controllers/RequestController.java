package org.oracul.service.controllers;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.InternalServiceError;
import org.oracul.service.exceptions.QueueOverflowException;
import org.oracul.service.logic.OrderQueue;
import org.oracul.service.model.Constants;
import org.oracul.service.model.ImageOrder;
import org.oracul.service.model.IntegrationFacade;
import org.oracul.service.model.MeteoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public @ResponseBody
    MeteoOrder orderPredictionGraphic(HttpServletRequest request) throws InterruptedException {

        if (facade.getOrderQueue().size() + 2 >= facade.getOrderQueue().getMaxSize()) {
            LOG.debug("Queue is overloaded. Order is rejected.");
            throw new QueueOverflowException();
        }

        Map<String, String> calcParamsMap = facade.getParamsMap(Constants.METEO_ORDER, request.getParameterMap());
        Map<String, String> imageParamsMap = facade.getParamsMap(Constants.IMAGE_ORDER, request.getParameterMap());

        String calcOrderType = calcParamsMap.get(facade.getConstants().calcOrderTypeName);
        String imageOrderType = imageParamsMap.get(facade.getConstants().imageOrderTypeName);

        try {
            MeteoOrder meteoOrder = new MeteoOrder(calcParamsMap,
                    facade.calcOrderWorkload(Constants.METEO_ORDER, calcOrderType),
                    facade.calcOrderExecutionTime(Constants.METEO_ORDER, calcOrderType));

            ImageOrder imageOrder = new ImageOrder(meteoOrder.getId(), imageParamsMap,
                    facade.calcOrderWorkload(Constants.IMAGE_ORDER, imageOrderType),
                    facade.calcOrderExecutionTime(Constants.IMAGE_ORDER, imageOrderType));

            facade.getOrderQueue().putOrder(meteoOrder);
            facade.getOrderQueue().putOrder(imageOrder);

            meteoOrder.setExpectedFullWaitTime(queue.getFullTimeToExecute() + facade.getOrderProcessor().getFullWorkLoad());

            return meteoOrder;

        } catch (QueueOverflowException e) {
            LOG.error("GPU Service is overloaded");
            throw e;
        }
        catch (Exception e) {
            LOG.error(e);
            throw new InternalServiceError();
        }

    }
}
