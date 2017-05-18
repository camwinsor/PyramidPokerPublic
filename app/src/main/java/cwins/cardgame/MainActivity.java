package cwins.cardgame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cwins.cardgame.dialogs.ChooseDialog;
import cwins.cardgame.dialogs.OkDialog;
import cwins.cardgame.dialogs.PauseDialog;
import cwins.cardgame.dialogs.ScoreDialogCreator;
import cwins.cardgame.model.Card;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.HoleCards;
import cwins.cardgame.model.Session;

import static android.R.style.Theme_Material_Dialog_Alert;
import static android.graphics.PorterDuff.Mode.SRC_IN;

// FIX: signin, main score button

public class MainActivity extends BaseActivity implements View.OnTouchListener, View.OnDragListener {

    private String TAG = "cardgame|MainActivity--";
    private GoogleApiClient client;
    private CharSequence currentTag;
    private ArrayList<ViewGroup> selectionLayouts = new ArrayList<>();

    @Inject CardGameManager cgm;
    @Inject Session session;
    ArrayList<ImageView> readyLights;
    ProgressDialog waitingDialog;
    Dialog currentAlertDialog;
    AlertDialog scoresDialog;

    @BindView(R.id.main_bar) ViewGroup mainBarLayout;
    @BindView(R.id.suspend_main_button) Button suspendButton;
    @BindView(R.id.scores_main_button) Button scoresButton;
    @BindView(R.id.current_round_main) TextView currentRoundTv;
    @BindView(R.id.reset_selections_button) Button resetSelectionsButton;

    @BindView(R.id.player_ready_lights) ViewGroup readyLightsLayout;

    @BindView(R.id.ready_choices_button) Button readyChoicesButton;

    @BindView(R.id.high_card_choice) ViewGroup highCardLayout;
    @BindView(R.id.high_card_main) ImageView highCardMainIv;

    @BindView(R.id.holdem_choice) ViewGroup holdemLayout;
    @BindView(R.id.holdem_main_1) ImageView holdemMainIv1;
    @BindView(R.id.holdem_main_2) ImageView holdemMainIv2;

    @BindViews({R.id.holdem_main_1, R.id.holdem_main_2})
    List<ImageView> holdemChoiceIvs;

    @BindView(R.id.omaha_choice) ViewGroup omahaLayout;
    @BindView(R.id.omaha_main_1) ImageView omahaMainIv1;
    @BindView(R.id.omaha_main_2) ImageView omahaMainIv2;
    @BindView(R.id.omaha_main_3) ImageView omahaMainIv3;
    @BindView(R.id.omaha_main_4) ImageView omahaMainIv4;

    @BindViews({R.id.omaha_main_1, R.id.omaha_main_2, R.id.omaha_main_3, R.id.omaha_main_4})
    List<ImageView> omahaChoiceIvs;

    @BindView(R.id.hole_cards) ViewGroup holeCardsLayout;
//    @BindView(R.id.hole_card_1) ImageView holeCardIv1;
//    @BindView(R.id.hole_card_2) ImageView holeCardIv2;
//    @BindView(R.id.hole_card_3) ImageView holeCardIv3;
//    @BindView(R.id.hole_card_4) ImageView holeCardIv4;
//    @BindView(R.id.hole_card_5) ImageView holeCardIv5;
//    @BindView(R.id.hole_card_6) ImageView holeCardIv6;
//    @BindView(R.id.hole_card_7) ImageView holeCardIv7;

//    @BindViews({R.id.high_card_main, R.id.holdem_main_1, R.id.holdem_main_2, R.id.omaha_main_1, R.id.omaha_main_2, R.id.omaha_main_3, R.id.omaha_main_4})
//    List<ImageView> allChoicesIvs;

//    @BindViews({R.id.hole_card_1, R.id.hole_card_2, R.id.hole_card_3, R.id.hole_card_4, R.id.hole_card_5, R.id.hole_card_6, R.id.hole_card_7})
//    List<ImageView> holeCardsIvs;

