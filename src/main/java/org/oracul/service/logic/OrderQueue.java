package org.oracul.service.logic;

import org.apache.log4j.Logger;
import org.oracul.service.exceptions.QueueOverflowException;
import org.oracul.service.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    private LinkedBlockingQueue<Order> queue;

    @PostConstruct
    private void initQueue() {
        queue = new LinkedBlockingQueue<>(maxSize);
        fullTimeToExecute = 0L;
        LOG.debug("Queue is created with size: " + maxSize);
    }

    public void putOrder(Order order) {
        if (!queue.offer(order)) {
            throw new QueueOverflowException();
        }
        order.setStatus(Order.Status.IN_QUEUE);
        fullTimeToExecute += order.getExecutionTime();
        LOG.debug("Order # " + order.getId() + " is added to queue");
        LOG.debug("Queue size: " + queue.size());
    }

    public void putOrderWithWaiting(Order order) throws InterruptedException{
        queue.put(order);
        order.setStatus(Order.Status.IN_QUEUE);
        fullTimeToExecute += order.getExecutionTime();
        LOG.debug("Order # " + order.getId() + " is added to queue");
        LOG.debug("Queue size: " + queue.size());
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public Order pollOrder() {
        Order order = queue.poll();
        order.setStatus(Order.Status.IN_PROCESSING);
        fullTimeToExecute -= order.getExecutionTime();
        LOG.debug("Order # " + order.getId() + " polled from queue. Queue size: " + queue.size());
        return order;
    }

    public Double getNextLoad() {
        if (isEmpty()) {
            LOG.debug("getNextLoad() - queue is empty");
            return 0.0;
        }
        Order o = queue.peek();
        return o.getExpectedWorkLoad();
    }

    public Long getFullTimeToExecute() {
        return fullTimeToExecute;
    }
}
