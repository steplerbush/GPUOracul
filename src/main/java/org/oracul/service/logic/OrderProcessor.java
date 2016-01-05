package org.oracul.service.logic;

import org.apache.log4j.Logger;
import org.oracul.service.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Miramax on 27.12.2015.
 */

@Service
public class OrderProcessor  extends Thread {
    private static final Logger LOG = Logger.getLogger(OrderProcessor.class);
    /*ADD THREAD THAT WILL POLL TASKS FROM QUEUE AND ADD TO ORDER PROCESSOR*
    /* AND ADD CLASS FOR THREADS-EXECUTORS, WHICH WILL RUN SERVICE PROGRAMS, OR MAKE ORDER CLASS RUNNABLE AS IN ORACUL SERVICE
     */
    Set<Order> orderSet;

    @Autowired
    OrderQueue queue;

    private Double currentWorkLoad;

    public OrderProcessor() {
        orderSet = new HashSet<>();
        currentWorkLoad = 0.0;
        this.setDaemon(true);
        this.start();
    }

    public Double addToProcessor(Order order) {
        orderSet.add(order);
        currentWorkLoad = getCurrentWorkLoad() + order.getExpectedWorkLoad();
        return getCurrentWorkLoad();
    }

    public Double releaseProcessor(Order order) {
        if (orderSet.contains(order)) {
            currentWorkLoad = getCurrentWorkLoad() - order.getExpectedWorkLoad();
            orderSet.remove(order);
        } else throw new RuntimeException("No such order in OrderProcessor. Order # " + order.getId());
        return getCurrentWorkLoad();
    }

    public Boolean canAddWorkLoad(Double oneWorkLoad) {
        if ((getCurrentWorkLoad() + oneWorkLoad) < 100 )
            return true;
        return false;
    }

    public Double getCurrentWorkLoad() {
        return currentWorkLoad;
    }

    public void run() {
        while (!isInterrupted()) {
            if (!queue.isEmpty() && this.canAddWorkLoad(queue.getNextLoad())) {
                if (LOG.isDebugEnabled()) LOG.debug("Adding order to processor");
                Order order = queue.pollOrder();
                addToProcessor(order);
                if (LOG.isDebugEnabled()) LOG.debug("Processor load: " + this.getCurrentWorkLoad() + ". Executing order " + order.getId());
                order.execute();
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
