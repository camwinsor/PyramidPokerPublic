package cwins.cardgame.model.emit.server;



public class GameHostedEmit extends ServerEmit {
    private String game_id;

    public GameHostedEmit() { }

    public Integer getGameId() {
        Integer gameId = Integer.parseInt(game_id);
        return gameId;
    }
}