    //TODO bug: 3plyrs in scoring, one drops and resumes while other two still in scoring.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FlashPokerApplication) getApplication()).component().inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // TODO: This still races on invoking onCreate before the CGM gets dealt its cards ??
        Crashlytics.log(Log.DEBUG, TAG, "onCreate");
        cgm.setMainActivity(this);

        selectionLayouts.add(highCardLayout);
        selectionLayouts.add(holdemLayout);
        selectionLayouts.add(omahaLayout);
//        selectionLayouts.add(holeCardsLayout);

        // Prevent phone from entering sleep
        keepScreenOn();

        highCardMainIv.setBackgroundResource(R.drawable.highcard_holder);

        for (ImageView current : holdemChoiceIvs) {
            current.setBackgroundResource(R.drawable.holdem_holder);
        }

        for (ImageView current : omahaChoiceIvs) {
            current.setBackgroundResource(R.drawable.omaha_holder);
        }

        readyChoicesButton.setOnClickListener(readySelectionsListener);
        suspendButton.setOnClickListener(suspendListener);
        scoresButton.setOnClickListener(scorePopUpListener);
        resetSelectionsButton.setOnClickListener(resetSelectionsListener);


        highCardLayout.setOnDragListener(this);
        omahaLayout.setOnDragListener(this);
        holdemLayout.setOnDragListener(this);
        holeCardsLayout.setOnDragListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        //if there was some error in game room creation, send user back to menu
        if (!session.isGameActive()) {
            returnToMenu(true);
        }

        displayUnreadyButton();

        //TODO: re-add views onStart ??

        //install backgrounds to card holder imageviews
        highCardMainIv.setBackgroundResource(R.drawable.highcard_holder);
        for (ImageView current : holdemChoiceIvs) {
            current.setBackgroundResource(R.drawable.holdem_holder);
            current.setPadding(1, 0, 1, 0);
        }
        for (ImageView current : omahaChoiceIvs) {
            current.setBackgroundResource(R.drawable.omaha_holder);
            current.setPadding(1, 0, 1, 0);
        }

        Game activeGame = session.getActiveGame();

        // join game if not host and not resuming
        if (activeGame.isNewGame() && !session.isHosting()) {
            Crashlytics.log(Log.DEBUG, TAG, "joining game with id: " + activeGame.getGameId().toString());
            cgm.joinGame(activeGame.getGameId());
        }

        // resume game if appropriate
        if (session.isCurrentUserResuming()) {
            cgm.resumeGame(activeGame.getGameId());
            Crashlytics.log(Log.DEBUG, TAG, "isCurrentUserResuming. post resumeGame(), allplayersactive = " +
                    activeGame.isAllPlayersActive());
//            Log.d(TAG, "PRS: " + activeGame.getPlayerState(session.getCurrentUser().getGoogleId()));
//            if (activeGame.getPlayerRoundState(session.getCurrentUser().getGoogleId()).equals("waiting_for_ready")) {
//                Log.d(TAG, "local prs = waiting_for_ready, loadNextRound()");
//                cgm.loadNextRound();
//            }

            if (!activeGame.isAllPlayersActive() || !activeGame.hasRoundStarted()) {
                Log.d(TAG, "first waiting");
                showWaitingDialog();
            } else if (activeGame.isAnyOpponentOnPreviousRound()) {
                Log.d(TAG, "second waiting");
                showWaitingDialog();
            }
        }

        // if hosting phone was slow to move from menu to main
        // check if numActivePlayers == numExpectedPlayers and
        // start game if necessary (usually called onPlayerJoined)
        cgm.checkPlayersForGameStart();

