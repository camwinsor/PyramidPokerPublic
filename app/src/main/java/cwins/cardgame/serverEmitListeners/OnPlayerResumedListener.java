package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.PlayerResumedEmit;
import io.socket.emitter.Emitter;

public class OnPlayerResumedListener implements Emitter.Listener {
    private static final String TAG = "cardgame|PResumed-- ";
    CardGameManager cgm;
    Session currentSession;

    public OnPlayerResumedListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String playerResumedJson = obj.toString();
        Gson gson = new Gson();
        PlayerResumedEmit playerResumedEmit = gson.fromJson(playerResumedJson, PlayerResumedEmit.class);

        String googleId = playerResumedEmit.getGoogleId();
        String gamerTag = playerResumedEmit.getGamerTag();
        Integer gameId = playerResumedEmit.getGameId();

        Crashlytics.log(Log.DEBUG, TAG, gamerTag + " resumed game " + gameId);

        currentSession.setPlayerState(gameId, googleId, "active");
        if (!googleId.equals(currentSession.getCurrentUser().getGoogleId())) {
            cgm.displayResumedMessage(gamerTag);
        }
    }
}
