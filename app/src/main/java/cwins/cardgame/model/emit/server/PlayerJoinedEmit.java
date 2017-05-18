package cwins.cardgame.model.emit.server;


public class PlayerJoinedEmit extends ServerEmit {
    private String google_id;
    private String gamer_tag;
    private Integer game_id;

    public PlayerJoinedEmit() { }

    public String getGoogleId() {
        return google_id;
    }

    public String getGamerTag() {
        return gamer_tag;
    }

    public Integer getGameId() {
        return game_id;
    }
}
