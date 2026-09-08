package business;

import model.Server;
import model.Task;
import strategy.*;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers;
    private List<Thread> serverThreads;
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int numberOfClients) {
        this.servers = new ArrayList<>();
        this.serverThreads = new ArrayList<>();
        this.maxNoServers = maxNoServers;
        for(int i = 0; i < maxNoServers; i++){
            Server s = new Server();
            servers.add(s);
            Thread t = new Thread(s);
            t.start();
            serverThreads.add(t);
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ShortestQueueStrategy();
        } else if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ShortestTimeStrategy();
        }
    }

    public int dispatchTask(Task task) {
        if (strategy == null) {
            throw new IllegalStateException("Selection strategy not set. Call changeStrategy(...) first.");
        }
        return strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void tick() {
        for (Server s : servers) {
            s.tick();
        }
    }

    public void shutdown() {
        for (Server s : servers) {
            s.stop();
        }
        for (Thread t : serverThreads) {
            t.interrupt();
        }
    }

    public boolean isEmpty(){
        for(Server s: servers){
            if(s.getTasks().length > 0){
                return false;
            }
        }
        return true;
    }

    public int getTotalWaitingClients() {
        int total = 0;
        for (Server s : servers) {
            total += s.getTasks().length;
        }
        return total;
    }

}
