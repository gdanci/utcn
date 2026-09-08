package strategy;

import model.*;
import java.util.List;

public class ShortestTimeStrategy implements Strategy{
    @Override
    public int addTask(List<Server> servers, Task task){
        Server server = servers.get(0);
        for(Server s : servers){
            if(s.getWaitingPeriod() < server.getWaitingPeriod()){
                server = s;
            }
        }
        int waitingTime = server.getWaitingPeriod();
        server.addTask(task);
        return waitingTime;
    }
}


