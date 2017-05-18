package cwins.cardgame.model.emit.client;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ClientEmit {

    public abstract JSONObject toJsonInternal() throws JSONException;

    public JSONObject toJson() {
        try {
            JSONObject obj = this.toJsonInternal();
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
