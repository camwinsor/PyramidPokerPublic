package cwins.cardgame.model.emit.server;

/**
 {
     "board_id": 0,
     "game_type": "high_card",
     "p1": "g5678hoster",
     "p1_hand_type": "Pair",
     "p1_score": 0,
     "p2": "1234joiner",
     "p2_hand_type": "Pair",
     "p2_score": 0,
     "winner": "tie"
 },
 */

public class RoundResultsDetailsEmit extends ServerEmit {
    private Integer board_id;
    private String game_type;
    private String opponent_id;
    private String hand_type;
    private String opponent_hand_type;
    private String winner;
    private Integer score;
    private Integer opponent_score;

    public Integer getBoardId() {
        return board_id;
    }

    public String getGameType() {
        return game_type;
    }

    public String getOpponentId() {
        return opponent_id;
    }

    public String getHandType() {
        return hand_type;
    }

    public String getOpponentHandType() {
        return opponent_hand_type;
    }

    public String getWinner() {
        return winner;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getOpponentScore() {
        return opponent_score;
    }
}
