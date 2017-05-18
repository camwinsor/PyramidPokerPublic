package cwins.cardgame.model;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import cwins.cardgame.model.emit.server.RoundStartedEmit;

public class Round {
    private static final String TAG = "cardgame|Round-- ";
    private Integer roundId;
    private Integer numPlayers;
    private Integer numBoards;
    private Integer roundNumber;
    private Integer numCardsSelected;
    private String roundState;
    private boolean isResuming;
    private boolean choicesSubmitted;
    private boolean readyForScoring;
    private HashMap<String, Board> allBoards;
    private HashMap<String, HashMap<String, ArrayList<String>>> allHandChoices;
    private ArrayList<UserScore> scores;
    private ArrayList<User> players;
    private HashMap<String, String> playerRoundStates;
    private Game game;
    private HoleCards holeCards;
    private RoundResults roundResults;
    private HandChoices userHandChoices;

    public Round(RoundStartedEmit roundStartedEmit, Game activeGame) {
        this.game = activeGame;
        this.allHandChoices = new HashMap<>();
        this.allBoards = new HashMap<>();
        this.playerRoundStates = new HashMap<>();
        this.readyForScoring = false;

        // TODO: tag choicesSubmitted based on playerRoundState
        if (roundStartedEmit != null) {

//            this.choicesSubmitted = false;
            this.roundId = roundStartedEmit.getRoundId();
            this.numPlayers = roundStartedEmit.getPlayers().size();
            Log.d(TAG, "Round constructing. numplayers = " + numPlayers);
            this.roundNumber = roundStartedEmit.getRoundNumber();
            this.isResuming = roundStartedEmit.isResuming();

            HashMap<Integer, String> emittedHoleCards = roundStartedEmit.getHoleCards();
            HashMap<Integer, Card> mHoleCards = new HashMap<>();
            for (Integer i : emittedHoleCards.keySet()) {
                String card = emittedHoleCards.get(i);
                Card mCard = new Card(card);
                mHoleCards.put(i, mCard);
                Log.d(TAG, "card " + card);

            }
            this.holeCards = new HoleCards(mHoleCards);

            /**
             *      { googleId : { gamerTag : thisguy,
             *                    playerState : active,
             *                    playerRoundState : waiting_for_ready,
             *                    score : 32
             *                    },
             *        googleId_2 : { .... }
             *          }
             */

            HashMap<String, HashMap<String, Object>> emittedPlayers = roundStartedEmit.getPlayers();
            ArrayList<UserScore> mScores = new ArrayList<>();
            ArrayList<User> mPlayers = new ArrayList<>();
            for (Object player : emittedPlayers.keySet()) {
                String googleId = (String) player;
                String gamerTag = (String) emittedPlayers.get(player).get("gamer_tag");
                String playerState = (String) emittedPlayers.get(player).get("player_state");
                String playerRoundState = (String) emittedPlayers.get(player).get("player_round_state");

                playerRoundStates.put(googleId, playerRoundState);

                // this is just retarded
                Integer score = Math.round(Math.round((Double) emittedPlayers.get(player).get("score")));
                Log.d(TAG, gamerTag + " score updated at round constructor: " + score);

                User user = new User(googleId, gamerTag);
                user.setPlayerState(playerState);
                mPlayers.add(user);
                mScores.add(new UserScore(game, user, score));
            }

            this.players = mPlayers;
            this.scores = mScores;
        }
        // TODO calculate numboards

    }

    public Integer getRoundId() {
        return roundId;
    }

    public Integer getNumPlayers() {
        return numPlayers;
    }


    public String getRoundNumberDisplay() {
        Integer roundDisplay = roundNumber + 1;
        return roundDisplay.toString();
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getAllHandChoices() {
        return allHandChoices;
    }

    public HashMap<String, ArrayList<Card>> getHandChoices(String googleId) {
        HashMap<String, ArrayList<Card>> playerChoices = new HashMap<>();
        HashMap<String, ArrayList<String>> serverChoiceMap = allHandChoices.get(googleId);

        for (String choiceType : serverChoiceMap.keySet()) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i=0; i<(serverChoiceMap.get(choiceType).size()); i++) {
                cards.add(new Card(serverChoiceMap.get(choiceType).get(i)));
            }
            playerChoices.put(choiceType, cards);
        }
        Log.d(TAG, "getHandChoices: " + playerChoices.toString());
        return playerChoices;
    }

    public void setAllHandChoices(HashMap<String, HashMap<String, ArrayList<String>>> allHandChoices) {
        this.allHandChoices = allHandChoices;
        readyForScoring = true;
    }

    public boolean isReadyForScoring() {
        return readyForScoring;
    }

    public void addUserHandChoices(HandChoices handChoices) {
        this.userHandChoices = handChoices;
    }

    public HandChoices getUserHandChoices() {
        return userHandChoices;
    }

    public HashMap<String, ArrayList<String>> getPlayerHandChoices(String googleId) {
        return allHandChoices.get(googleId);
    }

    public HashMap<String, Board> getAllBoards() {
        return allBoards;
    }

    public void addBoard(Board board) {
        allBoards.put(board.getBoardId(), board);
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public HoleCards getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(HoleCards holeCards) {
        this.holeCards = holeCards;
    }

    public ArrayList<UserScore> getScores() {
        return scores;
    }

    public void setScores(ArrayList<UserScore> scores) {
        this.scores = scores;
    }

    public ArrayList<User> getPlayers() {
        return players;
    }

    public String getGamerTag(String googleId) {
        for (User current : players) {
            if (current.getGoogleId().equals(googleId)) {
                return current.getGamerTag();
            }
        }
        return null;
    }

    public void setPlayers(ArrayList<User> players) {
        this.players = players;
    }

    public void setResuming(boolean resuming) {
        isResuming = resuming;
    }

    public boolean isResuming() {
        return isResuming;
    }

    public boolean isChoicesSubmitted() {
        return choicesSubmitted;
    }

    public void setChoicesSubmitted(boolean choicesSubmitted) {
        this.choicesSubmitted = choicesSubmitted;
    }

    public String getPlayerRoundState(String googleId) {
        return playerRoundStates.get(googleId);
    }

    public HashMap<String, String> getPlayerRoundStates() {
        return playerRoundStates;
    }

    public void setPlayerRoundStates(HashMap<String, String> playerRoundStates) {
        this.playerRoundStates = playerRoundStates;
    }

    public RoundResults getRoundResults() {
        return roundResults;
    }

    public void setRoundResults(RoundResults roundResults) {
        this.roundResults = roundResults;
    }
}
