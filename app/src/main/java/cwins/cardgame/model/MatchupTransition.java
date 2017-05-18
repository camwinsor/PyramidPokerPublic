package cwins.cardgame.model;

public class MatchupTransition {
    private Matchup matchup;

    public boolean isNewOpponent;
    public boolean isNewBoard;

    public MatchupTransition(boolean isNewOpponent, boolean isNewBoard, Matchup matchup) {
        this.isNewOpponent = isNewOpponent;
        this.isNewBoard = isNewBoard;
        this.matchup = matchup;
    }

    public GameType getGameType() {
        return matchup.getGameType();
    }

    public String getOpponentGamerTag() {
        return this.matchup.getOppoGoogleId();
    }

    public String getBoardId() {
        return this.matchup.getBoard().getBoardId();
    }

    public Matchup getMatchup() {
        return this.matchup;
    }


}