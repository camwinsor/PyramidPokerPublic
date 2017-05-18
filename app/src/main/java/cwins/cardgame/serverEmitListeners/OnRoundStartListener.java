package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.Round;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.RoundStartedEmit;
import io.socket.emitter.Emitter;

public class OnRoundStartListener implements Emitter.Listener {
    private static final String TAG = "cardgame|RoundStartLi-- ";
    Session currentSession;
    CardGameManager cgm;

    public OnRoundStartListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        currentSession.setGameActive(true);
        Game activeGame = currentSession.getActiveGame();
        //TODO server sent round start to suspended user (user emitted suspend) - fixed??
        activeGame.setAllPlayersActive(true);

        JSONObject obj = (JSONObject) args[0];
        String roundStartedJson = obj.toString();
        Gson gson = new Gson();
        RoundStartedEmit roundStartedEmit = gson.fromJson(roundStartedJson, RoundStartedEmit.class);

        Crashlytics.log(Log.DEBUG, TAG, "Setting new round...");
        Round currentRound = new Round(roundStartedEmit, activeGame);

        //TODO: this is fucked - fixed??
        // a resuming player triggers roundstart while remaining players are still in scoring
        // the new round eliminates the old roundResult obj so
        activeGame.setCurrentRound(currentRound);

        // roundStart is called either by a single resuming player or at the start of a new round
        // synchronously for all players. roundStart always signifies all players are in-game
        cgm.roundStartUp(currentRound);
    }
}

/**
 * 2 players still in score when suspending player resumes
 * resuming player makes server send round start
 * players still in scoring receive round start
 * resuming player sits in main but should have cards
 * other 2 players should get their new cards when they move to main
 *
 *
 *
 *
 *
 *
 */
