package cwins.cardgame.emits;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.serverEmitListeners.OnUserInfoListener;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;
import cwins.cardgame.model.UserScore;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserInfoEmitTest {

    @Test
    public void testGsonDeserialize() {
        JSONObject demoObj = new JSONObject();

        JSONArray games = new JSONArray();

        JSONObject testGame1 = new JSONObject();
        JSONObject testGame2 = new JSONObject();

        JSONObject gamer_tags = new JSONObject();
        JSONObject players_states = new JSONObject();
        JSONObject demoScores1 = new JSONObject();
        JSONObject demoScores2 = new JSONObject();

        try {
            testGame1.put("created_at", "2017-02-03T18:40:04");
            testGame1.put("started_at", "2017-02-03T18:40:08");
            testGame1.put("game_id", 1);
            testGame1.put("host", "g5678hoster");
            testGame1.put("is_active", false);
            testGame1.put("is_started", true);
            testGame1.put("num_players", 2);
            testGame1.put("rounds_played", 0);
            testGame1.put("game_state", "suspended");

            gamer_tags.put("1234joiner", "hax0r");
            gamer_tags.put("g5678hoster", "comby");

            players_states.put("1234joiner", "active");
            players_states.put("g5678hoster", "suspended");

            demoScores1.put("1234joiner", 0);
            demoScores1.put("g5678hoster", 0);

            testGame1.put("gamer_tags", gamer_tags);
            testGame1.put("players_states", players_states);
            testGame1.put("scores", demoScores1);


            testGame2.put("created_at", "2017-02-03T18:41:45");
            testGame2.put("started_at", "2017-02-03T18:42:08");
            testGame2.put("game_id", 2);
            testGame2.put("host", "g5678hoster");
            testGame2.put("is_active", false);
            testGame2.put("is_started", true);
            testGame2.put("num_players", 2);
            testGame2.put("rounds_played", 2);
            testGame2.put("game_state", "active");

            demoScores2.put("1234joiner", 22);
            demoScores2.put("g5678hoster", -22);

            testGame2.put("gamer_tags", gamer_tags);
            testGame2.put("players_states", players_states);
            testGame2.put("scores", demoScores2);

            games.put(testGame1);
            games.put(testGame2);

            demoObj.put("games", games);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Session demoSession = new Session();
        // XXX have not updated since passing cgm to listener
        OnUserInfoListener onUserInfoListener = new OnUserInfoListener(null, demoSession);

        onUserInfoListener.call(demoObj);

        HashMap<Integer, Game> savedGames = demoSession.getSavedGames();
        Game game1 = savedGames.get(1);
        assertThat(game1.isStarted(), is(equalTo(false)));
        assertThat(game1.getNumPlayers(), is(equalTo(2)));
        assertThat(game1.getGamerTag("g5678hoster"), is(equalTo("comby")));

        Game game2 = savedGames.get(2);
        assertThat(game2.getStartedAt(), is(equalTo("Fri 2/03 at 10:42")));
        assertThat(game2.getRoundsPlayed(), is(equalTo(2)));
        assertThat(game2.getGameState(), is(equalTo("active")));

        ArrayList<User> players1 = game1.getPlayers();
        assertThat(players1.size(), is(equalTo(2)));
        for (User user : players1) {
            if (user.getGoogleId().equals("1234joiner")) {
                assertThat(user.getGamerTag(), is(equalTo("hax0r")));
            } else if (user.getGoogleId().equals("g5678hoster")) {
                assertThat(user.getPlayerState(), is(equalTo("suspended")));
            }
        }

        ArrayList<User> players2 = game2.getPlayers();
        assertThat(players2.size(), is(equalTo(2)));
        for (User user : players2) {
            if (user.getPlayerState().equals("active")) {
                assertThat(user.getGamerTag(), is(equalTo("hax0r")));
            } else if (user.getGamerTag().equals("comby")) {
                assertThat(user.getGoogleId(), is(equalTo("g5678hoster")));
            }
        }

        ArrayList<UserScore> scores1 = game1.getScores();
        assertThat(scores1.size(), is(equalTo(2)));
        for (UserScore userScore : scores1) {
            if (userScore.getUser().getGoogleId().equals("1234joiner")) {
                assertThat(userScore.getTotalScore(), is(equalTo(0)));
            } else if (userScore.getUser().getGoogleId().equals("g5678hoster")) {
                assertThat(userScore.getTotalScore(), is(equalTo(0)));
            }
        }

        ArrayList<UserScore> scores2 = game2.getScores();
        assertThat(scores2.size(), is(equalTo(2)));
        for (UserScore userScore : scores2) {
            if (userScore.getUser().getGoogleId().equals("g5678hoster")) {
                assertThat(userScore.getTotalScore(), is(equalTo(-22)));
            } else if (userScore.getUser().getGoogleId().equals("1234joiner")) {
                assertThat(userScore.getTotalScore(), is(equalTo(22)));
            }
        }


    }
}
