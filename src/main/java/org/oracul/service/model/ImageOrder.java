package org.oracul.service.model;

import java.util.UUID;

/**
 * Created by Miramax on 27.12.2015.
 */
public class ImageOrder extends Order{

    private String imageURL;

    private String type;

    public ImageOrder(UUID id, String type, Double expectedWorkload, Long executionTime) {
        super(expectedWorkload, executionTime);
        this.id = id;
        this.type = type;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getType() {
        return type;
    }

}
