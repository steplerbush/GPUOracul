package org.oracul.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by Miramax on 27.12.2015.
 */
@Service
public class Constants {

    @Value("${meteo.dir}")
    public String meteoOrderDir;
    @Value("${meteo.command}")
    public String meteoOrderCommand;
    @Value("${image.command}")
    public String imageOrderCommand;
    @Value("${image.dir}")
    public String imageOrderDir;

    public static final String METEO_ORDER = "meteo";

    public static final String IMAGE_ORDER = "image";

    public String orderTypeParamKey;

    public String defaultCalcOrderTypeName;

    public String defaultImageOrderTypeName;

    public Double defaultCalcOrderWorkload;

    public Double defaultImageOrderWorkload;

    public Long defaultTimeToExecuteCalcOrder;

    public Long defaultTimeToExecuteImageOrder;

    public String calcOrderTypeName;

    public String imageOrderTypeName;

}
