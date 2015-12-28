package org.oracul.service.controllers;

import org.oracul.service.logic.OrderQueue;
import org.oracul.service.model.ImageOrder;
import org.oracul.service.model.IntegrationFacade;
import org.oracul.service.model.MeteoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Miramax on 21.12.2015.
 */
@RestController
@RequestMapping("/predict")
public class RequestController {

    @Autowired
    private IntegrationFacade facade;

    @Autowired
    private OrderQueue queue;

    @RequestMapping(value = "/order/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    MeteoOrder orderPredictionGraphic(HttpServletRequest request) throws InterruptedException {
        MeteoOrder meteoOrder = new MeteoOrder(request.getParameterMap(),
                facade.calcMeteoOrderWorkload(request.getParameter("type")),
                facade.calcMeteoOrderExecutionTime(request.getParameter("type")));
        facade.getOrderQueue().putOrder(meteoOrder);
        ImageOrder iorder = new ImageOrder(meteoOrder.getId(), request.getParameter("type"),
                facade.calcImageOrderWorkload(request.getParameter("type")),
                facade.calcImageOrderExecutionTime(request.getParameter("type")));
        meteoOrder.setExpectedFullWaitTime(queue.getFullTimeToExecute() /*+ full time of OrderProcessor*/);
        return meteoOrder;
    }
}
