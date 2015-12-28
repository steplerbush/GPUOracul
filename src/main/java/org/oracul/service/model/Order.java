package org.oracul.service.model;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Miramax on 27.12.2015.
 */
public abstract class Order {

    protected UUID id;

    private Map<String,String> params;

    protected Double expectedWorkLoad;

    protected Long executionTime;

    public enum Status {READY_FOR_QUEUE, IN_QUEUE, IN_PROCESSING, READY_FOR_PICKUP};

    private Status status;

    public Order(Map<String,String> params, Double expectedWorkload, Long executionTime){
        this.expectedWorkLoad = expectedWorkload;
        this.executionTime = executionTime;
        this.params = params;
        this.setStatus(Status.READY_FOR_QUEUE);
    }

    public UUID getId() {
        return id;
    }

    public Map<String,String> getParams() {
        return params;
    }

    public Double getExpectedWorkLoad() {
        return expectedWorkLoad;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
