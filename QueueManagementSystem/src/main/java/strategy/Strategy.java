package strategy;

import model.*;
import java.util.List;

public interface Strategy {
    int addTask(List<Server> servers, Task task);
}
