package cwins.cardgame.emits;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.model.emit.server.GameStartedEmit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StartedGameEmitTest {

    @Test
    public void testGsonDeserialize() {
        Gson gson = new Gson();

        JSONObject gameStartedObj = new JSONObject();

        try {
            gameStartedObj.put("game_id", "2");
            gameStartedObj.put("resuming", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String gameStartedJson = gameStartedObj.toString();
        GameStartedEmit gameStartedEmit = gson.fromJson(gameStartedJson, GameStartedEmit.class);

        assertThat(gameStartedEmit.getGameId(), is(equalTo(2)));
        assertThat(gameStartedEmit.isResuming(), is(equalTo(false)));
    }

}