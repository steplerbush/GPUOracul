package org.oracul.service.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 22.12.2015.
 */
public class MeteoOrder extends Order {

    private LocalDateTime requestTime;

    private Long expectedFullWaitTime;

    private Map<String,String[]> params;

    public MeteoOrder(Map<String,String[]> params, Double expectedWorkload, Long executionTime) {
        super(expectedWorkload, executionTime);
        id = UUID.randomUUID();
        requestTime = LocalDateTime.now();
        this.params = params;
    }

    public Map<String,String[]> getParams() {
        return params;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public Long getExpectedFullWaitTime() {
        return expectedFullWaitTime;
    }

    public void setExpectedFullWaitTime(Long expectedFullWaitTime) {
        this.expectedFullWaitTime = expectedFullWaitTime;
    }
}
