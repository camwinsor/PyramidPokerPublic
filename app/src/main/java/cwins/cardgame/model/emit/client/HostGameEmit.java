package cwins.cardgame.model.emit.client;

import org.json.JSONObject;


public class HostGameEmit extends ClientEmit {
    public HostGameEmit() {

    }

    @Override
    public JSONObject toJsonInternal() {
        JSONObject hosted_obj = new JSONObject();
        return hosted_obj;
    }
}
