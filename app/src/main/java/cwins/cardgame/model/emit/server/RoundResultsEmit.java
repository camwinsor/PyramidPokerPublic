package cwins.cardgame.model.emit.server;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoundResultsEmit extends ServerEmit {
    private Integer round_number;
    private Integer game_id;
    private Integer round_id;
    private HashMap<String, Integer> round_scores;
    private HashMap<String, Integer> total_scores;
    private HashMap<String, ArrayList<String>> all_boards;
    private HashMap<String, HashMap<String, ArrayList<String>>> all_hand_choices;
    private List<RoundResultsDetailsEmit> details;

    public RoundResultsEmit() { }

    public Integer getGameId() {
        return game_id;
    }

    public Integer getRoundId() {
        return round_id;
    }

    public Integer getRoundNumber() {
        return round_number;
    }

    public HashMap<String, Integer> getRoundScores() {
        return round_scores;
    }

    public HashMap<String, Integer> getTotalScores() {
        return total_scores;
    }

    public HashMap<String, ArrayList<String>> getAllBoards() {
        return all_boards;
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getAllHandChoices() {
        return all_hand_choices;
    }

    public List<RoundResultsDetailsEmit> getDetails() {
        return this.details;
    }
}
