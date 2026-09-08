package model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private AtomicInteger waitingPeriod;
    private BlockingQueue<Task> tasks;
    private final BlockingQueue<Object> ticks = new LinkedBlockingQueue<>();
    private static final Object TICK = new Object();
    private static final Object STOP = new Object();

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task task){
        tasks.add(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public void tick() {
        ticks.offer(TICK);
    }

    public void stop() {
        ticks.offer(STOP);
    }

    public void run(){
        while (true) {
            try {
                Object token = ticks.take();
                if (token == STOP) {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            Task currentTask = tasks.peek();
            if (currentTask != null) {
                waitingPeriod.decrementAndGet();
                currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                if (currentTask.getServiceTime() <= 0) {
                    tasks.poll();
                }
            }
        }
    }

    public Task[]  getTasks(){
        return tasks.toArray(new Task[0]);
    }

    public int getQueueSize() {
        return tasks.size();
    }

    public int getWaitingPeriod(){
        return waitingPeriod.get();
    }
}
