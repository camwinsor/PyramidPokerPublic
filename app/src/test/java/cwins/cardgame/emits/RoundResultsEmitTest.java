package cwins.cardgame.emits;


import com.google.gson.Gson;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Iterator;

import cwins.cardgame.BuildConfig;
import cwins.cardgame.TestUtils;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.GameType;
import cwins.cardgame.model.Matchup;
import cwins.cardgame.model.MatchupTransition;
import cwins.cardgame.model.Round;
import cwins.cardgame.model.RoundResults;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;
import cwins.cardgame.model.emit.server.RoundResultsEmit;
import cwins.cardgame.model.emit.server.RoundStartedEmit;

import static cwins.cardgame.model.GameType.HIGHCARD;
import static cwins.cardgame.model.GameType.HOLDEM;
import static cwins.cardgame.model.GameType.OMAHA;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class RoundResultsEmitTest extends TestCase {
    private Round currentRound;
    private Session demoSession;
    private Game currentGame;
    private User demoUser;

    @Before
    public void setUp() {
        demoSession = new Session();
        demoUser = new User("1234joiner", "comby");
        User demoOppo = new User("g5678hoster", "hax0r");
        demoSession.setCurrentUser(demoUser);


        ArrayList<User> demoPlayers = new ArrayList<>();
        demoPlayers.add(demoUser);
        demoPlayers.add(demoOppo);

        currentGame = new Game(24, demoUser, false);
        currentGame.setPlayers(demoPlayers);

        RoundStartedEmit rse = getRoundStartedEmitFromFile("demoRoundStart.json");

        currentRound = new Round(rse, demoSession.getActiveGame());
        currentRound.setPlayers(demoPlayers);
        currentGame.setCurrentRound(currentRound);
        demoSession.setActiveGame(currentGame);
    }

    private RoundResultsEmit getResultsEmitFromFile(String fileName) {
        String basePath = "app/src/test/java/cwins/cardgame/emits";
        String demoJsonpath = basePath + "/" + fileName;

        JSONObject demoObj = TestUtils.createJsonFromFile(demoJsonpath);
        String resultsJson = demoObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(resultsJson, RoundResultsEmit.class);
    }

    private RoundStartedEmit getRoundStartedEmitFromFile(String fileName) {
        String basePath = "app/src/test/java/cwins/cardgame/emits";
        String demoJsonpath = basePath + "/" + fileName;

        JSONObject demoObj = TestUtils.createJsonFromFile(demoJsonpath);
        String resultsJson = demoObj.toString();
        Gson gson = new Gson();
        return gson.fromJson(resultsJson, RoundStartedEmit.class);
    }

    @Test
    public void testGsonDeserialize() {
        RoundResultsEmit roundResultsEmit = getResultsEmitFromFile("results_new.json");
        RoundResults roundResults = new RoundResults(
                roundResultsEmit, this.currentRound, this.currentGame, this.demoUser);


//        assertThat(currentRound.getRoundId(), is(equalTo(147)));
        assertThat(roundResults.getAllBoards().size(), is(equalTo(6)));
        assertThat(
                roundResults.getBoardById("board_5").getCards().size(),
                is(equalTo(5)));
        assertThat(
                roundResults.getAllHandChoices().get("g5678hoster").get("omaha").size(),
                is(equalTo(4)));

        assertThat(roundResults.getRoundScores().get("g5678hoster"), is(equalTo(-14)));
        assertThat(roundResults.getTotalScores().get("74J9P"), is(equalTo(24)));

//        assertThat(roundResults.getMatchups().size(), is(equalTo(6)));

        Matchup highCard = roundResults.getMatchup("1234joiner", "74J9P", "board_0", "holdem");
        assertThat(
                highCard.getScore(),
                is(equalTo(2)));

        Matchup holdem = roundResults.getMatchup("1234joiner", "11DTP", "board_2", "holdem");
        assertThat(
                holdem.getWinner().getGoogleId(),
                is(equalTo("11DTP")));
    }

    @Test
    public void testMatchupIterator() {
        RoundResultsEmit roundResultsEmit = getResultsEmitFromFile("results_new.json");
        RoundResults roundResults = new RoundResults(
                roundResultsEmit, this.currentRound, this.currentGame, this.demoUser);

        Iterator<MatchupTransition> it = roundResults.iterator();

        MatchupTransition mt = it.next();

        assertThat(mt.getBoardId(), is(equalTo("board_0")));
        assertThat(mt.getGameType(), is(equalTo(GameType.HIGHCARD)));
        assertThat(mt.getOpponentGamerTag(), is(equalTo("11DTP")));
        assertThat(mt.isNewBoard, is(equalTo(true)));
        assertThat(mt.isNewOpponent, is(equalTo(true)));

        mt = it.next();
        assertThat(mt.getBoardId(), is(equalTo("board_0")));
        assertThat(mt.getGameType(), is(equalTo(HOLDEM)));
        assertThat(mt.getOpponentGamerTag(), is(equalTo("11DTP")));
        assertThat(mt.isNewBoard, is(equalTo(false)));
        assertThat(mt.isNewOpponent, is(equalTo(false)));

        mt = it.next();
        assertThat(mt.getBoardId(), is(equalTo("board_0")));
        assertThat(mt.getGameType(), is(equalTo(OMAHA)));
        assertThat(mt.getOpponentGamerTag(), is(equalTo("11DTP")));
        assertThat(mt.isNewBoard, is(equalTo(false)));
        assertThat(mt.isNewOpponent, is(equalTo(false)));

        mt = it.next();
        assertThat(mt.getBoardId(), is(equalTo("board_1")));
        assertThat(mt.getGameType(), is(equalTo(HIGHCARD)));
        assertThat(mt.getOpponentGamerTag(), is(equalTo("11DTP")));
        assertThat(mt.isNewBoard, is(equalTo(true)));
        assertThat(mt.isNewOpponent, is(equalTo(false)));
    }

    @Test
    public void testMatchupIteratorLength() {
        RoundResultsEmit roundResultsEmit = getResultsEmitFromFile("results_new.json");
        RoundResults roundResults = new RoundResults(
                roundResultsEmit, this.currentRound, this.currentGame, this.demoUser);

        Iterator<MatchupTransition> it = roundResults.iterator();
        int count = 0;
        while (it.hasNext()) {
            count += 1;
            it.next();
        }
        assertThat(count, is(equalTo(36)));
    }
}
