package cwins.cardgame.model;

import java.util.ArrayList;

public class User {
    private String playerState;
    private String playerRoundState;
    private String googleId;
    private String gamerTag;
    private String serverId;
    private ArrayList<Game> games;

    public User(String googleId, String gamerTag) {
        this.googleId = googleId;
        this.gamerTag = gamerTag;
    }

    public String getPlayerState() {
        return playerState;
    }

    public void setPlayerState(String state) {
        this.playerState = state;
    }

    public String getPlayerRoundState() {
        return playerRoundState;
    }

    public void setPlayerRoundState(String playerRoundState) {
        this.playerRoundState = playerRoundState;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getGamerTag() {
        return gamerTag;
    }

    public void setGamerTag(String gamerTag) {
        this.gamerTag = gamerTag;
    }

    public String getServerId() { return serverId; }

    public void setServerId(String serverId) { this.serverId = serverId; }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

}
