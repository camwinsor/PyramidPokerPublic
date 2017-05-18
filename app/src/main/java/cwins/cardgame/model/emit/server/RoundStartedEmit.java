package cwins.cardgame.model.emit.server;


import java.util.HashMap;

public class RoundStartedEmit extends ServerEmit {
    private boolean resuming;
    private Integer game_id;
    private Integer round_id;
    private Integer round_number;
    private HashMap<Integer, String> hole_cards;
    private HashMap<String, HashMap<String, Object>> players;

    public RoundStartedEmit() { }

    public boolean isResuming() {
        return resuming;
    }

    public Integer getGameId() {
        return game_id;
    }

    public Integer getRoundId() {
        return round_id;
    }

    public Integer getRoundNumber() {
        return round_number;
    }

    public HashMap<Integer, String> getHoleCards() {
        return hole_cards;
    }

    public HashMap<String, HashMap<String, Object>> getPlayers() {
        return players;
    }
}
