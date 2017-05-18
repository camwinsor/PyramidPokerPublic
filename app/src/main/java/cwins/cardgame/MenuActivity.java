package cwins.cardgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayerBuffer;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.nearby.Nearby;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cwins.cardgame.dialogs.InviteDialog;
import cwins.cardgame.dialogs.OkDialog;
import cwins.cardgame.model.Session;

// TODO

/**
 * finished button for animations shown early; needs to be moved
 * suspending and resuming more than once before other users have moved forward
 *
 *
 *
 */


public class MenuActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        Players.LoadPlayersResult,
        RoomUpdateListener,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener,
        OnInvitationReceivedListener {
    private final static String TAG = "cardgame|MenuActivity--";
    private final static int RC_SIGN_IN = 9001;
    private final static int RC_SELECT_PLAYERS = 10000;
    private final static int RC_INVITATION_INBOX = 10001;
    private final static int RC_WAITING_ROOM = 10002;
    private final static int RC_RESOLVE_ERR = 10003;
    private final static int RC_REQUEST_ACHIEVEMENTS = 10004;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    private String mIncomingInvitationId = null;
    private String mMyId = null;
    private String mRoomId = null;
    private Integer MIN_PLAYERS = 2;
    private boolean mResolvingConnectionFailure = false;
    private boolean mWaitingRoomFinishedFromCode = false;
    private boolean mPlaying = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;
    private boolean mExplicitSignOut = false;
    private boolean mInSignInFlow = false;
    private ArrayList<Participant> mParticipants = null;
    private GoogleApiClient mGoogleApiClient;

    @Inject CardGameManager cgm;
    @Inject Session session;
    Integer desiredNumPlayers;

    TextView signInButton, signOutButton, settingsButton;
    final static int[] CLICKABLES = {
            R.id.signin_button,
            R.id.signout_button,
            R.id.invite_players_button,
            R.id.invites_button,
            R.id.current_games_button,
            R.id.settings_button
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FlashPokerApplication) getApplication()).component().inject(this);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_menu);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {

                // Establish user session with crashlytics
                // User id = time + device model
                String userDeviceModel = android.os.Build.MODEL;
                Calendar c = Calendar.getInstance();
                Integer min = c.get(Calendar.MINUTE);
                String time_and_model = min.toString() + " + " + userDeviceModel;
                Crashlytics.log(Log.DEBUG, TAG, "logUser: " + time_and_model);
                logUser(time_and_model);
            }
        }

        // Prevent phone from entering sleep
        keepScreenOn();

        // Log screen density for this device
        Float density = getResources().getDisplayMetrics().density;
        Crashlytics.log(Log.DEBUG, TAG, "Screen Density: " + density.toString());

        // Start google api client
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.MESSAGES_API)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES);
        mGoogleApiClient = builder.build();

        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }

        signInButton = (TextView) (findViewById(R.id.signin_button));
        signOutButton = (TextView) (findViewById(R.id.signout_button));
        settingsButton = (TextView) (findViewById(R.id.settings_button));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Auto sign-in
        if (!mInSignInFlow && !mExplicitSignOut) {
            //will setSessionUser if connection succeeds
            mGoogleApiClient.connect();
            Crashlytics.log(Log.DEBUG, TAG, "onStart/Sign-In isConnecting: " +
                    mGoogleApiClient.isConnecting());
        }

        cgm.setMenuActivity(this);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("GAME_CREATION_ERROR", false)) {
                showGameCreationErrorDialog();
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    public void updateCheevoProgress(Integer recordRoundsPlayed) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && recordRoundsPlayed > 0) {
            Games.Achievements.setSteps(mGoogleApiClient, getString(R.string.cheevo_1), recordRoundsPlayed);
            Games.Achievements.setSteps(mGoogleApiClient, getString(R.string.cheevo_2), recordRoundsPlayed);
            Games.Achievements.setSteps(mGoogleApiClient, getString(R.string.cheevo_3), recordRoundsPlayed);
            Games.Achievements.setSteps(mGoogleApiClient, getString(R.string.cheevo_4), recordRoundsPlayed);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() && mRoomId != null) {
//            leaveRoom();
//        }
        Crashlytics.log(Log.DEBUG, TAG, "Activity Stopped. Disconnecting googleapiclient");
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crashlytics.log(Log.DEBUG, TAG, "Activity Destroyed");
        mGoogleApiClient.disconnect();
    }

    private void showGameCreationErrorDialog() {
        showOkDialog(R.string.game_creation_error, null);

//        AlertDialog.Builder builder;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            builder = new AlertDialog.Builder(this, Theme_Material_Dialog_Alert);
//        } else {
//            builder = new AlertDialog.Builder(this);
//        }
//
//        builder.setMessage("Unfortunately there was an error while creating the game. " +
//                "\nPlease try again.");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case (R.id.signin_button):
                // start the asynchronous sign in flow
                mSignInClicked = true;
                mResolvingConnectionFailure = false;
                mGoogleApiClient.connect();
                Crashlytics.log(Log.DEBUG, TAG, "Sign-In button clicked. Is Connecting: " +
                        mGoogleApiClient.isConnecting());
                break;
            case (R.id.signout_button):
                // user wants to sign out
                Crashlytics.log(Log.DEBUG, TAG, "Sign-Out button clicked.");
                mSignInClicked = false;
                mExplicitSignOut = true;
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();

                    // show sign-in button, hide the sign-out button
                    signInButton.setVisibility(View.VISIBLE);
                    signOutButton.setVisibility(View.GONE);
                    settingsButton.setVisibility(View.GONE);
//                    invite_players_button.setVisibility(View.GONE);
//                    current_games_button.setVisibility(View.GONE);
//                    invites_button.setVisibility(View.GONE);
                }
                break;
            case (R.id.invite_players_button):
                // show list of invitable players
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 5);
                    startActivityForResult(intent, RC_SELECT_PLAYERS);
                } else {
                    showMustSignInDialog();
                }
                break;
            case (R.id.invites_button):
                // show list of pending invitations
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
                    startActivityForResult(intent, RC_INVITATION_INBOX);
                } else {
                    showMustSignInDialog();
                }
                break;
            case (R.id.current_games_button):
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    final Intent cgIntent = new Intent(this, GamesListActivity.class);
                    startActivity(cgIntent);
                } else {
                    showMustSignInDialog();
                }
                break;
            case (R.id.settings_button):

                /** TESTING DIALOGS **/

                showOkDialog(R.string.ok_test, "combywomby");


