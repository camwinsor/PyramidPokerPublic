package cwins.cardgame.serverEmitListeners;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;
import cwins.cardgame.model.emit.server.PlayerJoinedEmit;
import io.socket.emitter.Emitter;

public class OnPlayerJoinedListener implements Emitter.Listener {
    private static final String TAG = "cardgame|PJoined-- ";
    CardGameManager cgm;
    Session currentSession;

    public OnPlayerJoinedListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;
    }

    @Override
    public void call(Object... args) {
        JSONObject obj = (JSONObject) args[0];
        String playerJoinedJson = obj.toString();
        Gson gson = new Gson();
        PlayerJoinedEmit playerJoinedEmit = gson.fromJson(playerJoinedJson, PlayerJoinedEmit.class);

        String googleId = playerJoinedEmit.getGoogleId();
        String gamerTag = playerJoinedEmit.getGamerTag();
        Integer gameId = playerJoinedEmit.getGameId();

        User joiningPlayer = new User(googleId, gamerTag);
        joiningPlayer.setPlayerState("active");
        currentSession.getActiveGame().addPlayer(joiningPlayer);
        Log.d(TAG, gamerTag + " joined. number of active players: " +
                currentSession.getActiveGame().getPlayers().size());

        //TODO: race condition with slow samsung hosting: (fixed?)
        // host receives playerJoined before having set expected # players to compare
        // against to determine whether to startGame
        cgm.checkPlayersForGameStart();
    }
}