        // dealCards if roundStart has come and gone while your slow phone was switching activities
        if (activeGame.isNewCardsWaiting()) {
            Log.d(TAG, "dealing cards from newcardswaiting check");
            resetSelections();
            session.getActiveGame().setNewCardsWaiting(false);

        }


    }

    //TODO fix on minimize; prob for other activities too
    @Override
    public void onRestart() {
        super.onRestart();
        cgm.setMainActivity(this);
        String prs = session.getCurrentUser().getPlayerRoundState();
        Crashlytics.log(Log.DEBUG, TAG, "onRestart. PRS = " + prs);

        if (prs.equals("waiting_for_choices")) {
            resetSelections();
        } else {
            replaceAllSelectionHolders();
            cgm.loadNextRound();
        }

//        // checks if this activity was restarted before user submitted choices
//        if (session.getCurrentUser().getPlayerRoundState().equals("waiting_for_ready")) {
//            replaceAllSelectionHolders();
//            //emit ready for next round to server and load waiting dialog
//            Log.d(TAG, "prs = wfr, loadNextRound");
//            cgm.loadNextRound();
//        } else {
//            resetSelections();
//        }

    }

    @Override
    public void onStop() {
        super.onStop();
        //TODO: necessary??
        if (session.isGameActive()) {
            session.setHosting(false);
            session.getActiveGame().setNewGame(false);
        }

//        if (cgm.areChoicesSubmitted()) {
//            Log.d(TAG, "Choices submitted true, resetting all imageviews to null");
//        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://cwins.cardgame/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void showReadyLights() {
        //display UNready lights
        Integer numPlayers = session.getActiveGame().getNumPlayers();
        Crashlytics.log(Log.DEBUG, TAG, "showing ready lights for numplayers = " + numPlayers);
//        readyLights = new ArrayList<>();

        for (int i=0; i<(numPlayers-1); i++) {
            ImageView view = new ImageView(this);
            view.setImageResource(R.drawable.unready_circle_sm);
            view.setPadding(5, 10, 5, 10);
            readyLightsLayout.addView(view);
            Log.d(TAG, "adding light");
        }
        readyLightsLayout.invalidate();
    }

    public void switchNextReadyLight() {
        for (int i=0; i<readyLightsLayout.getChildCount(); i++) {
            ImageView current = (ImageView) readyLightsLayout.getChildAt(i);
            if (current.getTag() == null) {
                current.setImageResource(R.drawable.ready_circle_sm);
                current.setTag("ready");
                return;
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && view.getTag() != null) {
            currentTag = (CharSequence) view.getTag().toString();
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag().toString());
            ClipData dragData = new ClipData(
                    (CharSequence) view.getTag().toString(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);

            view.setTag(null);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(dragData, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }

    public boolean onDrag(View layoutview, DragEvent dragevent) {
        int action = dragevent.getAction();

        ImageView sourceView = (ImageView) dragevent.getLocalState();
        LinearLayout sourceLayout = (LinearLayout) sourceView.getParent();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                for(int i=0; i<((ViewGroup)layoutview).getChildCount(); i++) {
                    ImageView nextChild = (ImageView) ((ViewGroup)layoutview).getChildAt(i);
                    if (nextChild.getDrawable() == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        nextChild.setBackgroundTintMode(SRC_IN);
                        nextChild.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        nextChild.invalidate();
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                for(int i=0; i<((ViewGroup)layoutview).getChildCount(); i++) {
                    ImageView nextChild = (ImageView) ((ViewGroup)layoutview).getChildAt(i);
                    if (nextChild.getDrawable() == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        nextChild.setBackgroundTintMode(SRC_IN);
                        nextChild.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        nextChild.invalidate();
                    }
                }
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                for(int i=0; i<((ViewGroup)layoutview).getChildCount(); i++) {
                    ImageView nextChild = (ImageView) ((ViewGroup)layoutview).getChildAt(i);
                    if (nextChild.getDrawable() == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        nextChild.setBackgroundTintMode(SRC_IN);
                        nextChild.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        nextChild.invalidate();
                    }
                }
                break;
            case DragEvent.ACTION_DROP:
                // Gets the item containing the dragged data
                ClipData.Item item = dragevent.getClipData().getItemAt(0);

                // Gets the text data from the item
                CharSequence dragData = item.getText();
                LinearLayout targetLayout = (LinearLayout) layoutview;

                // Cancel drop if source and target layout is the same
                if (sourceLayout == targetLayout) {
                    return false;
                }

                for (int i=0; i<(targetLayout.getChildCount()); i++) {
                    ImageView current = (ImageView) targetLayout.getChildAt(i);
                    // Place dragged card into first open spot in desired selection layout
                    if (current.getTag() == null) {
                        sourceLayout.removeView(sourceView);
                        targetLayout.removeView(current);
                        targetLayout.addView(sourceView, i);

                        // Replace background holder
                        replaceHolder(sourceLayout, null);
                        sourceView.setTag(dragData);
                        return true;
                    }
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                // Remove all drag-oriented tints
                for(int i=0; i<((ViewGroup)layoutview).getChildCount(); i++) {
                    ImageView nextChild = (ImageView) ((ViewGroup)layoutview).getChildAt(i);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        nextChild.setBackgroundTintMode(SRC_IN);
                        nextChild.setBackgroundTintList(null);
                        nextChild.invalidate();
                    }
                }

                // if drag did not succeed, reset the tag data and visibility of the source
                if (!dragevent.getResult()) {
                    sourceView.setTag(currentTag);
                    sourceView.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "drag failed, resetting tag on sourceview to : " + currentTag.toString());
                }

                // keep track of empty hole card spots, when all empty, show ready button
                Integer nullCounter = 0;
                for (int i=0; i<holeCardsLayout.getChildCount(); i++) {
                    if (holeCardsLayout.getChildAt(i).getTag() == null) {
                        nullCounter += 1;
                    }
                }
                if (nullCounter == 7) {
                    displayReadyButton();
                } else {
                    displayUnreadyButton();
                }

                break;
            default:
                sourceView.setVisibility(View.VISIBLE);
                break;
        }
        return true;
    }

    private void replaceHolder(LinearLayout sourceLayout, Integer originalIndex) {
        ImageView replacement = new ImageView(this);
        replacement.setTag(null);

        if (sourceLayout.getId() == highCardLayout.getId()) {
            replacement.setBackgroundResource(R.drawable.highcard_holder);
            sourceLayout.addView(replacement);
//            Log.d(TAG, "source is highcard");
        }

        if (sourceLayout.getId() == holdemLayout.getId()) {
            replacement.setBackgroundResource(R.drawable.holdem_holder);
            sourceLayout.addView(replacement);
//            Log.d(TAG, "source is holdem");
        }

        if (sourceLayout.getId() == omahaLayout.getId()) {
            replacement.setBackgroundResource(R.drawable.omaha_holder);
            sourceLayout.addView(replacement);
//            Log.d(TAG, "source is omaha");
        }

        if (sourceLayout.getId() == holeCardsLayout.getId()) {
            replacement.setBackgroundResource(R.drawable.hole_holder);
            //TODO: add index arg
            sourceLayout.addView(replacement);
//            Log.d(TAG, "source is hole");
        }
    }

    private void replaceAllSelectionHolders() {
        replaceHolder((LinearLayout) highCardLayout, null);
        replaceHolder((LinearLayout) holdemLayout, null);
        replaceHolder((LinearLayout) holdemLayout, null);
        replaceHolder((LinearLayout) omahaLayout, null);
        replaceHolder((LinearLayout) omahaLayout, null);
        replaceHolder((LinearLayout) omahaLayout, null);
        replaceHolder((LinearLayout) omahaLayout, null);
    }

    private View.OnClickListener readySelectionsListener = new View.OnClickListener() {
        public void onClick(View v) {
            readySelections();
        }
    };

    public void readySelections() {
        if ((Boolean) readyChoicesButton.getTag()) {

            String hctag = (String) highCardLayout.getChildAt(0).getTag();
            Integer ihctag = Integer.parseInt(hctag);
            ArrayList<Integer> highCardChoices = new ArrayList<>();
            highCardChoices.add(ihctag);

            ArrayList<Integer> holdemChoices = new ArrayList<>();
            for (int i = 0; i < holdemLayout.getChildCount(); i++) {
                String hetag = (String) holdemLayout.getChildAt(i).getTag();
                Integer ihetag = Integer.parseInt(hetag);
                holdemChoices.add(ihetag);
            }

            ArrayList<Integer> omahaChoices = new ArrayList<>();
            for (int i = 0; i < omahaLayout.getChildCount(); i++) {
                String omtag = (String) omahaLayout.getChildAt(i).getTag();
                Integer iomtag = Integer.parseInt(omtag);
                omahaChoices.add(iomtag);
            }

            Crashlytics.log(Log.DEBUG, TAG, "choices: " + highCardChoices.toString() + holdemChoices.toString() + omahaChoices.toString());
            cgm.recordHandChoices(highCardChoices, holdemChoices, omahaChoices);

            // ask user to confirm selections
            showAlertDialog("confirmSelections", null);
        } else {
            showAlertDialog("selectCards", null);
        }
    }

    public void displayReadyButton() {
        readyChoicesButton.setTag(true);
        readyChoicesButton.setBackgroundResource(R.drawable.ready_circle_lg);
    }

    public void displayUnreadyButton() {
        readyChoicesButton.setTag(false);
        readyChoicesButton.setBackgroundResource(R.drawable.unready_circle_lg);
    }

    public void resetSelections() {
        for (ViewGroup layout : selectionLayouts) {
            if (layout != null && layout.getChildCount() > 0) {
                Integer childCount = layout.getChildCount();
                layout.removeAllViews();
                for (int i=0; i<childCount; i++) {
                    replaceHolder((LinearLayout) layout, null);
                }
            }
        }
        displayHoleCards(session.getActiveGame().getCurrentRound().getHoleCards());
        displayUnreadyButton();
    }

    private View.OnClickListener suspendListener = new View.OnClickListener() {
        public void onClick(View v) {
//            returnToMenu();

/** temporary dev skip button **/
            ArrayList<Integer> highCardChoices = new ArrayList<>();
            ArrayList<Integer> holdemChoices = new ArrayList<>();
            ArrayList<Integer> omahaChoices = new ArrayList<>();

            highCardChoices.add(0);
            holdemChoices.add(1);
            holdemChoices.add(2);
            omahaChoices.add(3);
            omahaChoices.add(4);
            omahaChoices.add(5);
            omahaChoices.add(6);

            cgm.recordHandChoices(highCardChoices, holdemChoices, omahaChoices);
            cgm.submitHandChoices();
            showScoresActivity();
            //end dev skip
        }
    };

    private View.OnClickListener resetSelectionsListener = new View.OnClickListener() {
        public void onClick(View v) {
            resetSelections();
        }
    };

    public void displayHoleCards(HoleCards holeCards) {
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }

        if (currentAlertDialog != null) {
            currentAlertDialog.dismiss();
        }

        holeCardsLayout.removeAllViews();
        holeCardsLayout.invalidate();

        HashMap<Integer, Card> cards = holeCards.getCards();
        for (int i=0; i<7; i++) {
            Integer resourceId = cards.get(i).getResourceId();
            ImageView currentIv = new ImageView(MainActivity.this);
            currentIv.setImageResource(resourceId);
            String tag = cards.get(i).getHoleCardId().toString();
            currentIv.setTag(tag);
            currentIv.setOnTouchListener(this);
            holeCardsLayout.addView(currentIv);
        }

    }

    private View.OnClickListener scorePopUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showScoreDialog();
            }
    };

    //TODO: polish and fix
    public void showScoreDialog() {
        String roundDisplay = session.getActiveGame().getCurrentRound().getRoundNumberDisplay();
        if (roundDisplay.equals("1")) {
            showAlertDialog("noScoreYet", null);
        } else {
            ScoreDialogCreator scoreDialogCreator = new ScoreDialogCreator();
            Bundle args = new Bundle();
            args.putString("round", roundDisplay);
            args.putBoolean("scoring", false);
            scoreDialogCreator.setArguments(args);

            scoreDialogCreator.setTotalScores(session.getActiveGame().getScores());
            scoreDialogCreator.show(this.getFragmentManager(), "whooo");
        }
    }

    public void showAlertDialog(String messageType, String gamerTag) {
        if (currentAlertDialog != null) {
            currentAlertDialog.dismiss();
        }

        switch (messageType) {
            case "playerConnected":
                if (session.getActiveGame().isAllPlayersActive() && waitingDialog != null) {
                    waitingDialog.dismiss();
                }
                showOkDialog(R.string.player_connected, gamerTag);
                break;

            case "playerSuspended":
                showPauseDialog(R.string.player_suspended, gamerTag);
                break;

            case "playerLeft":
                showOkDialog(R.string.player_left, gamerTag);
                break;

            case "socketDisconnect":
                if (waitingDialog != null) {
                    waitingDialog.dismiss();
                }
                showOkDialog(R.string.socket_disconnect_main, null);
                break;

            case "playerWaiting":
                showOkDialog(R.string.resumed_waiting, gamerTag);
                break;

            case "confirmSelections":
                showChooseDialog(R.string.confirm_selections);
                break;

            case "noScoreYet":
                showOkDialog(R.string.no_score, null);
                break;

            case "selectCards":
                showOkDialog(R.string.select_cards, null);
                break;

        }
    }

    public void showPauseDialog(int type, String gamerTag) {
        PauseDialog pauseDialog = new PauseDialog(MainActivity.this, type, gamerTag);
        pauseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentAlertDialog = pauseDialog;
        pauseDialog.show();
    }

    public void showOkDialog(int type, String gamerTag) {
        OkDialog okDialog = new OkDialog(MainActivity.this, type, gamerTag);
        okDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentAlertDialog = okDialog;
        okDialog.show();
    }

    public void showChooseDialog(int type) {
        ChooseDialog chooseDialog = new ChooseDialog(MainActivity.this, type, cgm);
        chooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentAlertDialog = chooseDialog;
        chooseDialog.show();
    }

    // TODO needs custom redesign
    public void showWaitingDialog() {
        if (currentAlertDialog != null) {
            currentAlertDialog.dismiss();
        }

        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            waitingDialog = new ProgressDialog(this, Theme_Material_Dialog_Alert);
        } else {
            waitingDialog = new ProgressDialog(this);
        }
        waitingDialog.setMessage("Waiting on other players");
        waitingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Return to Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToMenu(false);
            }
        });
        waitingDialog.show();
    }

    public void setRound(String roundNumber) {
        currentRoundTv.setText("Round " + roundNumber);
    }

    public void showScoresActivity() {
        for (ViewGroup each : selectionLayouts) {
            if (each != null && each.getChildCount() > 0) {
                each.removeAllViews();
            }
        }
        holeCardsLayout.removeAllViews();
        holeCardsLayout.invalidate();
        readyLightsLayout.removeAllViews();

        final Intent sintent = new Intent(this, ScoringActivity.class);
        startActivity(sintent);
    }

    public void returnToMenu(boolean creationError) {
        Intent i = new Intent(this, MenuActivity.class);

        // emit suspend game temp to server
        if (cgm.isSocketConnected() && session.isGameActive()) {
            cgm.suspendGame();
        }

        // start menu activity
        boolean fromChildActivity = true;
        i.putExtra("GAME_CREATION_ERROR", creationError);
        i.putExtra("FROM_CHILD_ACTIVITY", fromChildActivity);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
//            Crashlytics.log(Log.DEBUG, TAG, "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        returnToMenu(false);
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