//                cheevo progress
//                startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
//                        RC_REQUEST_ACHIEVEMENTS);
        }
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        Crashlytics.log(Log.DEBUG, TAG, "onActRes/req: " + request + " response: " +
                response + " data: " + data);

        switch (request) {
            case RC_SELECT_PLAYERS:
                // we got the result from the "select players" UI -- ready to create the room
                handleSelectPlayersResult(response, data);
                desiredNumPlayers = 1;
                cgm.hostGame();
                Crashlytics.log(Log.DEBUG, TAG, "Invitees selected, creating waiting room, setting is_host == " +
                        session.isHosting());
                break;
            case RC_INVITATION_INBOX:
                // we got the result from the "select invitation" UI (invitation inbox). We're
                // ready to accept the selected invitation:
                handleInvitationInboxResult(response, data);
                break;
            case RC_WAITING_ROOM:
                // we got the result from the "waiting room" UI.
                if (response == Activity.RESULT_OK) {
                    // ready to start playing
                    if (session.isHosting()) {
                        Crashlytics.log(Log.DEBUG, TAG, "Setting desired players at: "
                                + desiredNumPlayers);
                        session.setExpectedNumPlayers(desiredNumPlayers);
                        Crashlytics.log(Log.DEBUG, TAG, "Starting Main Activity with game_id (" +
                                session.getActiveGame().getGameId() +
                                ") and desiredNumPlayers (" + desiredNumPlayers + ")");
                    }

                    // increments the games played cheevos
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.cheevo_5), 1);
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.cheevo_6), 1);
                    Games.Achievements.increment(mGoogleApiClient, getString(R.string.cheevo_7), 1);

                    final Intent theIntent = new Intent(this, MainActivity.class);
                    startActivity(theIntent);

                } else if (response == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                    // player indicated that they want to leave the room
                    Crashlytics.log(Log.DEBUG, TAG, "RESULT_LEFT_ROOM");
                } else if (response == Activity.RESULT_CANCELED) {
                    Crashlytics.log(Log.DEBUG, TAG, "RESULT_CANCELLED");
                    // Dialog was cancelled
                    Room room = data.getExtras().getParcelable(Multiplayer.EXTRA_ROOM);
                    leaveRoom(room);
                    cgm.cancelHostGame();
                }
                break;
            case RC_SIGN_IN:
                Crashlytics.log(Log.DEBUG, TAG, "onActivityResult with requestCode:RC_SIGN_IN, responseCode="
                        + response + ", intent=" + data);
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (response == RESULT_OK) {
                    Crashlytics.log(Log.DEBUG, TAG, "connecting google client from Sign-In button.");
                    mGoogleApiClient.connect();
                } else {
                    // TODO: error handling
//                    BaseGameUtils.showActivityResultError(this,request,response, R.string.signin_other_error);
                }
                break;
            case RC_REQUEST_ACHIEVEMENTS:
                if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                    showErrorDialog(response);
                }
            case REQUEST_RESOLVE_ERROR:
                Crashlytics.log(Log.DEBUG, TAG, "reqreserr - connecting...");
                mGoogleApiClient.connect();

        }
        super.onActivityResult(request, response, data);
    }

    private void handleSelectPlayersResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Crashlytics.log(Log.DEBUG, TAG, "Select players UI cancelled, response: " + response);
            cgm.cancelHostGame();
            session.setHosting(false);
            return;
        }
        Crashlytics.log(Log.DEBUG, TAG, "Select players UI succeeded.");

        session.setHosting(true);

        // get the invitee list
        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Crashlytics.log(Log.DEBUG, TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Crashlytics.log(Log.DEBUG, TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Crashlytics.log(Log.DEBUG, TAG, "Creating room...");
        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.addPlayersToInvite(invitees);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        if (autoMatchCriteria != null) {
            rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
        }
        keepScreenOn();
//        resetGameVars();
        Games.RealTimeMultiplayer.create(mGoogleApiClient, rtmConfigBuilder.build());
        Crashlytics.log(Log.DEBUG, TAG, "Room created, waiting for it to be ready...");
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        RoomConfig.Builder builder = RoomConfig.builder(this);
        builder.setMessageReceivedListener(this);
        builder.setRoomStatusUpdateListener(this);
        return builder;
    }

    void leaveRoom(Room room) {
//        Crashlytics.log(Log.DEBUG, TAG, "Leaving room. Room Id = " + mRoomId);
        stopKeepingScreenOn();
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, room.getRoomId());
        mRoomId = null;
    }

    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Crashlytics.log(Log.DEBUG, TAG, "handleInvInboxRes/invitation inbox UI cancelled, response: " +
                    response);
            return;
        }

        Crashlytics.log(Log.DEBUG, TAG, "handleInvInboxRes/Invitation inbox UI succeeded");
        Invitation inv = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        acceptInviteToRoom(inv.getInvitationId());
    }

    void acceptInviteToRoom(String invId) {
        // accept the invitation
        Crashlytics.log(Log.DEBUG, TAG, "acceptInviteToRoom/Accepting invitation: " + invId);
        RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
        roomConfigBuilder.setInvitationIdToAccept(invId)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
        keepScreenOn();
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
    }

    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        mIncomingInvitationId = invitation.getInvitationId();
        String inviter = invitation.getInviter().getDisplayName();

        Crashlytics.log(Log.DEBUG, TAG, "onInvitationReceived/Invited by: " +
                invitation.getInviter().getDisplayName());

        showInviteReceivedDialog(inviter, invitation);
    }

    private void showInviteReceivedDialog(String inviter, Invitation inv) {
        InviteDialog inviteDialog = new InviteDialog(MenuActivity.this, inv, inviter);
        inviteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        inviteDialog.show();

//        AlertDialog.Builder sirdBuilder = new AlertDialog.Builder(this);
//        final Invitation invite = inv;
//        sirdBuilder
//                .setTitle("New Invite Received")
//                .setMessage(inviter + " has sent you a game invite")
//                .setCancelable(false)
//                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                })
//                .setPositiveButton("Join Game", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        acceptInviteToRoom(invite.getInvitationId());
//                    }
//                });
//        sirdBuilder.show();
    }

    public void acceptInvite(Invitation invite) {
        acceptInviteToRoom(invite.getInvitationId());
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        if (mIncomingInvitationId != null && mIncomingInvitationId.equals(invitationId)) {
            mIncomingInvitationId = null;
            Crashlytics.log(Log.DEBUG, TAG, "Invite removed");
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        signInButton.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);
        settingsButton.setVisibility(View.VISIBLE);
//        invite_players_button.setVisibility(View.VISIBLE);
//        current_games_button.setVisibility(View.VISIBLE);
//        invites_button.setVisibility(View.VISIBLE);

        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        String googleId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String gamerTag = player.getDisplayName();

        Crashlytics.log(Log.DEBUG, TAG, "onConnected/googleId: " + googleId);
        Crashlytics.log(Log.DEBUG, TAG, "onConnected/gamerTag: " + gamerTag);
        cgm.setSessionUser(googleId, gamerTag);

        if (connectionHint != null) {
            Crashlytics.log(Log.DEBUG, TAG, "onConnected/connectionHint: " +
                    connectionHint.toString());
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Crashlytics.log(Log.DEBUG, TAG,"onConnected: connection hint has a room invite.");
                acceptInviteToRoom(inv.getInvitationId());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Crashlytics.log(Log.DEBUG, TAG, "Connection suspended. Attempting reconnect...");
        mGoogleApiClient.connect();
    }

    private void showMustSignInDialog() {
        AlertDialog.Builder smsidBuilder = new AlertDialog.Builder(this);
        Crashlytics.log(Log.DEBUG, TAG, "showing mustSignInDialog");

        // TODO make better
        smsidBuilder
                .setTitle("Error")
                .setMessage("You must sign in to use this feature")
                .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        smsidBuilder.show();
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Crashlytics.log(Log.DEBUG, TAG, "onRoomCreated not successful, statusCode: " +
                    statusCode);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error: Unstable connection to Google Play services")
                    .setMessage("Please reconnect and try again")
                    .setCancelable(false)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();
            // show error message, return to menu
        } else {
            mRoomId = room.getRoomId();
            Crashlytics.log(Log.DEBUG, TAG, "Room created successfully, Room ID: " + mRoomId);
            showWaitingRoom(room);
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Crashlytics.log(Log.DEBUG, TAG, "onJoinedRoom not successful, statusCode: "
                    + statusCode);

            // show error message, return to menu
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error: Unstable connection to Google Play services")
                    .setMessage("Please reconnect and try again")
                    .setCancelable(false)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.show();
            return;
        }
        showWaitingRoom(room);
    }

    void showWaitingRoom(Room room) {
        Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, 1);

        // show waiting room UI
        startActivityForResult(i, RC_WAITING_ROOM);
    }

    @Override
    public PlayerBuffer getPlayers() {
        return null;
    }

    @Override
    public void release() {
    }

    @Override
    public Status getStatus() {
        return null;
    }


    @Override
    public void onLeftRoom(int i, String s) {
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Crashlytics.log(Log.DEBUG, TAG, "onRoomConnected, statusCode: " + statusCode);
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Crashlytics.log(Log.DEBUG, TAG, "onRoomAutomatching=");
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> list) {
        updateRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> list) {
        updateRoom(room);
        if (session.isHosting()) {
            desiredNumPlayers += 1;
        }
    }

    @Override
    public void onPeerLeft(Room room, List<String> list) {
        updateRoom(room);
        if (session.isHosting()) {
            desiredNumPlayers -= 1;
        }
    }

    @Override
    public void onConnectedToRoom(Room room) {
        //get participants and my ID:
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if (mRoomId == null) {
            mRoomId = room.getRoomId();
        }
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        // show error message XXX if applicable!
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Error: Disconnected from room")
//                .setMessage("Sorry, it seems you have been disconnected from the game room. " +
//                        "Check your connection to Google Play Games and try again.")
//                .setCancelable(false)
//                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        builder.show();

        mRoomId = null;
    }

    // Hosting client sends game_id to connected peers
    // Receiving clients send game_id to server to join game

    @Override
    public void onPeersConnected(Room room, List<String> list) {
        updateRoom(room);

        if (session.isHosting() && session.getActiveGame().getGameId() != null) {
            sendGameIdMessage(false);
        } else {
            // TODO: handle errors!
        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) {
        updateRoom(room);
        desiredNumPlayers = list.size();
    }

    public void sendGameIdMessage(Boolean fromCGM) {
        if (session.isHosting()) {
            //make JSON
            String gameId = session.getActiveGame().getGameId().toString();
            byte[] gameIdMessage = gameId.getBytes(Charset.forName("UTF-8"));

            Crashlytics.log(Log.DEBUG, TAG, "Sending gameId message: " + gameIdMessage.toString());
            Crashlytics.log(Log.DEBUG, TAG, "Google Room ID = " + mRoomId);

            if (mRoomId != null) {
                for (Participant p : mParticipants) {
                    if (!p.getParticipantId().equals(mMyId)) {
                        Games.RealTimeMultiplayer.sendReliableMessage(
                                mGoogleApiClient, null, gameIdMessage,
                                mRoomId, p.getParticipantId());
                    }
                }
            } else {
                //TODO: something?
                // this means the host cancelled the invite screen
//                cgm.cancelHostGame();

                //showGoogleConnectionError();
            }

            Crashlytics.log(Log.DEBUG, TAG, "onPeersConnected/sendGameIdMessage/game_id = " +
                    gameId + "  from CGM: " + fromCGM);
        }
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] b = realTimeMessage.getMessageData();

        String gameIdMessage = new String(b, Charset.forName("UTF-8"));
        Integer gameId = Integer.parseInt(gameIdMessage);
        Crashlytics.log(Log.DEBUG, TAG, "Setting active with id: " + gameId.toString());
        if (!session.isHosting()) {
            session.createNewGame(gameId);
        }
    }

    void updateRoom(Room room) {
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
            // do something?
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Crashlytics.log(Log.DEBUG, TAG, "onConnectionFailed result: " + result.toString());
        if (mResolvingConnectionFailure) {
            // Already attempting to resolve an error.
            Crashlytics.log(Log.DEBUG, TAG, "already resolving...");
            return;
        } else if (result.hasResolution()) {
            try {
                Crashlytics.log(Log.DEBUG, TAG, "connection failure has resolution...");
                mResolvingConnectionFailure = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            Crashlytics.log(Log.DEBUG, TAG, "BGU resolution attempting...");

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
//            if (!BaseGameUtils.resolveConnectionFailure(this,
//                    mGoogleApiClient, result,
//                    RC_SIGN_IN, "There was an issue with sign in. Please try again later.")) {
//                mResolvingConnectionFailure = false;
//            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingConnectionFailure = false;
        }
    }

    @Override
    public void onP2PConnected(String s) { }

    @Override
    public void onP2PDisconnected(String s) { }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        Crashlytics.log(Log.DEBUG, TAG, "showErrorDialog/errorCode: " + errorCode);

    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingConnectionFailure = false;
    }

    public void showSocketDisconnect() {
        //change to progressdialog??
        showOkDialog(R.string.socket_disconnect, null);

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Error: Server Disconnection");
//        builder.setMessage(
//                "We're sorry, it seems your connection to the game server has been severed. " +
//                        "Attempting to reconnect...");
//        Crashlytics.log(Log.DEBUG, TAG, "Socket disconnected message sent to user");
//        builder.show();
    }

    public void showSocketReconnected() {
        showOkDialog(R.string.socket_reconnected, null);

//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Error: Server Disconnection");
//        builder.setMessage(
//                "Reconnected to server successfully!");
//        Crashlytics.log(Log.DEBUG, TAG, "Socket reconnected message sent to user");
//        builder.show();

    }

