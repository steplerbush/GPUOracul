package org.oracul.service.model;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 27.12.2015.
 */
public class ImageOrder extends Order{

    private String imageURL;

    public ImageOrder(UUID id, Map<String,String> params, Double expectedWorkload, Long executionTime) {
        super(params, expectedWorkload, executionTime);
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
