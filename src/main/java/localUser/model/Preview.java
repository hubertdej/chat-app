package localUser.model;

import java.util.ArrayList;
import java.util.List;

public class Preview {
    private List<Conversation> conversations;

    public Preview(/*Object serverAdapter*/) {
        conversations = loadFromServer(/*Object serverAdapter*/);
    }
    private List<Conversation> loadFromServer(/*Object serverAdapter*/) {
        //TODO
        List<Conversation> list = new ArrayList<>();
        list.add(new Conversation());
        return list;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}