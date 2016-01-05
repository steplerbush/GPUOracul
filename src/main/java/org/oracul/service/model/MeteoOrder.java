package org.oracul.service.model;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.InternalServiceError;
import org.oracul.service.exceptions.QueueOverflowException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by Miramax on 22.12.2015.
 */
public class MeteoOrder extends Order {

    private LocalDateTime requestTime;

    private static final Logger LOG = Logger.getLogger(MeteoOrder.class);

    public MeteoOrder(/*Map<String,String> params,*/ Double expectedWorkload, Long executionTime) {
        super(/*params,*/ expectedWorkload, executionTime);
        id = UUID.randomUUID();
        requestTime = LocalDateTime.now();
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    @Override
    public void run() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(facade.getConstants().meteoOrderCommand, id.toString());
            processBuilder.directory(new File(facade.getConstants().meteoOrderDir));
            LOG.debug("MeteoOrder #" + this.getId() + " prepared for execution. Stating...");
            Process start = processBuilder.start();
            start.waitFor();
            LOG.debug("MeteoOrder #" + this.getId() + " finished execution");
            facade.getOrderProcessor().releaseProcessor(this);
        } catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
        if (new File(facade.getConstants().meteoOrderDir + "/" + getId()).exists()) {
            ImageOrder io = new ImageOrder(getId(),
                    facade.calcOrderWorkload(Constants.IMAGE_ORDER, facade.getConstants().defaultImageOrderTypeName),
                    facade.calcOrderExecutionTime(Constants.IMAGE_ORDER, facade.getConstants().defaultImageOrderTypeName));
            try {
                facade.getOrderStore().put(io);
                facade.getOrderQueue().putOrder(io);
            } catch (QueueOverflowException e) {
                LOG.error("Cant add image order to queue. GPU Service is overloaded");
                throw e;
            } catch (Exception e) {
                LOG.error(e);
                throw new InternalServiceError();
            }
        }
    }
}
