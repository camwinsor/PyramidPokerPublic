package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.GameHostedEmit;
import io.socket.emitter.Emitter;

public class OnGameHostedListener implements Emitter.Listener {
    private static final String TAG = "cardgame|GameHostedLi-- ";
    Session currentSession;
    CardGameManager cgm;

    public OnGameHostedListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String gameHostedJson = obj.toString();
        Gson gson = new Gson();
        GameHostedEmit gameHostedEmit = gson.fromJson(gameHostedJson, GameHostedEmit.class);

        Crashlytics.log(Log.DEBUG, TAG, "Hosted gameId: " + gameHostedEmit.getGameId().toString());

        currentSession.hostGame(gameHostedEmit.getGameId());
        cgm.shareGameIdWithInvitees();
    }
}
