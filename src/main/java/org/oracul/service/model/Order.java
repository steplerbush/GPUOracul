package org.oracul.service.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * Created by Miramax on 27.12.2015.
 */
public abstract class Order {

    protected UUID id;

    protected Double expectedWorkLoad;

    protected Long executionTime;

    public Order(Double expectedWorkload, Long executionTime){
        this.expectedWorkLoad = expectedWorkload;
        this.executionTime = executionTime;
    }

    public UUID getId() {
        return id;
    }

    public Double getExpectedWorkLoad() {
        return expectedWorkLoad;
    }

    public Long getExecutionTime() {
        return executionTime;
    }
}
