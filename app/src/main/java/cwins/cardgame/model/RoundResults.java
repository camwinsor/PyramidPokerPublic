package cwins.cardgame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cwins.cardgame.model.emit.server.RoundResultsDetailsEmit;
import cwins.cardgame.model.emit.server.RoundResultsEmit;


public class RoundResults implements Iterable<MatchupTransition> {
    private String TAG = "RoundResults-- ";
    private Round round;
    private Integer roundId;
    private Game game;
    private RoundResultsEmit roundResultsEmit;

    private ArrayList<Matchup> matchups;
    private HashMap<String, Integer> roundScores;
    private HashMap<String, Integer> totalScores;
    private ArrayList<Board> allBoards;
    private HashMap<String, HashMap<String, ArrayList<String>>> allHandChoices;

    public RoundResults(RoundResultsEmit roundResultsEmit, Round round, Game game, User currentUser) {
        this.roundResultsEmit = roundResultsEmit;
        this.round = round;
        this.roundId = round.getRoundId();
        this.game = game;
        this.roundScores = roundResultsEmit.getRoundScores();
        this.totalScores = roundResultsEmit.getTotalScores();
        this.allHandChoices = roundResultsEmit.getAllHandChoices();

        // unpack the boards
        this.allBoards = new ArrayList<>();
        for (String boardId : roundResultsEmit.getAllBoards().keySet()) {
            Board board = new Board(boardId);
            for (String cardName : roundResultsEmit.getAllBoards().get(boardId)) {
                board.addCard(cardName);
            }
            this.allBoards.add(board);
        }

        // unpack all the matchups
        this.matchups = new ArrayList<>(roundResultsEmit.getDetails().size());
        for (RoundResultsDetailsEmit details : this.roundResultsEmit.getDetails()) {
            Board board = getBoardById(details.getBoardId());
            String opponentId = details.getOpponentId();
            User opposingUser = new User(opponentId, this.game.getGamerTag(opponentId));
            //User currentUser, User opponent, Board board, RoundResultsDetailsEmit details)
            Matchup matchup = new Matchup(currentUser, opposingUser, board, details);
            this.matchups.add(matchup);
        }
//
//        // TODO; no idea what this does
//        ArrayList<UserScore> scores = new ArrayList<>();
//        for (User user : round.getPlayers()) {
//            if (!game.getCurrentUser().getGoogleId().equals(user.getGoogleId())) {
//                matchups.put(user.getGoogleId(), new HashMap<String, Matchup>());
//            }
//
//            scores.add(new UserScore(game, user, totalScores.get(user.getGoogleId())));
//        }
    }


    public List<Board> getAllBoards() {
        return allBoards;
    }

    public Board getBoardById(String boardId) {
        for (Board b : this.allBoards) {
            if (b.getBoardId().equals(boardId)) {
                return b;
            }
        }
        return null;
    }

    public Board getBoardById(Integer id) {
        return getBoardById("board_" + id.toString());
    }

    public Round getRound() {
        return round;
    }

    public Game getGame() {
        return game;
    }

    public HashMap<String, Integer> getRoundScores() {
        return roundScores;
    }

    public HashMap<String, Integer> getTotalScores() {
        return totalScores;
    }

    public ArrayList<Matchup> getMatchups() {
        return matchups;
    }

    public Matchup getMatchup(String userId, String opponentId, String boardId, String gameType) {
        for (Matchup matchup : this.matchups) {
//            System.out.println("user= " + matchup.getUserGoogleId() + " opp= " + matchup.getOppoGoogleId() + " boardid= " + matchup.getBoard().getBoardId() + " gt= " + matchup.getGameType());
            if (matchup.getUserGoogleId().equals(userId) &&
                    matchup.getOppoGoogleId().equals(opponentId) &&
                    matchup.getBoard().getBoardId().equals(boardId) &&
                    matchup.getGameType().equals(gameType)) {
                return matchup;
            }
        }
        return null;
    }

    public Iterator<MatchupTransition> iterator() {
        return new MatchupTransitionIterator(this.matchups);
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getAllHandChoices() {
        return allHandChoices;
    }
}
