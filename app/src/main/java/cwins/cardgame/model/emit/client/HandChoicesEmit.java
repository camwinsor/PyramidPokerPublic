package cwins.cardgame.model.emit.client;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cwins.cardgame.model.HandChoices;


public class HandChoicesEmit extends ClientEmit {
    private Integer roundId;
    private Integer gameId;
    private String googleId;
    private ArrayList<Integer> highCard;
    private ArrayList<Integer> holdem;
    private ArrayList<Integer> omaha;
    private HashMap<String, ArrayList<Integer>> choices;
    private HandChoices handChoices;


    public HandChoicesEmit(Integer roundId, Integer gameId, String googleId, HandChoices handChoices) {
        this.roundId = roundId;
        this.gameId = gameId;
        this.googleId = googleId;
        this.handChoices = handChoices;

        highCard = handChoices.getHighCardId();
        holdem = handChoices.getHoldemId();
        omaha = handChoices.getOmahaId();
    }

    public void setHighCard(ArrayList<Integer> highCard) { this.highCard = highCard; }

    public void setHoldem(ArrayList<Integer> holdem) {
        this.holdem = holdem;
    }

    public void setOmaha(ArrayList<Integer> omaha) {
        this.omaha = omaha;
    }

    @Override
    public JSONObject toJsonInternal() throws JSONException {
        JSONObject choicesObj = new JSONObject();
        choicesObj.put("high_card", new JSONArray(highCard));
        choicesObj.put("holdem", new JSONArray(holdem));
        choicesObj.put("omaha", new JSONArray(omaha));

        JSONObject obj = new JSONObject();
        obj.put("choices", choicesObj);
        obj.put("game_id", gameId);
        obj.put("round_id", roundId);

        Log.d("HandChoicesEmit ", obj.toString());
        return obj;
    }
}
