package org.oracul.service.controllers;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.InternalServiceError;
import org.oracul.service.exceptions.QueueOverflowException;
import org.oracul.service.logic.OrderQueue;
import org.oracul.service.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @RequestMapping(value = "/order/", method = RequestMethod.GET, produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Map<String, Object> orderPrediction() throws InterruptedException {
        if (facade.getOrderQueue().size() + 1 >= facade.getOrderQueue().getMaxSize()) {
            LOG.debug("Queue is overloaded. Order is rejected.");
            throw new QueueOverflowException();
        }

        //Map<String, String> calcParamsMap = facade.getParamsMap(Constants.METEO_ORDER, request.getParameterMap());
        //Map<String, String> imageParamsMap = facade.getParamsMap(Constants.IMAGE_ORDER, request.getParameterMap());
        //LOG.debug("received calc params: " + calcParamsMap + "; image params: " + imageParamsMap);
        String calcOrderType = /*calcParamsMap.get(facade.getConstants().calcOrderTypeName);*/facade.getConstants().getDefaultCalcOrderTypeName();
        String imageOrderType = /*imageParamsMap.get(facade.getConstants().imageOrderTypeName);*/facade.getConstants().getDefaultImageOrderTypeName();
        //LOG.debug("received calc Order Type: " + calcOrderType + "; image Order Type: " + imageOrderType);

        try {
            MeteoOrder meteoOrder = new MeteoOrder(/*calcParamsMap,*/
                    facade.calcOrderWorkload(Constants.METEO_ORDER, facade.getConstants().getDefaultCalcOrderTypeName()),
                    facade.calcOrderExecutionTime(Constants.METEO_ORDER, facade.getConstants().getDefaultCalcOrderTypeName()),
                    facade);
            LOG.debug("MeteoOrder " + meteoOrder.getId() + " created and ready for queue");
            queue.putOrder(meteoOrder);
            Map<String, Object> returnParams = new HashMap<>();
            returnParams.put("id", meteoOrder.getId());
            returnParams.put("wait", queue.getFullTimeToExecute() + facade.calcOrderExecutionTime(Constants.IMAGE_ORDER, imageOrderType));
            return returnParams;

        } catch (QueueOverflowException e) {
            LOG.error("GPU Service is overloaded");
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            throw new InternalServiceError();
        }
    }

    @RequestMapping(value = "/isready/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getPredictionImage(@PathVariable("id") UUID id) throws InterruptedException {
        Map<String, Object> returnParams = new HashMap<>();
        if (facade.getOrderStore().containsOrderID(id)) {
            ImageOrder io = (ImageOrder) facade.getOrderStore().peek(id);
            if (Order.Status.READY_FOR_PICKUP.equals(io.getStatus())) {
                returnParams.put("status", "READY");
            } else if (Order.Status.IN_PROCESSING.equals(io.getStatus())) {
                returnParams.put("status", "IN_PROCESSING");
                returnParams.put("wait", io.getExecutionTime());
            } else if (Order.Status.IN_QUEUE.equals(io.getStatus())) {
                //needs more intelligent logic with retrieving time from all orders in queue starting with our order
                returnParams.put("status", "IN_QUEUE");
                returnParams.put("wait", io.getExecutionTime());
            } else if (Order.Status.READY_FOR_QUEUE.equals(io.getStatus())) {
                //maybe need to add retrieving time from processor
                returnParams.put("status", "READY_FOR_QUEUE");
                returnParams.put("wait", queue.getFullTimeToExecute()+io.getExecutionTime());
            }
        } else {
            returnParams.put("status","NOT_CREATED");
        }
        return returnParams;
    }

    @RequestMapping(value = "/getimage/{id}", method = RequestMethod.GET, produces = "image/jpg")
    public @ResponseBody byte[] getFile(@PathVariable("id") UUID id)  {
        File f = new File(((ImageOrder)facade.getOrderStore().peek(id)).getImageURL());
        LOG.debug("Getting image from root: " + f.getAbsolutePath());
        try (InputStream is = new FileInputStream(f);
                ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            BufferedImage img = ImageIO.read(is);
            //ByteArrayOutputStream bao = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", bao);
            byte[] image = bao.toByteArray();
            bao.flush();
            bao.close();
            return image;
        } catch (IOException e) {
            LOG.error("Error while making image response", e);
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/release/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Object removeData(@PathVariable("id") UUID id) {
        Map<String, String> m = new HashMap<>(1);
        try {
            String imagePath = ((ImageOrder) facade.getOrderStore().peek(id)).getImageURL();
            String dataPath = facade.getConstants().getMeteoOrderDir() + "OUT/" + id + "/out.dat";
            facade.getOrderStore().remove(id);
            File image = new File(imagePath);
            File data = new File(dataPath);
            Path fp = image.toPath();
            Files.delete(fp);
            Files.delete(data.toPath());
            Files.delete(data.getParentFile().toPath());
            if (!image.exists() && !data.getParentFile().exists())
                m.put("status", "SUCCESS");
            else
                m.put("status", "FAILED");
        } catch (Exception e) {
            LOG.debug("Error while removing data for order " + id, e);
        }
        return m;
    }
}
