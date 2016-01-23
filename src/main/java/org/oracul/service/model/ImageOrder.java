package org.oracul.service.model;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 27.12.2015.
 */
public class ImageOrder extends Order{

    private static final Logger LOG = Logger.getLogger(MeteoOrder.class);

    private String imageURL;

    public ImageOrder(UUID id, /*Map<String,String> params,*/ Double expectedWorkload, Long executionTime, IntegrationFacade facade) {
        super(/*params,*/ expectedWorkload, executionTime, facade);
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public void run() {
        try {
            //File meteocalc = new File(facade.getConstants().getMeteoOrderDir()+facade.getConstants().getMeteoOrderCommand());
            String[] command = {"CMD", "/C", facade.getConstants().getImageOrderCommand(), getId().toString()};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(facade.getConstants().getImageOrderDir()));
            LOG.debug("ImageOrder #" + this.getId() + " prepared for execution. Starting.");
            Process process = processBuilder.start();
            process.waitFor();
            LOG.debug("ImageOrder #" + this.getId() + " finished execution");
            facade.getOrderProcessor().releaseProcessor(this);
            String url = facade.getConstants().getMeteoOrderDir() + "OUT_IMAGES/" + getId() + "." + facade.getConstants().getImageFormat();
            if (new File(url).canRead()) {
                setImageURL(url);
                setStatus(Status.READY_FOR_PICKUP);
            } else {
                throw new Exception("Image file was not created or cannot to be read");
            }
        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        }
    }
}
