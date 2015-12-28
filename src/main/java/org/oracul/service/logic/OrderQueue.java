package org.oracul.service.logic;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.QueueOverflowException;
import org.oracul.service.model.MeteoOrder;
import org.oracul.service.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Miramax on 26.12.2015.
 */
@Service
public class OrderQueue {
    private static final Logger LOG = Logger.getLogger(OrderQueue.class);

    private Long fullTimeToExecute;

    @Value("${queue.size}")
    private Integer maxSize;

    public Integer getMaxSize() {
        return maxSize;
    }

    @Value("${timeout}")
    private Integer timeout;

    private LinkedBlockingQueue<Order> queue;

    @PostConstruct
    private void initQueue() {
        queue = new LinkedBlockingQueue<>(maxSize);
        LOG.debug("Queue is created with size: " + maxSize);
    }

    public void putOrder(Order order) throws InterruptedException {
        if (!queue.offer(order)) {
            throw new QueueOverflowException();
        }
        order.setStatus(Order.Status.IN_QUEUE);
        fullTimeToExecute += order.getExecutionTime();
        LOG.debug("Order is added with ID = " + order.getId());
        LOG.debug("Queue size: " + queue.size());
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public Order pollMeteoOrder() {
        Order order = queue.poll();
        order.setStatus(Order.Status.IN_PROCESSING);
        fullTimeToExecute -= order.getExecutionTime();
        LOG.debug("Order #" + order.getId() + " polled. Queue size: " + queue.size());
        return order;
    }

    public Double getNextLoad() {
        if (queue.peek() == null) {
            LOG.debug("getNextLoad() - TASK is null");
            return 0.0;
        }
        LOG.debug("Order id = " + queue.peek().getId());
        return queue.peek().getExpectedWorkLoad();
    }

    public Long getFullTimeToExecute() {
        return fullTimeToExecute;
    }
}
