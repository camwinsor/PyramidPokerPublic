package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Board;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.Round;
import cwins.cardgame.model.RoundResults;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.RoundResultsEmit;
import io.socket.emitter.Emitter;


public class OnResultsListener implements Emitter.Listener {
    private static final String TAG = "cardgame|OnResults-- ";
    Session currentSession;
    CardGameManager cgm;

    public OnResultsListener(CardGameManager cardGameManager, Session session) {
        this.currentSession = session;
        cgm = cardGameManager;
        Log.d("OnResultsListener--", currentSession.getCurrentUser().getGoogleId());
    }

    @Override
    public void call(Object... args) {
        // parse the json
        Gson gson = new Gson();
        JSONObject obj = (JSONObject) args[0];
        String resultsJson = obj.toString();
        Crashlytics.log(Log.DEBUG, TAG, "JSON RR: " + resultsJson);
        RoundResultsEmit roundResultsEmit = gson.fromJson(resultsJson, RoundResultsEmit.class);

        // unpack json into RoundResults object
        Game activeGame = currentSession.getActiveGame();

        // XXX find better way to avoid the problem of results being delivered to a unready user
        if (activeGame.getCurrentRound() != null) {
            Round currentRound = activeGame.getCurrentRound();
            RoundResults roundResults = new RoundResults(
                    roundResultsEmit, currentRound, activeGame, currentSession.getCurrentUser());

            // update state using RoundResults object
            currentRound.setRoundResults(roundResults);
            currentRound.setAllHandChoices(
                    roundResults.getAllHandChoices());

            // record boards
            for (Board b : roundResults.getAllBoards()) {
                currentRound.addBoard(b);
            }

            // TODO: need to update game scores | need to know starting total score for score indicator
            // currently just updating after when next round starts with roundstart emit data
//        game.setScores(scores);

            // beginScoring
            cgm.displayResults();
        }
    }
}
