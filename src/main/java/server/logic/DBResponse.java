package server.logic;

import java.util.List;

public interface DBResponse {
    interface Subscriber {

    }
    List<Subscriber> getSubscribers();
}
