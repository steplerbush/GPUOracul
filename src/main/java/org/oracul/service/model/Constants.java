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
    public String meteoOrderDir;
    @Value("${meteo.command}")
    public String meteoOrderCommand;
    @Value("${image.command}")
    public String imageOrderCommand;
    @Value("${image.dir}")
    public String imageOrderDir;

    public static final String METEO_ORDER = "meteo";

    public static final String IMAGE_ORDER = "image";

    public static final String ROOT_PATH = new File(".").getAbsolutePath();

    @Value("${calc.order.type.default}")
    public String defaultCalcOrderTypeName;

    @Value("${image.order.type.default}")
    public String defaultImageOrderTypeName;

    @Value("${calc.order.workload.default}")
    public Double defaultCalcOrderWorkload;

    @Value("${image.order.workload.default}")
    public Double defaultImageOrderWorkload;

    @Value("${calc.order.execute.time.default}")
    public Long defaultTimeToExecuteCalcOrder;

    @Value("${image.order.execute.time.default}")
    public Long defaultTimeToExecuteImageOrder;

    @Value("${calc.order.type.name}")
    public String calcOrderTypeName;

    @Value("${image.order.type.name}")
    public String imageOrderTypeName;

    @Value("${image.format}")
    public String imageFormat;

}
