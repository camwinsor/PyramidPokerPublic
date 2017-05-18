package cwins.cardgame;

import java.util.HashMap;
import java.util.Map;

import cwins.cardgame.model.User;

public class UserManager {
    private HashMap<String, User> users;

    public void addUser(User user) {
        if (users.containsKey(user.getGoogleId())) {
            return;
        }

        users.put(user.getGoogleId(), user);
    }

    public User getUserByGoogleId(String googleId) {
        return users.get(googleId);
    }

    public User getUserByGamerTag(String gamerTag) {
        for (Map.Entry<String, User> e : users.entrySet()) {
            if (e.getValue().getGamerTag().equals(gamerTag)) {
                return e.getValue();
            }
        }
        return null;
    }
}
