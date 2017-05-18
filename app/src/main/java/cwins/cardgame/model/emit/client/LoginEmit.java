package cwins.cardgame.model.emit.client;


import org.json.JSONException;
import org.json.JSONObject;

import cwins.cardgame.model.User;

public class LoginEmit extends ClientEmit {
    private String googleId;
    private String gamerTag;
    private String version;

    public LoginEmit(User currentUser, String version) {
        this.googleId = currentUser.getGoogleId();
        this.gamerTag = currentUser.getGamerTag();
        this.version = version;
    }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("google_id", googleId);
        obj.put("gamer_tag", gamerTag);
        obj.put("version", version);

        return obj;
    }
}
