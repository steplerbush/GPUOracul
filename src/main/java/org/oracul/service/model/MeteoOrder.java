package org.oracul.service.model;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.InternalServiceError;
import org.oracul.service.exceptions.QueueOverflowException;

import java.io.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by Miramax on 22.12.2015.
 */
public class MeteoOrder extends Order {

    private LocalDateTime requestTime;

    private static final Logger LOG = Logger.getLogger(MeteoOrder.class);

    public MeteoOrder(/*Map<String,String> params,*/ Double expectedWorkload, Long executionTime, IntegrationFacade facade) {
        super(/*params,*/ expectedWorkload, executionTime, facade);
        id = UUID.randomUUID() /*UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")*/;
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
        callCalculation();
        createImageOrder();
    }

    private void callCalculation() {
        try {
            //This is hack for windows - we cannot run process directly (throws exeption 'Cant find program')
            // so we cannot wait for its execution using waitFor().
            String[] command = {"CMD", "/C", facade.getConstants().getMeteoOrderCommand(), getId().toString()};
            ProcessBuilder probuilder = new ProcessBuilder(command);
            probuilder.directory(new File(facade.getConstants().getMeteoOrderDir()));
            LOG.debug("!!!MeteoOrder #" + this.getId() + " started execution");
            Process process = probuilder.start();
            //
           // File meteocalc = new File(facade.getConstants().getMeteoOrderDir()+facade.getConstants().getImageOrderCommand());
           // ProcessBuilder processBuilder = new ProcessBuilder("imagecalc");
           // processBuilder.directory(new File("C:\\"));
            //Runtime rt = Runtime.getRuntime();
            //Process proc = rt.exec("cd C:\\gpuoracul\\");
            //rt.exec("imagecalc", new String[]{},meteocalc.getParentFile());
            //LOG.debug("File meteocalc " + meteocalc.getAbsolutePath()
            //        + " exists " + meteocalc.exists()
            //        + " can exe - " + meteocalc.canExecute());
            //processBuilder.directory(meteocalc.getParentFile());
            //processBuilder.command("imagecalc"/*, id.toString()*/);
            ////Process p = Runtime.getRuntime().exec(meteocalc.getAbsolutePath() + " " + id.toString());
            //LOG.debug("MeteoOrder #" + this.getId() + " prepared for execution. Starting with command: " + meteocalc.getAbsolutePath() + " " + id.toString());
            //Process process = processBuilder.start();
            process.waitFor();
            LOG.debug("!!!MeteoOrder #" + this.getId() + " finished execution");
            facade.getOrderProcessor().releaseProcessor(this);
        } catch (Exception e) {
            LOG.error("Error while processing meteo order " + getId(), e);
        }
    }

    private void createImageOrder() {
        if (new File(facade.getConstants().getImageOrderDir()
                + "OUT/" + getId() + "/out.dat").canRead()) {
            ImageOrder io = new ImageOrder(getId(),
                    facade.calcOrderWorkload(Constants.IMAGE_ORDER, facade.getConstants().getDefaultImageOrderTypeName()),
                    facade.calcOrderExecutionTime(Constants.IMAGE_ORDER, facade.getConstants().getDefaultImageOrderTypeName()),
                    facade);
            LOG.debug("ImageOrder " + io.getId() + " created and ready for queue");
            try {
                facade.getOrderStore().put(io);
                facade.getOrderQueue().putOrderWithWaiting(io);
            } catch (QueueOverflowException e) {
                LOG.error("Can't add image order to queue. GPU Service is overloaded");
                throw e;
            } catch (Exception e) {
                LOG.error(e);
                throw new InternalServiceError();
            }
        } else {
            throw new RuntimeException("can't find calculation results for order " + getId());
        }
    }
}
