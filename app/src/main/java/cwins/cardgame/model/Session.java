package cwins.cardgame.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import cwins.cardgame.model.emit.server.UserInfoGameEmit;

public class Session {
    private static final String TAG = "cardgame|Session-- ";
    private boolean isGameActive;
    private boolean isConnectedToServer;
    private boolean isLoggedInToServer;
    private boolean isHosting;
    private boolean isCurrentUserResuming;
    private Integer recordRoundsPlayed;
    private Game activeGame;
    private Settings settings;
    private Integer expectedNumPlayers;
    private String currentActivity;
    private User currentUser;
    private HashMap<Integer, Game> savedGames;

    public Session() {
        isGameActive = false;
        isConnectedToServer = false;
        isLoggedInToServer = false;
        isHosting = false;
        isCurrentUserResuming = false;
        currentActivity = "menu";
    }

    public void hostGame(Integer gameId) {
        this.isHosting = true;
        this.isGameActive = true;
        this.isCurrentUserResuming = false;
        setActiveGame(new Game(gameId, currentUser, true));
        Log.d(TAG, "new game hosted with id " + gameId);
    }

    public void createNewGame(Integer gameId) {
        this.isGameActive = true;
        this.isCurrentUserResuming = false;
        setActiveGame(new Game(gameId, currentUser, true));
        Log.d(TAG, "new game created with id " + gameId);
    }

    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
        isGameActive = true;
        Log.d(TAG, "active game set w/ id: " + activeGame.getGameId());
    }

    public Game getActiveGame() {
        return activeGame;
    }

    public void updateSavedGames(ArrayList<UserInfoGameEmit> userInfoGameEmits) {
        savedGames = new HashMap<>();

        for (UserInfoGameEmit userInfoGameEmit : userInfoGameEmits) {
            Game game = new Game(userInfoGameEmit.getGameId(), currentUser, false);

            Integer roundsPlayed = userInfoGameEmit.getRoundsPlayed();
            game.setRoundsPlayed(roundsPlayed);

            game.setNumPlayers(userInfoGameEmit.getNumPlayers());
            game.setGameState(userInfoGameEmit.getGameState());
            if (userInfoGameEmit.isStarted()) {
                game.setStartedAt(userInfoGameEmit.getStartedAt(), true);
            } else {
                game.setStartedAt(null, false);
            }

            ArrayList<User> players = new ArrayList<>();
            ArrayList<UserScore> scores = new ArrayList<>();
            for (String currentGoogleId : userInfoGameEmit.getGamerTags().keySet()) {
                User user = new User(currentGoogleId, userInfoGameEmit.getGamerTags().get(currentGoogleId));
                user.setPlayerState(userInfoGameEmit.getPlayersStates().get(currentGoogleId));
                scores.add(new UserScore(game, user, userInfoGameEmit.getScores().get(currentGoogleId)));
                players.add(user);
            }
            game.setPlayers(players);
            game.setScores(scores);
            savedGames.put(game.getGameId(), game);
        }

    }

    public boolean isGameActive() {
        return isGameActive;
    }

    public void setGameActive(boolean gameActive) {
        isGameActive = gameActive;
    }

    public boolean isConnectedToServer() {
        return isConnectedToServer;
    }

    public void setConnectedToServer(boolean connectedToServer) {
        this.isConnectedToServer = connectedToServer;
    }

    public boolean isLoggedInToServer() {
        return isLoggedInToServer;
    }

    public void setLoggedInToServer(boolean loggedInToServer) {
        isLoggedInToServer = loggedInToServer;
    }

    public boolean isHosting() { return isHosting; }

    public void setHosting(boolean hosting) { isHosting = hosting; }

    public boolean isCurrentUserResuming() {
        return isCurrentUserResuming;
    }

    public void setCurrentUserResuming(boolean currentUserResuming) {
        isCurrentUserResuming = currentUserResuming;
    }

    public Integer getExpectedNumPlayers() {
        return expectedNumPlayers;
    }

    public void setExpectedNumPlayers(Integer expectedNumPlayers) {
        this.expectedNumPlayers = expectedNumPlayers;
    }



    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) { this.settings = settings; }

    public String getCurrentActivity() { return currentActivity; }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public HashMap<Integer, Game> getSavedGames() {
        return savedGames;
    }

    // need editing methods for individual saved games
    public void setPlayerState(Integer gameId, String googleId, String playerState) {
        if (isGameActive && activeGame.getGameId() == gameId) {
            activeGame.setPlayerState(googleId, playerState);
        }
//        else {
//            savedGames.get(gameId).setPlayerState(googleId, playerState);
//        }
    }

    public void setSavedGames(HashMap<Integer, Game> savedGames) {
        this.savedGames = savedGames;
    }

    public void deactivateGame() {
        if (activeGame.getGameId() != null) {
            Log.d(TAG, "Deactivating. game id: " + activeGame.getGameId());
        }

        // is this needed when the server sends all saved games
//        addSavedGame(activeGame);

        isGameActive = false;
        isCurrentUserResuming = false;
        isHosting = false;
        activeGame = null;
        expectedNumPlayers = null;
    }

    public void addSavedGame(Game game) {
        Log.d(TAG, "Adding saved game. game id: " + game.getGameId());
        if (savedGames.containsKey(game.getGameId())) {
            savedGames.remove(game.getGameId());
        }
        savedGames.put(game.getGameId(), game);
    }

    public void removeSavedGame(Integer gameId) {
        savedGames.remove(gameId);
    }

    public void resetSessionVars() {
        isGameActive = false;
        isConnectedToServer = false;
        isHosting = false;
        isCurrentUserResuming = false;
        activeGame = null;
        settings = new Settings();
        currentActivity = "menu";
        currentUser = null;
        expectedNumPlayers = null;
    }

}
