package cwins.cardgame.serverEmitListeners;


import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.emit.server.UserInfoGameEmit;
import io.socket.emitter.Emitter;

public class OnUserInfoListener implements Emitter.Listener {
    private static final String TAG = "cardgame|UserInfoLi-- ";
    Session currentSession;
    CardGameManager cgm;

    public OnUserInfoListener(CardGameManager cgm, Session session) {
        this.cgm = cgm;
        this.currentSession = session;

        Crashlytics.log(Log.DEBUG, TAG, "User Info for " + currentSession.getCurrentUser().getGoogleId());
//        currentSession.setLoggedInToServer(true);
    }


    @Override
    public void call(Object... args) {
        Gson gson = new Gson();
        JSONObject obj = (JSONObject) args[0];
        JSONArray userInfo;

        try {
            userInfo = obj.getJSONArray("games");
            ArrayList<UserInfoGameEmit> userInfoGameEmits = new ArrayList<>();
            for (int i = 0; i < userInfo.length(); i++) {
                JSONObject currentGame = userInfo.getJSONObject(i);
                String currentGameJsonString = currentGame.toString();
                UserInfoGameEmit userInfoGameEmit = gson.fromJson(currentGameJsonString, UserInfoGameEmit.class);
                userInfoGameEmits.add(userInfoGameEmit);
            }

            currentSession.updateSavedGames(userInfoGameEmits);
            cgm.checkCheevoStatus(userInfoGameEmits);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
