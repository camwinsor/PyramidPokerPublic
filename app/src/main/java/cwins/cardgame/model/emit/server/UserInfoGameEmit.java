package cwins.cardgame.model.emit.server;


import java.util.HashMap;

public class UserInfoGameEmit extends ServerEmit {
    private Integer game_id;
    private Integer rounds_played;
    private Integer num_players;
    private String host;
    private String started_at;
    private String created_at;
    private String game_state;
    private boolean is_active;
    private boolean is_started;
    private HashMap<String, String> gamer_tags;
    private HashMap<String, String> player_states;
    private HashMap<String, Integer> scores;

    public UserInfoGameEmit() { }

    public Integer getGameId() {
        return game_id;
    }

    public String getGameState() {
        return game_state;
    }

    public Integer getRoundsPlayed() {
        return rounds_played;
    }

    public Integer getNumPlayers() {
        return num_players;
    }

    public String getHost() {
        return host;
    }

    public String getStartedAt() {
        return started_at;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public boolean isActive() {
        return is_active;
    }

    public boolean isStarted() {
        return is_started;
    }

    public HashMap<String, String> getGamerTags() {
        return gamer_tags;
    }

    public HashMap<String, String> getPlayersStates() {
        return player_states;
    }

    public HashMap<String, Integer> getScores() {
        return scores;
    }
}
