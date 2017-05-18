package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.GameStartedEmit;
import io.socket.emitter.Emitter;

public class OnGameStartedListener implements Emitter.Listener {
    private static final String TAG = "cardgame|GameStartedLi-- ";
    Session currentSession;

    public OnGameStartedListener(Session session) {
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String gameStartedJson = obj.toString();
        Gson gson = new Gson();
        GameStartedEmit gameStartedEmit = gson.fromJson(gameStartedJson, GameStartedEmit.class);

        Crashlytics.log(Log.DEBUG, TAG, "Started gameId: " + gameStartedEmit.getGameId().toString());

        currentSession.setGameActive(true);
    }
}
