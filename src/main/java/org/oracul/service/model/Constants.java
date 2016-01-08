package org.oracul.service.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by Miramax on 27.12.2015.
 */
@Service
public class Constants {

    @Value("${meteo.dir}")
    private String meteoOrderDir;

    @Value("${meteo.comm}")
    private String meteoOrderCommand;

    @Value("${image.comm}")
    private String imageOrderCommand;

    @Value("${image.dir}")
    private String imageOrderDir;

    public static final String METEO_ORDER = "meteo";

    public static final String IMAGE_ORDER = "image";

    public static final String ROOT_PATH = new File(".").getAbsolutePath();

    @Value("${calc.order.type.default}")
    private String defaultCalcOrderTypeName;

    @Value("${image.order.type.default}")
    private String defaultImageOrderTypeName;

    @Value("${calc.order.workload.default}")
    private Double defaultCalcOrderWorkload;

    @Value("${image.order.workload.default}")
    private Double defaultImageOrderWorkload;

    @Value("${calc.order.execute.time.default}")
    private Long defaultTimeToExecuteCalcOrder;

    @Value("${image.order.execute.time.default}")
    private Long defaultTimeToExecuteImageOrder;

    @Value("${calc.order.type.name}")
    private String calcOrderTypeName;

    @Value("${image.order.type.name}")
    private String imageOrderTypeName;

    @Value("${image.format}")
    private String imageFormat;

    public String getMeteoOrderDir() {
        return meteoOrderDir;
    }

    public String getMeteoOrderCommand() {
        return meteoOrderCommand;
    }

    public String getImageOrderCommand() {
        return imageOrderCommand;
    }

    public String getImageOrderDir() {
        return imageOrderDir;
    }

    public String getDefaultCalcOrderTypeName() {
        return defaultCalcOrderTypeName;
    }

    public String getDefaultImageOrderTypeName() {
        return defaultImageOrderTypeName;
    }

    public Double getDefaultCalcOrderWorkload() {
        return defaultCalcOrderWorkload;
    }

    public Double getDefaultImageOrderWorkload() {
        return defaultImageOrderWorkload;
    }

    public Long getDefaultTimeToExecuteCalcOrder() {
        return defaultTimeToExecuteCalcOrder;
    }

    public Long getDefaultTimeToExecuteImageOrder() {
        return defaultTimeToExecuteImageOrder;
    }

    public String getCalcOrderTypeName() {
        return calcOrderTypeName;
    }

    public String getImageOrderTypeName() {
        return imageOrderTypeName;
    }

    public String getImageFormat() {
        return imageFormat;
    }
}
