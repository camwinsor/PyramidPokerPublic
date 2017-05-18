package cwins.cardgame.model.emit.client;

import org.json.JSONException;
import org.json.JSONObject;



public class FetchStateEmit extends ClientEmit {
    public FetchStateEmit() { }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject obj = new JSONObject();
        return obj;
    }
}
