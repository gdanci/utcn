package strategy;

import model.*;
import java.util.List;

public class ShortestQueueStrategy implements Strategy{
    @Override
    public int addTask(List<Server> servers, Task task){
        Server server = servers.get(0);
        for(Server s : servers){
            if(s.getQueueSize() < server.getQueueSize()){
                server = s;
            }
        }
        int waitingTime = server.getWaitingPeriod();
        server.addTask(task);
        return waitingTime;
    }
}
