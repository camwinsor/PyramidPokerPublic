package cwins.cardgame.model.emit.client;


import org.json.JSONException;
import org.json.JSONObject;

public class ReadyNextRoundEmit extends ClientEmit {
    private Integer gameId;
    private Integer roundId;

    public ReadyNextRoundEmit(Integer gameId, Integer roundId) {
        this.gameId = gameId;
        this.roundId = roundId;
    }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("game_id", this.gameId);
        obj.put("round_id", this.roundId);
        // googleId?
        return obj;
    }
}
