package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.PlayerLeftEmit;
import io.socket.emitter.Emitter;

public class OnPlayerLeftListener implements Emitter.Listener {
    private static final String TAG = "cardgame|PLeft-- ";
    CardGameManager cgm;
    Session currentSession;

    public OnPlayerLeftListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String playerLeftJson = obj.toString();
        Gson gson = new Gson();
        PlayerLeftEmit playerLeftEmit = gson.fromJson(playerLeftJson, PlayerLeftEmit.class);

        String googleId = playerLeftEmit.getGoogleId();
        String gamerTag = playerLeftEmit.getGamerTag();
        Integer gameId = playerLeftEmit.getGameId();

        Crashlytics.log(Log.DEBUG, TAG, gamerTag + " left game " + gameId);

        currentSession.setPlayerState(gameId, googleId, "left");
        if (!googleId.equals(currentSession.getCurrentUser().getGoogleId())) {
            cgm.displayLeftMessage(gamerTag);
        }
    }

}
