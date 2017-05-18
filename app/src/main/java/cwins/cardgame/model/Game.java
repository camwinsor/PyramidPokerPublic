package cwins.cardgame.model;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class Game {
    private static String TAG = "cardgame|Game Model--";
    private boolean isStarted;
    private boolean allPlayersActive;
    private boolean rejoining;
    private boolean isNewGame;
    private boolean newCardsWaiting;
    private Integer gameId;
    private Integer roundsPlayed;
    private Integer numPlayers;
    private String gameState;
    private Round currentRound;
    private User host;
    private User currentUser;
    private ArrayList<UserScore> scores;
    private ArrayList<User> players;
    private String startedAt;

    public Game(Integer gameId, User currentUser, boolean newGame) {
        this.gameId = gameId;
        this.currentUser = currentUser;
        this.isNewGame = newGame;

        if (newGame) {
            newCardsWaiting = false;
            isStarted = false;
            allPlayersActive = false;
            rejoining = false;
            roundsPlayed = 0;
        }

        scores = new ArrayList<>();
        players = new ArrayList<>();
        addPlayer(currentUser);

        Log.d(TAG, "Game constructed. new=" + newGame + " id=" + gameId);
    }

    private void updateRoundVars() {
        // maybe not?
        if (isStarted()) {
            currentRound.setPlayers(players);
        }
    }

    public void updateTotalScores() {
        //grabs total scores from round results and updates this.scores
        HashMap<String, Integer> resultsTotalScores = currentRound.getRoundResults().getTotalScores();
        ArrayList<UserScore> newScores = new ArrayList<>();
        for (String googleId : resultsTotalScores.keySet()) {
            UserScore current = new UserScore(this, getUser(googleId, null), resultsTotalScores.get(googleId));
            newScores.add(current);
        }
        setScores(newScores);
        Log.d(TAG, "Total scores updated: " + scores.toString());
    }

    public User getUser(String googleId, String gamerTag) {
        if (googleId != null) {
            for (User user : players) {
                if (user.getGoogleId().equals(googleId)) {
                    return user;
                }
            }
        } else {
            for (User user : players) {
                if (user.getGamerTag().equals(gamerTag)) {
                    return user;
                }
            }
        }
        return null;
    }

    public boolean isAnyOpponentOnPreviousRound() {
        Log.d(TAG, "Checking if any users are on previous round...");
        for (User user : players) {
            Log.d(TAG, user.getGamerTag() + " prs= " + user.getPlayerRoundState());
            if (user.getPlayerRoundState().equals("waiting_for_choices")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRoundStarted() {
        if (this.currentRound == null) {
            return false;
        }
        return true;
    }

    public boolean isNewCardsWaiting() {
        return newCardsWaiting;
    }

    public void setNewCardsWaiting(boolean newCardsWaiting) {
        this.newCardsWaiting = newCardsWaiting;
    }

    public boolean isNewGame() {
        return isNewGame;
    }

    public void setNewGame(boolean newGame) {
        isNewGame = newGame;
    }

    public void setIsActive(boolean active) { this.isStarted = active; }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public boolean isAllPlayersActive() {
        return allPlayersActive;
    }

    public void setAllPlayersActive(boolean allPlayersActive) {
        this.allPlayersActive = allPlayersActive;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setRoundsPlayed(Integer roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    public Integer getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setNumPlayers(Integer numPlayers) { this.numPlayers = numPlayers; }

    public Integer getNumPlayers() {
        return numPlayers;
    }

    public void setGameState(String gameState) { this.gameState = gameState; }

    public String getGameState() {
        return gameState;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
        this.players = currentRound.getPlayers();
        this.numPlayers = currentRound.getNumPlayers();
        this.scores = currentRound.getScores();
    }

    public void setNewRound(Round newRound) {
        this.currentRound = newRound;
        this.allPlayersActive = true;
        this.players = currentRound.getPlayers();
        this.numPlayers = currentRound.getNumPlayers();
        Log.d(TAG, "Setting new round with numplayers = " + numPlayers);
        this.scores = currentRound.getScores();
        this.roundsPlayed = currentRound.getRoundNumber();
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setHost(User host) { this.host = host; }

    public User getHost() {
        return host;
    }

    public void setPlayers(ArrayList<User> players) {
        this.players = players;
    }

    public ArrayList<User> getPlayers() {
        return players;
    }

    public void addPlayer(User player) {
        if (!players.contains(player)) {
            players.add(player);
            Log.d(TAG, player.getGamerTag() + " added to game " + this.gameId);
        }
        updateRoundVars();
    }

    public String getGoogleId(String gamerTag) {
        for (User current : players) {
            if (current.getGamerTag().equals(gamerTag)) {
                return current.getGoogleId();
            }
        }
        return null;
    }

    public String getGamerTag(String googleId) {
        for (User current : players) {
            if (current.getGoogleId().equals(googleId)) {
                return current.getGamerTag();
            }
        }
        return null;
    }

    public String getPlayerState(String googleId) {
        for (User current : players) {
            if (current.getGoogleId().equals(googleId)) {
                return current.getPlayerState();
            }
        }
        return null;
    }

    public void setPlayerState(String googleId, String status) {
        for (User current : players) {
            if (current.getGoogleId().equals(googleId)) {
                current.setPlayerState(status);
            }
        }

        if (status.equals("suspended")) {
            allPlayersActive  = false;
        } else if (status.equals("left")) {
            numPlayers -= 1;
        }

        if (numPlayers == checkActivePlayers()) {
            allPlayersActive = true;
        }

        updateRoundVars();
    }

    public Integer checkActivePlayers() {
        Integer activePlayers = 0;
        for (User current : players) {
            if (current.getPlayerState() != null && current.getPlayerState().equals("active")) {
                activePlayers += 1;
            }
        }
        return activePlayers;
    }


    public boolean choicesSubmitted() {
        if (currentRound != null) {
            return currentRound.isChoicesSubmitted();
        } else {
            return false;
        }
    }

    public ArrayList<UserScore> getScores() {
        return scores;
    }

    public void setScores(ArrayList<UserScore> scores) {
        this.scores = scores;
    }

    public Integer getPlayerScore(String googleId) {
        for (UserScore userScore : scores) {
            if (userScore.getUser().getGoogleId().equals(googleId)) {
                return userScore.getTotalScore();
            }
        }
        return null;
    }

    public void setStartedAt(String startedAt, boolean started) {
        if (started) {
            this.startedAt = formatDateTime(startedAt);
        } else {
            this.startedAt = "Never started";
        }
    }

    public String formatDateTime(String dateTime) {
        // format date/time with joda time
        String startDate = dateTime + ".000Z";
        DateTimeZone defaultZone = DateTimeZone.getDefault();
        DateTime dt = ISODateTimeFormat.dateTime().withZone(defaultZone).parseDateTime(startDate);

        Calendar calendar = Calendar.getInstance();
        DateTime currentDateTime = new DateTime(calendar.getTimeInMillis(), defaultZone);
        Period period = new Period(dt, currentDateTime);

        PeriodFormatter formatter;
        String formattedDateTime;

        if (period.getWeeks() != 0) {
            if (period.getWeeks() == 1) {
                formatter = new PeriodFormatterBuilder().appendWeeks().appendSuffix(" week ago").printZeroNever().toFormatter();
            } else {
                formatter = new PeriodFormatterBuilder().appendWeeks().appendSuffix(" weeks ago").printZeroNever().toFormatter();
            }
        } else if (period.getDays() != 0) {
            if (period.getDays() == 1) {
                formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(" day ago").printZeroNever().toFormatter();
            } else {
                formatter = new PeriodFormatterBuilder().appendDays().appendSuffix(" days ago").printZeroNever().toFormatter();
            }
        } else if (period.getHours() != 0) {
            if (period.getHours() == 1) {
                formatter = new PeriodFormatterBuilder().appendHours().appendSuffix(" hour ago").printZeroNever().toFormatter();
            } else {
                formatter = new PeriodFormatterBuilder().appendHours().appendSuffix(" hours ago").printZeroNever().toFormatter();
            }
        } else {
            if (period.getMinutes() == 0) {
                return "Moments ago";
            } else if (period.getMinutes() == 1) {
                formatter = new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minute ago").printZeroNever().toFormatter();
            } else {
                formatter = new PeriodFormatterBuilder().appendMinutes().appendSuffix(" minutes ago").printZeroNever().toFormatter();
            }
        }
        formattedDateTime = formatter.print(period);
        return formattedDateTime;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isRejoining() {
        return rejoining;
    }

    public void setRejoining(boolean rejoining) {
        this.rejoining = rejoining;
    }
}


