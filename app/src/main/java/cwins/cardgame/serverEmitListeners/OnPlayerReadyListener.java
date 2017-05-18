package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.PlayerReadyEmit;
import io.socket.emitter.Emitter;

public class OnPlayerReadyListener implements Emitter.Listener {
    private static final String TAG = "cardgame|PReady-- ";
    CardGameManager cgm;
    Session currentSession;

    public OnPlayerReadyListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String playerReadyJson = obj.toString();
        Gson gson = new Gson();
        PlayerReadyEmit playerReadyEmit = gson.fromJson(playerReadyJson, PlayerReadyEmit.class);

        String googleId = playerReadyEmit.getGoogleId();
        Integer gameId = playerReadyEmit.getGameId();

        Crashlytics.log(Log.DEBUG, TAG, googleId + " ready for game " + gameId);

        //just switches a ready light on in main
        cgm.showPlayerReady(googleId);
    }
}
