package org.oracul.service.logic;

import org.oracul.service.model.Order;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Miramax on 27.12.2015.
 */

@Service
public class OrderProcessor {
    /*ADD THREAD THAT WILL POLL TASKS FROM QUEUE AND ADD TO ORDER PROCESSOR*
    /* AND ADD CLASS FOR THREADS-EXECUTORS, WHICH WILL RUN SERVICE PROGRAMS, OR MAKE ORDER CLASS RUNNABLE AS IN ORACUL SERVICE
     */
    Set<Order> orderSet;

    private Double currentWorkLoad;

    public OrderProcessor() {
        orderSet = new HashSet<>();
        currentWorkLoad = 0.0;
    }

    public Double addToWorkLoad(Double oneWorkLoad) {
        currentWorkLoad = getCurrentWorkLoad() + oneWorkLoad;
        return getCurrentWorkLoad();
    }

    public Double releaseWorkLoad(Order order) {
        currentWorkLoad = getCurrentWorkLoad() - order.getExpectedWorkLoad();
        return getCurrentWorkLoad();
    }

    public Boolean canAddWorkLoad(Double oneWorkLoad) {
        if ((getCurrentWorkLoad() +oneWorkLoad) < 100 )
            return true;
        return false;
    }

    public Double getCurrentWorkLoad() {
        return currentWorkLoad;
    }
}
