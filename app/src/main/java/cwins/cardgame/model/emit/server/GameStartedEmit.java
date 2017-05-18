package cwins.cardgame.model.emit.server;

public class GameStartedEmit extends ServerEmit {
    private String game_id;
    private boolean resuming;

    public GameStartedEmit() { }

    public Integer getGameId() {
        Integer gameId = Integer.parseInt(game_id);
        return gameId;
    }

    public boolean isResuming() {
        return resuming;
    }

}
