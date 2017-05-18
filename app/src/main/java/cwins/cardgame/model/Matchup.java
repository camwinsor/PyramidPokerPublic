package cwins.cardgame.model;


import cwins.cardgame.model.emit.server.RoundResultsDetailsEmit;


public class Matchup implements Comparable<Matchup>{
    private User opponent;
    private User currentUser;
    private GameType gameType;
    private Board board;
    private User winner;
    private Integer scoreChange;
    private Integer opponentScore;
    private String handType;
    private String opponentHandType;

    public Matchup(User currentUser, User opponent, Board board, RoundResultsDetailsEmit details) {
        this.currentUser = currentUser;
        this.opponent = opponent;
        this.board = board;

        switch (details.getGameType()) {
            case "high_card":
                this.gameType = GameType.HIGHCARD;
                break;
            case "holdem":
                this.gameType = GameType.HOLDEM;
                break;
            case "omaha":
                this.gameType = GameType.OMAHA;
                break;
        }

        this.handType = details.getHandType();
        this.opponentHandType = details.getOpponentHandType();

        this.scoreChange = details.getScore();
        this.opponentScore = details.getOpponentScore();

        if (details.getWinner().equals(currentUser.getGoogleId())) {
            this.winner = currentUser;
        } else if (details.getWinner().equals(opponent.getGoogleId())) {
            this.winner = opponent;
        } else {
            this.winner = null;
        }
    }

    // TODO: reformat strings to fit textviews (add newline for long strings)
    public String getHandType() {
        return handType;
    }

    public String getOpponentHandType() {
        return opponentHandType;
    }

    public String getUserGoogleId() {
        return currentUser.getGoogleId();
    }

    public String getOppoGoogleId() { return opponent.getGoogleId(); }

    public String getOppoGamerTag() {
        return opponent.getGamerTag();
    }

    public Board getBoard() {
        return board;
    }

    public GameType getGameType() {
        return gameType;
    }

    public User getWinner() {
        return winner;
    }

    public Integer getScore() {
        return scoreChange;
    }

    public Integer getOpponentScore() {
        return opponentScore;
    }

    public int compareTo(Matchup other) {
        // first order by opponent
        int i = opponent.getGoogleId().compareTo(other.opponent.getGoogleId());
        if (i != 0) return i;

        // then order by board
        i = board.getBoardId().compareTo(other.board.getBoardId());
        if (i != 0) return i;

        // then order by game_type
        // TODO: this only works because ["highcard", "holdem", "omaha"] are already in order
        return gameType.compareTo(other.gameType);
    }
}
