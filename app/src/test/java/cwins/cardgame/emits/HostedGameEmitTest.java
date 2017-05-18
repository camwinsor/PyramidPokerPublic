package cwins.cardgame.emits;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.model.emit.server.GameHostedEmit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HostedGameEmitTest {

    @Test
    public void testGsonDeserialize() {
        Gson gson = new Gson();
        JSONObject obj = new JSONObject();
        try {
            obj.put("game_id", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String gameHostedJson = obj.toString();
        GameHostedEmit gameHostedEmit = gson.fromJson(gameHostedJson, GameHostedEmit.class);

        assertThat(gameHostedEmit.getGameId(), is(equalTo(2)));
    }
}
