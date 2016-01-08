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
public class OrderProcessor  implements Runnable {
    private static final Logger LOG = Logger.getLogger(OrderProcessor.class);
    /*ADD THREAD THAT WILL POLL TASKS FROM QUEUE AND ADD TO ORDER PROCESSOR*
    /* AND ADD CLASS FOR THREADS-EXECUTORS, WHICH WILL RUN SERVICE PROGRAMS, OR MAKE ORDER CLASS RUNNABLE AS IN ORACUL SERVICE
     */
    Set<Order> orderSet;
    Thread t;

    @Autowired
    OrderQueue queue;

    private Double currentWorkLoad;

    public OrderProcessor() {
        LOG.debug("OrderProcessor is starting...");
        orderSet = new HashSet<>();
        currentWorkLoad = 0.0;
        t = new Thread(this);
        t.start();
        LOG.debug("OrderProcessor is started");
    }

    public Double addToProcessor(Order order) {
        orderSet.add(order);
        LOG.debug("Order " + order.getId() + " is added to OrderProcessor");
        currentWorkLoad = getCurrentWorkLoad() + order.getExpectedWorkLoad();
        LOG.debug("currentWorkLoad=" + currentWorkLoad);
        return getCurrentWorkLoad();
    }

    public Double releaseProcessor(Order order) {
        if (orderSet.contains(order)) {
            currentWorkLoad = getCurrentWorkLoad() - order.getExpectedWorkLoad();
            orderSet.remove(order);
            LOG.debug("Order " + order.getId() + " is removed from OrderProcessor");
            LOG.debug("currentWorkLoad=" + currentWorkLoad);
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
        try {
            while (true) {
                if (queue != null && !queue.isEmpty() && this.canAddWorkLoad(queue.getNextLoad())) {
                    Order order = queue.pollOrder();
                    addToProcessor(order);
                    LOG.debug("New order added to processor: " + order.getClass() + " " + order.getId()
                            + "Processor current Load: " + this.getCurrentWorkLoad());
                    order.execute();
                } else {
                    try {
                        t.sleep(20);
                    } catch (InterruptedException e) {
                        LOG.error(e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Error in processor. stopped",e);
        }
    }
}
