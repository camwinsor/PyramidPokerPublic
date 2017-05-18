package cwins.cardgame;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.BuildConfig;
import com.google.gson.Gson;

import java.net.URISyntaxException;
import java.util.ArrayList;

import cwins.cardgame.serverEmitListeners.OnGameHostedListener;
import cwins.cardgame.serverEmitListeners.OnGameStartedListener;
import cwins.cardgame.serverEmitListeners.OnPlayerJoinedListener;
import cwins.cardgame.serverEmitListeners.OnPlayerLeftListener;
import cwins.cardgame.serverEmitListeners.OnPlayerReadyListener;
import cwins.cardgame.serverEmitListeners.OnPlayerResumedListener;
import cwins.cardgame.serverEmitListeners.OnPlayerSuspendedListener;
import cwins.cardgame.serverEmitListeners.OnResultsListener;
import cwins.cardgame.serverEmitListeners.OnRoundStartListener;
import cwins.cardgame.serverEmitListeners.OnUserInfoListener;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.HandChoices;
import cwins.cardgame.model.Round;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;
import cwins.cardgame.model.emit.client.ClientEmit;
import cwins.cardgame.model.emit.client.FetchStateEmit;
import cwins.cardgame.model.emit.client.HandChoicesEmit;
import cwins.cardgame.model.emit.client.HostGameEmit;
import cwins.cardgame.model.emit.client.JoinGameEmit;
import cwins.cardgame.model.emit.client.LeaveGameEmit;
import cwins.cardgame.model.emit.client.LoginEmit;
import cwins.cardgame.model.emit.client.ReadyNextRoundEmit;
import cwins.cardgame.model.emit.client.ResumeGameEmit;
import cwins.cardgame.model.emit.client.StartGameEmit;
import cwins.cardgame.model.emit.client.SuspendGameEmit;
import cwins.cardgame.model.emit.server.UserInfoGameEmit;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class CardGameManager {

    // client to server
    private static final String MESSAGE_TYPE_LOGIN = "login";
    private static final String MESSAGE_TYPE_HOST_GAME = "host_game";
    private static final String MESSAGE_TYPE_JOIN_GAME = "join_game";
    private static final String MESSAGE_TYPE_START_GAME = "start_game";
    private static final String MESSAGE_TYPE_RESUME_GAME = "resume_game";
    private static final String MESSAGE_TYPE_SUSPEND_GAME = "suspend_game";
    private static final String MESSAGE_TYPE_LEAVE_GAME = "leave_game";
    private static final String MESSAGE_TYPE_FETCH_STATE = "fetch_state";
    private static final String MESSAGE_TYPE_HAND_CHOICES = "hand_choices";
    private static final String MESSAGE_TYPE_READY_NEXT_ROUND = "ready_for_next_round";

    // server to client
    private static final String MESSAGE_TYPE_GAME_HOSTED = "game_hosted";
    private static final String MESSAGE_TYPE_GAME_STARTED = "game_started";
    private static final String MESSAGE_TYPE_ROUND_STARTED = "round_start";
    private static final String MESSAGE_TYPE_PLAYER_JOINED = "player_joined";
    private static final String MESSAGE_TYPE_PLAYER_READY = "player_ready";
    private static final String MESSAGE_TYPE_PLAYER_SUSPENDED = "player_suspended";
    private static final String MESSAGE_TYPE_PLAYER_RESUMED = "player_resumed";
    private static final String MESSAGE_TYPE_PLAYER_LEFT = "player_left";
    private static final String MESSAGE_TYPE_USER_INFO = "user_info";
    private static final String MESSAGE_TYPE_RESULTS = "results";

    private MainActivity mainActivity;
    private MenuActivity menuActivity;
    private ScoringActivity scoringActivity;
    private GamesListActivity gamesListActivity;

    private Socket mSocket;
    private String TAG = "cardgame|CGManager--";
    private final Gson gson = new Gson();
    private String serverUrl;
    private Session currentSession;

    private static CardGameManager theInstance;


    public CardGameManager(Session session, String serverUrl) {
        this.serverUrl = serverUrl;
        this.currentSession = session;
    }
;ljhsfhgsdhgsdhf
    public void setMenuActivity(final MenuActivity menu_app) {
        Crashlytics.log(Log.DEBUG, TAG, "setMenuActivity");
        currentSession.setCurrentActivity("menu");
        this.menuActivity = menu_app;
    }

    public void setMainActivity(final MainActivity main_app) {
        Crashlytics.log(Log.DEBUG, TAG, "setMainActivity");
        this.mainActivity = main_app;
        currentSession.setCurrentActivity("main");
    }

    public void setScoringActivity(final ScoringActivity score_app) {
        Crashlytics.log(Log.DEBUG, TAG, "setScoringActivity");
        currentSession.setCurrentActivity("scoring");
        this.scoringActivity = score_app;
    }

    public void setGamesListActivity(final GamesListActivity games_app) {
        Crashlytics.log(Log.DEBUG, TAG, "setGamesListActivity");
        currentSession.setCurrentActivity("games");
        this.gamesListActivity = games_app;
    }

    private void socketing() {
        try {
            mSocket = IO.socket(serverUrl);
        } catch (URISyntaxException e) {
            Crashlytics.log(Log.DEBUG, TAG, "socket exception: " + e.toString());
        }

        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                currentSession.setConnectedToServer(true);
                if (!currentSession.isLoggedInToServer()) {
                    login();
                }

                String serverId = mSocket.id();
                currentSession.getCurrentUser().setServerId(serverId);
                Crashlytics.log(Log.DEBUG, TAG, "Connected to Server with serverId: " + serverId);
            }
        });

        /** Game Flow
         * e || i (e = emit to server, i = incoming from server)
         * prs (player_round_state : a = waiting_for_choices, b = waiting_for_ready, c = ready)
         * * (players : gamer_tag, google_id, player_round_state, player_state, score)
         *
         *
         * New Game
         *
         * e (one player that is_host) host_game
         * i game_hosted : game_id
         *      -host sends game_id to all other players
         * e (all other players) join_game : game_id
         * i game_started : array of players {google_id:gamer_tag}
         *
         *
         * New Round -- all prs = c
         *
         * i round_start : round_id, game_id, hole_cards, *players, resuming, round_num
         * prs = a
         * e hand_choices : round_id, game_id, choices, google_id
         * prs = b
         * i results : round_id, ....
         * e ready_for_next_round : round_id, game_id, google_id
         * prs = c
         *
         * End Round
         *
         */

        // server going to send connected_players data -- use this
        mSocket.on(MESSAGE_TYPE_ROUND_STARTED, new OnRoundStartListener(this, currentSession))
                .on(MESSAGE_TYPE_GAME_HOSTED, new OnGameHostedListener(this, currentSession))
                .on(MESSAGE_TYPE_GAME_STARTED, new OnGameStartedListener(currentSession))
                .on(MESSAGE_TYPE_USER_INFO, new OnUserInfoListener(this, currentSession))
                .on(MESSAGE_TYPE_PLAYER_JOINED, new OnPlayerJoinedListener(this, currentSession))
                .on(MESSAGE_TYPE_PLAYER_SUSPENDED, new OnPlayerSuspendedListener(this, currentSession))
                .on(MESSAGE_TYPE_PLAYER_RESUMED, new OnPlayerResumedListener(this, currentSession))
                .on(MESSAGE_TYPE_PLAYER_LEFT, new OnPlayerLeftListener(this, currentSession))
                .on(MESSAGE_TYPE_RESULTS, new OnResultsListener(this, currentSession))
                .on(MESSAGE_TYPE_PLAYER_READY, new OnPlayerReadyListener(this, currentSession))

        .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Crashlytics.log(Log.DEBUG, TAG, "SOCKET DISCONNECTED");
                    currentSession.setLoggedInToServer(false);

                    switch (currentSession.getCurrentActivity()) {
                        case "main":
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mainActivity.showAlertDialog("socketDisconnect", null);
                                    Crashlytics.log(Log.DEBUG, TAG, "sending socket DISCONNECT message from MAIN");
                                }
                            });
                            break;
                        case "scoring":
                            scoringActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    scoringActivity.showAlertDialog("socketDisconnect", null);
                                    Crashlytics.log(Log.DEBUG, TAG, "sending socket DISCONNECT message from SCORE");
                                }
                            });
                            break;
                        case "menu":
                            //needs refactoring
                            menuActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    menuActivity.showSocketDisconnect();
                                    Crashlytics.log(Log.DEBUG, TAG, "sending socket DISCONNECT message from MENU");
                                }
                            });

                            break;
                        case "games":
                            //needs refactoring
                            gamesListActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gamesListActivity.showSocketDisconnect();
                                    Crashlytics.log(Log.DEBUG, TAG, "sending socket DISCONNECT message from GAMES");
                                }
                            });
                            break;
                    }
                }
        });

    }

    public void setSessionUser(String googleId, String gamerTag) {
        if (currentSession.getCurrentUser() == null || !currentSession.getCurrentUser().getGoogleId().equals(googleId)) {
            currentSession.resetSessionVars();
            currentSession.setCurrentUser(new User(googleId, gamerTag));
        }

        if (!currentSession.isConnectedToServer()) {
            socketing();
            connectToServer();
        }
    }

    public void connectToServer() {
        mSocket.connect();
    }

    public boolean isSocketConnected() {
        Crashlytics.log(Log.DEBUG, TAG, "Checking if socket connected: " + mSocket.connected());
        return mSocket.connected();
    }

    /** Client emits to server **/

    public void login() {
        String version = BuildConfig.VERSION_NAME;
        User currentUser = currentSession.getCurrentUser();

        LoginEmit loginEmit = new LoginEmit(currentUser, version);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting login: " + loginEmit.toString());
        doEmit(MESSAGE_TYPE_LOGIN, loginEmit);
    }

    // called by host (in menu act) to get a game_id to be sent (via Google Play invite) to invited users
    public void hostGame() {
        HostGameEmit hostGameEmit = new HostGameEmit();
        Crashlytics.log(Log.DEBUG, TAG, "Emitting host game.");
        doEmit(MESSAGE_TYPE_HOST_GAME, hostGameEmit);
        currentSession.setHosting(true);
    }

    public void cancelHostGame() {
        currentSession.deactivateGame();
    }

    // called by invited users to join a new game, emits join_game with game_id
    public void joinGame(Integer gameId) {
        JoinGameEmit joinGameEmit = new JoinGameEmit(gameId);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting join game: " + joinGameEmit.toString());
        doEmit(MESSAGE_TYPE_JOIN_GAME, joinGameEmit);
    }

    public void checkPlayersForGameStart() {
        if (currentSession.isHosting() &&
                currentSession.getExpectedNumPlayers() != null &&
                currentSession.getExpectedNumPlayers() == currentSession.getActiveGame().getPlayers().size() &&
                !currentSession.getActiveGame().isStarted()) {
            Log.d(TAG, "host starting game");
            startGame();
        }
    }

    public void startGame() {
        Crashlytics.log(Log.DEBUG, TAG, "Emitting start game.");
        currentSession.getActiveGame().setStarted(true);
        StartGameEmit startGameEmit = new StartGameEmit(
                currentSession.getActiveGame().getGameId().toString());
        doEmit(MESSAGE_TYPE_START_GAME, startGameEmit);
        currentSession.getActiveGame().setAllPlayersActive(true);
        currentSession.setGameActive(true);
    }

    public void fetchState() {
        FetchStateEmit fetchStateEmit = new FetchStateEmit();
        Crashlytics.log(Log.DEBUG, TAG, "Emitting fetch state.");
        doEmit(MESSAGE_TYPE_FETCH_STATE, fetchStateEmit);
    }

    public void resumeGame(Integer gameId) {
        currentSession.setCurrentUserResuming(true);
        ResumeGameEmit resumeGameEmit = new ResumeGameEmit(gameId);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting resume game: " + resumeGameEmit.toString());
        doEmit(MESSAGE_TYPE_RESUME_GAME, resumeGameEmit);
    }

    public void loadNextRound() {
        if (currentSession.getCurrentActivity().equals("main")) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.showWaitingDialog();
                }
            });
        }
        Integer gameId = currentSession.getActiveGame().getGameId();
        Integer roundId = currentSession.getActiveGame().getCurrentRound().getRoundId();
        ReadyNextRoundEmit readyNextRoundEmit = new ReadyNextRoundEmit(gameId, roundId);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting ready next round: " + readyNextRoundEmit.toString());
        doEmit(MESSAGE_TYPE_READY_NEXT_ROUND, readyNextRoundEmit);
    }

    // called by user suspending active game, emits suspend_game with active_game_id
    public void suspendGame() {
        Integer gameId = currentSession.getActiveGame().getGameId();
        SuspendGameEmit suspendGameEmit = new SuspendGameEmit(gameId);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting suspend game: " + suspendGameEmit.toString());
        doEmit(MESSAGE_TYPE_SUSPEND_GAME, suspendGameEmit);

        currentSession.deactivateGame();
    }

    public void leaveGame(Integer gameId) {
        LeaveGameEmit leaveGameEmit = new LeaveGameEmit(gameId);
        Crashlytics.log(Log.DEBUG, TAG, "Emitting leave game: " + leaveGameEmit.toString());
        doEmit(MESSAGE_TYPE_LEAVE_GAME, leaveGameEmit);

        currentSession.removeSavedGame(gameId);
    }

    public void submitHandChoices() {
        HandChoices mHandChoices =
                currentSession.getActiveGame().getCurrentRound().getUserHandChoices();

        HandChoicesEmit handChoicesEmit =
                new HandChoicesEmit(currentSession.getActiveGame().getCurrentRound().getRoundId(),
                        currentSession.getActiveGame().getGameId(),
                        currentSession.getCurrentUser().getGoogleId(),
                        mHandChoices);

        Crashlytics.log(Log.DEBUG, TAG, "Emitting hand choices: " + handChoicesEmit.toString());
        doEmit(MESSAGE_TYPE_HAND_CHOICES, handChoicesEmit);
    }

    public void recordHandChoices(ArrayList<Integer> highCard, ArrayList<Integer> holdem, ArrayList<Integer> omaha) {
        HandChoices handChoices =
                new HandChoices(highCard, holdem, omaha, currentSession.getActiveGame().getCurrentRound().getHoleCards());

        currentSession.getActiveGame().getCurrentRound().addUserHandChoices(handChoices);
        currentSession.getActiveGame().getCurrentRound().setChoicesSubmitted(true);
    }

    private void doEmit(String messageType, ClientEmit emitObj) {
        mSocket.emit(messageType, emitObj.toJson());
    }

    /** End client emits **/

    // TODO change all choicesSubmitted checks to (if prs = waiting_for_choices)
    public boolean areChoicesSubmitted() {
        return currentSession.getActiveGame().choicesSubmitted();
    }

    /** Methods called to update UI from serverEmitListeners **/

    public void checkCheevoStatus(ArrayList<UserInfoGameEmit> userInfoGameEmits) {
        Integer recordRoundsPlayed = 0;
        for (UserInfoGameEmit userInfoGameEmit : userInfoGameEmits) {
            Integer roundsPlayed = userInfoGameEmit.getRoundsPlayed();
            if (roundsPlayed > recordRoundsPlayed) {
                recordRoundsPlayed = roundsPlayed;
            }
        }
        Log.d(TAG, "record rounds played: " + recordRoundsPlayed);
        final Integer recordUpdate = recordRoundsPlayed;

        //update cheevo status on google play
        menuActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuActivity.updateCheevoProgress(recordUpdate);
            }
        });
    }

    public void shareGameIdWithInvitees() {
        menuActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuActivity.sendGameIdMessage(true);
            }
        });
    }

    public void displayResults() {
        if (currentSession.getCurrentActivity().equals("scoring") &&
                currentSession.getActiveGame().getCurrentRound().isReadyForScoring()) {
            scoringActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scoringActivity.beginScoring();
                }
            });
        }

        // TODO find a better way
        currentSession.getCurrentUser().setPlayerRoundState("waiting_for_ready");
    }

    public void updateGameScores() {
        currentSession.getActiveGame().updateTotalScores();
    }

    public void dealCards() {
        // cards dealt, new cards not waiting. set true if user suspends before submitting cards.
        currentSession.getActiveGame().setNewCardsWaiting(false);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.showReadyLights();
                mainActivity.displayHoleCards(currentSession.getActiveGame().getCurrentRound().getHoleCards());
                mainActivity.setRound(currentSession.getActiveGame().getCurrentRound().getRoundNumberDisplay());
            }
        });
    }

    public void roundStartUp(Round currentRound) {
        Game activeGame = currentRound.getGame();

        String prs = currentRound.getPlayerRoundState(currentSession.getCurrentUser().getGoogleId());
        currentSession.getCurrentUser().setPlayerRoundState(prs);
        Log.d(TAG, "currentUser playerRoundState: " + prs);

        if (!currentRound.isResuming()) {
            // not a resuming roundStart meaning this is a new round for a game that was created
            // within this session. all the usual round start procedures apply to all players
            Log.d(TAG, "roundStart not resuming");
            activeGame.setNewCardsWaiting(true);
            // TODO race condition -
            // samsung hosts, does not load mainAct before roundStart triggers dealCards

            if (mainActivity != null) {
                dealCards();
            }
        } else if (currentRound.isResuming() && currentSession.isCurrentUserResuming()) {
            // this is a resuming roundStart and it applies to the currentUser - there may be other
            // players either still suspended or waiting in-game that this does not apply to
            Log.d(TAG, "roundStart isResuming && currentUser isResuming");
            activeGame.setNewRound(currentRound);
//            activeGame.setCardsDealt(true);
            currentSession.addSavedGame(activeGame);

            // no longer resuming
            currentSession.setCurrentUserResuming(false);

            if (prs.equals("waiting_for_ready")) {
                loadNextRound();
                currentSession.getActiveGame().getCurrentRound().setChoicesSubmitted(false);
            } else if (prs.equals("waiting_for_choices")) {
                activeGame.setNewCardsWaiting(true);
                Log.d(TAG, "roundStart user isResuming, dealCards");
                currentSession.setCurrentUserResuming(false);
                activeGame.getCurrentRound().setResuming(false);
                if (mainActivity != null) {
                    dealCards();
                } else {
                    //move to score activity w/ waiting dialog
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.showScoresActivity();
                        }
                    });
                }
                //prs cannot = waitingforready
            } else if (prs.equals("ready")) {
                if (mainActivity != null) {
                    dealCards();


//                    mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mainActivity.showWaitingDialog();
//                        }
//                    });
                }
            }
        } else {
            // this is a resuming roundStart that does not apply to the in-game and waiting
            // currentUser
            Log.d(TAG, "roundStart isResuming && currentUser not resuming");
            if (currentSession.getCurrentActivity().equals("main")) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mainActivity.waitingDialog != null) {
                            mainActivity.waitingDialog.dismiss();
                        }

                        if (currentRound.isReadyForScoring()) {
                            mainActivity.showScoresActivity();
                        } else if (activeGame.isNewCardsWaiting()) {
                            dealCards();
                        }
                    }
                });
            } else if (currentSession.getCurrentActivity().equals("scoring")) {
                Log.d(TAG, "user is still in scoring, setting newCardsWaiting = true");
                activeGame.setNewCardsWaiting(true);
            }
        }
    }

    public void showPlayerReady(String googleId) {
        if (!googleId.equals(currentSession.getCurrentUser().getGoogleId())
                && currentSession.getCurrentActivity().equals("main")) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.switchNextReadyLight();
                }
            });
        }
    }

    public void displaySuspendMessage(final String gamerTag) {
        switch (currentSession.getCurrentActivity()) {
            case "main":
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showAlertDialog("playerSuspended", gamerTag);
                    }
                });
                break;
            case "scoring":
                scoringActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoringActivity.showAlertDialog("playerSuspended", gamerTag);
                    }
                });
                break;
        }
    }

    public void displayResumedMessage(final String gamerTag) {
        switch (currentSession.getCurrentActivity()) {
            case "main":
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showAlertDialog("playerConnected", gamerTag);
                    }
                });
                break;
            case "scoring":
                scoringActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoringActivity.showAlertDialog("playerConnected", gamerTag);
                    }
                });
                break;
            case "menu":

                break;
            case "games":

                break;
        }
    }

    public void displayLeftMessage(final String gamerTag) {
        switch (currentSession.getCurrentActivity()) {
            case "main":
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.showAlertDialog("playerLeft", gamerTag);
                    }
                });
                break;
            case "scoring":
                scoringActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scoringActivity.showAlertDialog("playerLeft", gamerTag);
                    }
                });
                break;
            case "menu":

                break;
            case "games":

                break;
        }
    }
}
