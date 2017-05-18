import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.inject.Named;
import javax.inject.Singleton;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.ForApplication;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.Round;
import cwins.cardgame.model.RoundResults;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;
import cwins.cardgame.model.emit.server.RoundResultsEmit;
import cwins.cardgame.model.emit.server.RoundStartedEmit;
import dagger.Module;
import dagger.Provides;

@Module
public class MockModule {
    @Provides
    @Singleton
    Session provideSession() {
        Session s = new Session();
        User currentUser = new User("joiner_57PRH", "KJHOQ");
        s.setCurrentUser(currentUser);

        String rseBlob = "{\"game_id\": 27,\"hole_cards\": " +
                "{\"0\": \"5c\",\"1\": \"7h\",\"2\": \"Tc\",\"3\": \"5h\",\"4\": \"6c\",\"5\": \"7c\",\"6\": \"6s\"}," +
                "\"players\": {\"g5678hoster\": {\"gamer_tag\": \"And0r\",\"player_round_state\": \"waiting_for_choices\",\"player_state\": \"active\",\"score\": 0}," +
                "\"joiner_57PRH\": {\"gamer_tag\": \"KJHOQ\",\"player_round_state\": \"waiting_for_choices\",\"player_state\": \"active\",\"score\": 0}}," +
                "\"resuming\": false,\"round_id\": 15,\"round_number\": 0}";
        RoundStartedEmit rse = getRoundStartEmitFromBlob(rseBlob);
        Game g = new Game(27, currentUser, true);
        Round r = new Round(rse, g);

        String resultsBlob =
                "{\"all_boards\": {\"board_0\": [\"7s\",\"Kh\",\"5d\",\"8h\",\"Qd\"]," +
                                "\"board_1\": [\"Kc\",\"Qc\",\"Kd\",\"Ad\",\"4s\"]," +
                                "\"board_2\": [\"Th\",\"Jh\",\"9h\",\"9s\",\"8d\"]," +
                                "\"board_3\": [\"Ac\",\"4h\",\"2d\",\"Jd\",\"As\"]," +
                                "\"board_4\": [\"Ts\",\"5s\",\"3s\",\"Qs\",\"Jc\"]," +
                                "\"board_5\": [\"2c\",\"9c\",\"6d\",\"Js\",\"2s\"]}," +
                "\"all_hand_choices\": {\"g5678hoster\": {\"high_card\": [\"6h\"],\"holdem\": [\"3c\",\"4d\"],\"omaha\": [\"8s\",\"Ah\",\"9d\",\"8c\"]}," +
                                        "\"joiner_57PRH\": {\"high_card\": [\"6s\"],\"holdem\": [\"6c\",\"7c\"],\"omaha\": [\"5c\",\"7h\",\"Tc\",\"5h\"]}}," +
                "\"details\": [{\"board_id\": 0,\"game_type\": \"high_card\",\"hand_type\": \"High Card\",\"opponent_hand_type\": \"High Card\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 0,\"score\": 0,\"winner\": \"tie\"}," +
                                "{\"board_id\": 0,\"game_type\": \"holdem\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"High Card\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": -2,\"score\": 2,\"winner\": \"joiner_57PRH\"}," +
                                "{\"board_id\": 0,\"game_type\": \"omaha\",\"hand_type\": \"Three of a Kind\",\"opponent_hand_type\": \"Three of a Kind\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 1,\"game_type\": \"high_card\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 0,\"score\": 0,\"winner\": \"tie\"}," +
                                "{\"board_id\": 1,\"game_type\": \"holdem\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"Two Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 2,\"score\": -2,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 1,\"game_type\": \"omaha\",\"hand_type\": \"Two Pair\",\"opponent_hand_type\": \"Two Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 2,\"game_type\": \"high_card\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 0,\"score\": 0,\"winner\": \"tie\"}," +
                                "{\"board_id\": 2,\"game_type\": \"holdem\",\"hand_type\": \"Straight\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": -2,\"score\": 2,\"winner\": \"joiner_57PRH\"}," +
                                "{\"board_id\": 2,\"game_type\": \"omaha\",\"hand_type\": \"Flush\",\"opponent_hand_type\": \"Full House\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 3,\"game_type\": \"high_card\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 0,\"score\": 0,\"winner\": \"tie\"}," +
                                "{\"board_id\": 3,\"game_type\": \"holdem\",\"hand_type\": \"Pair\",\"opponent_hand_type\": \"Two Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 2,\"score\": -2,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 3,\"game_type\": \"omaha\",\"hand_type\": \"Two Pair\",\"opponent_hand_type\": \"Three of a Kind\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 4,\"game_type\": \"high_card\",\"hand_type\": \"Flush\",\"opponent_hand_type\": \"High Card\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": -1,\"score\": 1,\"winner\": \"joiner_57PRH\"}," +
                                "{\"board_id\": 4,\"game_type\": \"holdem\",\"hand_type\": \"High Card\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 2,\"score\": -2,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 4,\"game_type\": \"omaha\",\"hand_type\": \"Three of a Kind\",\"opponent_hand_type\": \"Straight\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}," +
                                "{\"board_id\": 5,\"game_type\": \"high_card\",\"hand_type\": \"Two Pair\",\"opponent_hand_type\": \"Two Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 0,\"score\": 0,\"winner\": \"tie\"}," +
                                "{\"board_id\": 5,\"game_type\": \"holdem\",\"hand_type\": \"Two Pair\",\"opponent_hand_type\": \"Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": -2,\"score\": 2,\"winner\": \"joiner_57PRH\"}," +
                                "{\"board_id\": 5,\"game_type\": \"omaha\",\"hand_type\": \"Two Pair\",\"opponent_hand_type\": \"Two Pair\",\"opponent_id\": \"g5678hoster\",\"opponent_score\": 3,\"score\": -3,\"winner\": \"g5678hoster\"}]," +
                "\"game_id\": 27,\"round_id\": 15,\"round_number\": 0," +
                        "\"round_scores\": {\"g5678hoster\": 17,\"joiner_57PRH\": -17}," +
                        "\"total_scores\": {\"g5678hoster\": 17,\"joiner_57PRH\": -17}}";
        RoundResultsEmit resultsEmit = getRoundResultsEmitFromBlob(resultsBlob);
        RoundResults results = new RoundResults(resultsEmit, r, g, currentUser);
        r.setRoundResults(results);
        r.setAllHandChoices(results.getAllHandChoices());
        g.setCurrentRound(r);
        s.setActiveGame(g);
        return s;
    }

    @Provides
    @Singleton
    CardGameManager provideCardGameManager(Session session, @Named("serverUrl") String serverUrl) {
        CardGameManager cardGameManager = new CardGameManager(session, serverUrl);
        return cardGameManager;
    }

    @Named("serverUrl")
    @Provides
    @Singleton
    String provideServerUrl() {
        String serverUrl = "http://10.0.1.65:8000";
        return serverUrl;
    }

    @Provides
    @Singleton
    GoogleApiClient provideGoogleApiClient(@ForApplication Context context) {
        return null;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return null;
    }

    public static JSONObject getJsonObjectFromString(String jsonBlob) {
        try {
            return (JSONObject) new JSONTokener(jsonBlob).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private RoundStartedEmit getRoundStartEmitFromBlob(String jsonBlob) {
        JSONObject demoObj = getJsonObjectFromString(jsonBlob);
        String resultsJson = demoObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(resultsJson, RoundStartedEmit.class);
    }

    private RoundResultsEmit getRoundResultsEmitFromBlob(String jsonBlob) {
        JSONObject demoObj = getJsonObjectFromString(jsonBlob);
        String resultsJson = demoObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(resultsJson, RoundResultsEmit.class);
    }

}
