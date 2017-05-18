package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.PlayerSuspendedEmit;
import io.socket.emitter.Emitter;

public class OnPlayerSuspendedListener implements Emitter.Listener {
    private static final String TAG = "cardgame|PSuspended-- ";
    CardGameManager cgm;
    Session currentSession;

    public OnPlayerSuspendedListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String playerSuspendedJson = obj.toString();
        Gson gson = new Gson();
        PlayerSuspendedEmit playerSuspendedEmit = gson.fromJson(playerSuspendedJson, PlayerSuspendedEmit.class);

        String googleId = playerSuspendedEmit.getGoogleId();
        String gamerTag = playerSuspendedEmit.getGamerTag();
        Integer gameId = playerSuspendedEmit.getGameId();

        Crashlytics.log(Log.DEBUG, TAG, gamerTag + " suspended game " + gameId);

        if (!googleId.equals(currentSession.getCurrentUser().getGoogleId())) {
            currentSession.setPlayerState(gameId, googleId, "suspended");
            cgm.displaySuspendMessage(gamerTag);
        }
    }

}