//    public void showGoogleConnectionError() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Error: connection problem with Google Play");
//        builder.setCancelable(false);
//        builder.setMessage("Connection to waiting room lost.");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                leaveRoom();
//            }
//        });
//        Crashlytics.log(Log.DEBUG, TAG, "Google Servers Error message sent to user");
//        builder.show();
//    }



    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void logUser(String userInstance) {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier(userInstance);
//        Crashlytics.setUserEmail("user@fabric.io");
//        Crashlytics.setUserName(user);
    }

    public void showPlayerResumedAndWaiting(HashMap<String, String> player_res_details) {
        final String player = player_res_details.get("gamer_tag");
        final String game = player_res_details.get("game_id");
        Integer game_int = Integer.parseInt(game);

        showOkDialog(R.string.resumed_waiting, player);

//        HashMap<String, Object> selected_game_details = cgm.getGameMap().get(game_int);
//        final String date_time = (String) selected_game_details.get("date_created");

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("A player wishes to resume one of your current games");
////        builder.setMessage(player + " is waiting for you resume play in a game created on " + date_time
////                + "\nCheck your current games list to resume.");
//        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                Crashlytics.log(Log.DEBUG, TAG, "Player acknowledged game " + game +
//                        " resumed by " + player);
//                dialog.dismiss();
//            }
//        });
//        builder.show();
    }

    public void showOkDialog(int type, String gamerTag) {
        OkDialog dialog = new OkDialog(MenuActivity.this, type, gamerTag);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void menuExtension(View view) {


//        Toast.makeText(this, "No settings yet :p", Toast.LENGTH_SHORT).show();
//        throw new RuntimeException("This is a crash");
    }
}
