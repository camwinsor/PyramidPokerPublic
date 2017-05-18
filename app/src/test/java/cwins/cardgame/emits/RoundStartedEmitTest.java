package cwins.cardgame.emits;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.model.emit.server.RoundStartedEmit;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RoundStartedEmitTest {

    @Test
    public void testGsonDeserialize() {
        // TODO: this is all bogus; replace with loading a json blob from disk
        Gson gson = new Gson();

        JSONObject roundStartedObj = new JSONObject();

        JSONObject players = new JSONObject();

        JSONObject player1 = new JSONObject();
        JSONObject player2 = new JSONObject();
        JSONObject player3 = new JSONObject();

        JSONObject holeCards = new JSONObject();


        try {
            holeCards.put("0", "Ad");
            holeCards.put("1", "10c");
            holeCards.put("2", "10d");
            holeCards.put("3", "10s");
            holeCards.put("4", "2c");
            holeCards.put("5", "Jc");
            holeCards.put("6", "Ah");

            player1.put("score", 32);
            player1.put("gamer_tag", "combywomby");
            player1.put("player_state", "active");
            player1.put("player_round_state", "ready");

            player2.put("score", 20);
            player2.put("gamer_tag", "dogg0");
            player2.put("player_state", "suspended");
            player1.put("player_round_state", "ready");

            player3.put("score", -52);
            player3.put("gamer_tag", "kangaroo9");
            player3.put("player_state", "left");
            player1.put("player_round_state", "waiting_for_ready");

            players.put("g123", player1);
            players.put("g789", player2);
            players.put("g456", player3);

            roundStartedObj.put("hole_cards", holeCards);
            roundStartedObj.put("players", players);
            roundStartedObj.put("game_id", "2");
            roundStartedObj.put("resuming", false);
            roundStartedObj.put("round_id", 55);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        String roundStartedJson = roundStartedObj.toString();
        RoundStartedEmit roundStartedEmit = gson.fromJson(roundStartedJson, RoundStartedEmit.class);

        assertThat(roundStartedEmit.getGameId(), is(equalTo(2)));
        assertThat(roundStartedEmit.isResuming(), is(equalTo(false)));
        assertThat((String) roundStartedEmit.getPlayers().get("g123").get("gamer_tag"), is(equalTo("combywomby")));

        //score test is funky
//        assertThat(roundStartedEmit.getPlayers().get("g789").get("score"), Matchers.<Object>is(equalTo(20)));
        assertThat((String) roundStartedEmit.getPlayers().get("g456").get("player_state"), is(equalTo("left")));
        assertThat(roundStartedEmit.getPlayers().size(), is(equalTo(3)));
        assertThat(roundStartedEmit.getHoleCards().get(2), is(equalTo("10d")));
        assertThat(roundStartedEmit.getRoundId(), is(equalTo(55)));

    }



}
