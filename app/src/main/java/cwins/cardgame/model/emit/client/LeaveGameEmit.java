package cwins.cardgame.model.emit.client;

import org.json.JSONException;
import org.json.JSONObject;


public class LeaveGameEmit extends ClientEmit {
    private Integer gameId;

    public LeaveGameEmit(Integer gameId) {
        this.gameId = gameId;
    }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("game_id", gameId);
        return obj;
    }
}
