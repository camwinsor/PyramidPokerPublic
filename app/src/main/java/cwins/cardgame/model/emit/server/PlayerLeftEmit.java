package cwins.cardgame.model.emit.server;



public class PlayerLeftEmit extends ServerEmit {
    private String google_id;
    private String gamer_tag;
    private Integer game_id;

    public PlayerLeftEmit() { }

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
