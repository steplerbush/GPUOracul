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

    public ImageOrder(UUID id, /*Map<String,String> params,*/ Double expectedWorkload, Long executionTime) {
        super(/*params,*/ expectedWorkload, executionTime);
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
            ProcessBuilder processBuilder = new ProcessBuilder(facade.getConstants().imageOrderCommand, id.toString());
            processBuilder.directory(new File(facade.getConstants().imageOrderDir));
            LOG.debug("ImageOrder #" + this.getId() + " prepared for execution. Starting.");
            Process process = processBuilder.start();
            process.waitFor();
            LOG.debug("ImageOrder #" + this.getId() + " finished execution");
            facade.getOrderProcessor().releaseProcessor(this);
            String url = facade.getConstants().imageOrderDir + "/" + getId()+facade.getConstants().imageFormat;
            if (new File(url).canRead()) {
                setImageURL(url);
                setStatus(Status.READY_FOR_PICKUP);
            } else {
                throw new RuntimeException("Image file is not created / can't to be read");
            }
        } catch (IOException | InterruptedException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
