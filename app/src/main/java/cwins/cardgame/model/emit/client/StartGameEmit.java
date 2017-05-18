package cwins.cardgame.model.emit.client;

import org.json.JSONException;
import org.json.JSONObject;


public class StartGameEmit extends ClientEmit {
    private String gameId;

    public StartGameEmit(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("game_id", gameId);
        return obj;
    }
}
