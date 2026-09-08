package business;

import gui.SimulationFrame;
import strategy.*;
import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SimulationManager implements Runnable {

    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int numberOfServers;
    public int numberOfClients;
    public int minArrivalTime;
    public int maxArrivalTime;
    public SelectionPolicy selectionPolicy;

    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;
    private PrintWriter logWriter;

    private double totalWaitingTime = 0;
    private double totalServiceTime = 0;
    private int peakHour = 0;
    private int peakClients = 0;

    public SimulationManager() {
        reinitialize();
        frame = new SimulationFrame(this);
    }

    public void reinitialize() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        if (logWriter != null) {
            logWriter.close();
        }
        scheduler = new Scheduler(numberOfServers, numberOfClients);
        scheduler.changeStrategy(selectionPolicy);
        generatedTasks = new ArrayList<>();
        totalWaitingTime = 0;
        totalServiceTime = 0;
        peakHour = 0;
        peakClients = 0;
        generateNRandomTasks();

        try {
            logWriter = new PrintWriter(new FileWriter("log.txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        for (int i = 1; i <= numberOfClients; i++) {
            int arrivalTime = minArrivalTime + random.nextInt(maxArrivalTime - minArrivalTime + 1);
            int serviceTime = minProcessingTime + random.nextInt(maxProcessingTime - minProcessingTime + 1);
            generatedTasks.add(new Task(i, arrivalTime, serviceTime));
            totalServiceTime += serviceTime;
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    @Override
    public void run() {
        int currentTime = 0;
        while (currentTime < timeLimit) {
            Iterator<Task> iterator = generatedTasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getArrivalTime() == currentTime) {
                    totalWaitingTime += scheduler.dispatchTask(task);
                    iterator.remove();
                }
            }
            int clientsInQueues = scheduler.getTotalWaitingClients();
            if (clientsInQueues > peakClients) {
                peakClients = clientsInQueues;
                peakHour = currentTime;
            }
            String logger = buildlog(currentTime);
            log(logger);
            frame.update(currentTime, scheduler.getServers(), generatedTasks);

            if (generatedTasks.isEmpty() && scheduler.isEmpty()) {
                break;
            }
            scheduler.tick();
            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        double avgWaiting = (double) totalWaitingTime / numberOfClients;
        double avgService = (double) totalServiceTime / numberOfClients;

        String results = "Average waiting time: " + String.format("%.2f", avgWaiting) + "\n"
                + "Average service time: " + String.format("%.2f", avgService) + "\n"
                + "Peak hour: " + peakHour;
        log(results);
        frame.showResults(avgWaiting, avgService, peakHour);

        if (logWriter != null) {
            logWriter.close();
        }
    }

    private String buildlog(int currentTime) {
        StringBuilder sb = new StringBuilder();
        sb.append("Time ").append(currentTime).append("\n");
        sb.append("Waiting clients: ");
        if (generatedTasks.isEmpty()) {
            sb.append("none");
        } else {
            for (Task t : generatedTasks) {
                sb.append("(").append(t.getId()).append(", ")
                        .append(t.getArrivalTime()).append(", ")
                        .append(t.getServiceTime()).append(") ");
            }
        }
        sb.append("\n");

        List<model.Server> servers = scheduler.getServers();
        for (int i = 0; i < servers.size(); i++) {
            sb.append("Queue ").append(i + 1).append(": ");
            Task[] tasks = servers.get(i).getTasks();
            if (tasks.length == 0) {
                sb.append("closed");
            } else {
                for (Task t : tasks) {
                    sb.append("(").append(t.getId()).append(", ")
                            .append(t.getArrivalTime()).append(", ")
                            .append(t.getServiceTime()).append(") ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void log(String message) {
        System.out.println(message);
        if (logWriter != null) {
            logWriter.println(message);
            logWriter.flush();
        }
    }
}
