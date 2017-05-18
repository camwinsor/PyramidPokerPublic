package cwins.cardgame.model.emit.server;


public class PlayerReadyEmit extends ServerEmit {
    private String google_id;
    private String round_id;
    private Integer game_id;

    public PlayerReadyEmit() { }

    public String getGoogleId() {
        return google_id;
    }

    public String getRoundId() {
        return round_id;
    }

    public Integer getGameId() {
        return game_id;
    }
}
