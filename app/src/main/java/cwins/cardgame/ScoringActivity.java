package cwins.cardgame;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cwins.cardgame.dialogs.ScoreDialogCreator;
import cwins.cardgame.model.Board;
import cwins.cardgame.model.Card;
import cwins.cardgame.model.Matchup;
import cwins.cardgame.model.MatchupTransition;
import cwins.cardgame.model.RoundResults;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.User;

import static android.R.style.Theme_Material_Dialog_Alert;


public class ScoringActivity extends BaseActivity {
    @BindView(R.id.skip_button) Button skipButton;
    @BindView(R.id.exit_button) Button exitButton;
    @BindView(R.id.finished_button) TextView finishedButton;
    @BindView(R.id.pause_button) Button pauseButton;

    /** Opponent Views */
    @BindView(R.id.opponent_name) TextView opponentNameTv;

    @BindView(R.id.oppo_high_card_choice) ViewGroup oppoHighCardLayout;
    @BindView(R.id.oppo_high_card) ImageView oppoHighCardIv;

    @BindView(R.id.oppo_holdem_choice) ViewGroup oppoHoldemLayout;
    @BindView(R.id.oppo_holdem_1) ImageView oppoHoldemIv1;
    @BindView(R.id.oppo_holdem_2) ImageView oppoHoldemIv2;

    @BindViews({R.id.oppo_holdem_1, R.id.oppo_holdem_2})
    List<ImageView> oppoHoldemChoiceIvs;

    @BindView(R.id.oppo_omaha_choice) ViewGroup oppoOmahaLayout;
    @BindView(R.id.oppo_omaha_1) ImageView oppoOmahaIv1;
    @BindView(R.id.oppo_omaha_2) ImageView oppoOmahaIv2;
    @BindView(R.id.oppo_omaha_3) ImageView oppoOmahaIv3;
    @BindView(R.id.oppo_omaha_4) ImageView oppoOmahaIv4;

    @BindViews({R.id.oppo_omaha_1, R.id.oppo_omaha_2, R.id.oppo_omaha_3, R.id.oppo_omaha_4})
    List<ImageView> oppoOmahaChoiceIvs;

    @BindViews({R.id.oppo_high_card,
            R.id.oppo_holdem_1,
            R.id.oppo_holdem_2,
            R.id.oppo_omaha_1,
            R.id.oppo_omaha_2,
            R.id.oppo_omaha_3,
            R.id.oppo_omaha_4})
    List<ImageView> oppoAllChoicesIvs;

    /** User Views */
    @BindView(R.id.user_name) TextView userNameTv;

    @BindView(R.id.user_high_card_choice) ViewGroup userHighCardLayout;
    @BindView(R.id.user_high_card) ImageView userHighCardIv;

    @BindView(R.id.user_holdem_choice) ViewGroup userHoldemLayout;
    @BindView(R.id.user_holdem_1) ImageView userHoldemIv1;
    @BindView(R.id.user_holdem_2) ImageView userHoldemIv2;

    @BindViews({R.id.user_holdem_1, R.id.user_holdem_2})
    List<ImageView> userHoldemChoiceIvs;

    @BindView(R.id.user_omaha_choice) ViewGroup userOmahaLayout;
    @BindView(R.id.user_omaha_1) ImageView userOmahaIv1;
    @BindView(R.id.user_omaha_2) ImageView userOmahaIv2;
    @BindView(R.id.user_omaha_3) ImageView userOmahaIv3;
    @BindView(R.id.user_omaha_4) ImageView userOmahaIv4;

    @BindViews({R.id.user_omaha_1, R.id.user_omaha_2, R.id.user_omaha_3, R.id.user_omaha_4})
    List<ImageView> userOmahaChoiceIvs;

    @BindViews({R.id.user_high_card,
            R.id.user_holdem_1,
            R.id.user_holdem_2,
            R.id.user_omaha_1,
            R.id.user_omaha_2,
            R.id.user_omaha_3,
            R.id.user_omaha_4})
    List<ImageView> userAllChoicesIvs;

    /** Board Views */
    @BindView(R.id.board_indicator) TextView boardIndicatorTv;

    @BindView(R.id.board_cards) ViewGroup boardCardsLayout;
    @BindView(R.id.board_card_1) ImageView boardCardIv1;
    @BindView(R.id.board_card_2) ImageView boardCardIv2;
    @BindView(R.id.board_card_3) ImageView boardCardIv3;
    @BindView(R.id.board_card_4) ImageView boardCardIv4;
    @BindView(R.id.board_card_5) ImageView boardCardIv5;

    @BindViews({R.id.board_card_1,
            R.id.board_card_2,
            R.id.board_card_3,
            R.id.board_card_4,
            R.id.board_card_5})
    List<ImageView> boardCardsIvs;

    /** Score & Value Views */
    @BindView(R.id.oppo_total_score_indicator) TextView oppoTotalScoreTv;
    @BindView(R.id.user_total_score_indicator) TextView userTotalScoreTv;

    @BindView(R.id.oppo_omaha_value_indicator) TextView oppoOmahaValueTv;
    @BindView(R.id.oppo_holdem_value_indicator) TextView oppoHoldemValueTv;
    @BindView(R.id.oppo_highcard_value_indicator) TextView oppoHighcardValueTv;
    @BindView(R.id.oppo_omaha_score_indicator) TextView oppoOmahaScoreTv;
    @BindView(R.id.oppo_holdem_score_indicator) TextView oppoHoldemScoreTv;
    @BindView(R.id.oppo_highcard_score_indicator) TextView oppoHighcardScoreTv;

    @BindView(R.id.user_omaha_value_indicator) TextView userOmahaValueTv;
    @BindView(R.id.user_holdem_value_indicator) TextView userHoldemValueTv;
    @BindView(R.id.user_highcard_value_indicator) TextView userHighcardValueTv;
    @BindView(R.id.user_omaha_score_indicator) TextView userOmahaScoreTv;
    @BindView(R.id.user_holdem_score_indicator) TextView userHoldemScoreTv;
    @BindView(R.id.user_highcard_score_indicator) TextView userHighcardScoreTv;

    @BindViews({R.id.oppo_omaha_value_indicator,
            R.id.oppo_holdem_value_indicator,
            R.id.oppo_highcard_value_indicator,
            R.id.oppo_omaha_score_indicator,
            R.id.oppo_holdem_score_indicator,
            R.id.oppo_highcard_score_indicator,
            R.id.user_omaha_value_indicator,
            R.id.user_holdem_value_indicator,
            R.id.user_highcard_value_indicator,
            R.id.user_omaha_score_indicator,
            R.id.user_holdem_score_indicator,
            R.id.user_highcard_score_indicator})
    List<TextView> allScoreValueIndicatorTvs;

    private String TAG = "cardgame|ScoringActivity--";
    @Inject CardGameManager cgm;
    @Inject Session session;
    ProgressDialog waitingDialog;
    AlertDialog currentAlertDialog;
    AlertDialog finalDialog;

    private Animator scoringAnimator;

    private boolean vis = true;

    private Boolean score_visual_inspection = false;

    private Integer totalUserScoreCounter;
    private Integer totalOpponentScoreCounter;
    private Integer timerDelay = 1200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FlashPokerApplication) getApplication()).component().inject(this);
        setContentView(R.layout.activity_scoring);
        ButterKnife.bind(this);

        // hide animation pause button if old phone
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            pauseButton.setVisibility(View.GONE);
        }

        // Prevent phone from entering sleep
        keepScreenOn();

//        cgm.setScoringActivity(this);

//        if (!cgm.areChoicesSubmitted()) {
//            cgm.submitHandChoices();
//        }

        // display placeholders for all card locations
        oppoHighCardIv.setBackgroundResource(R.drawable.highcard_holder);
        userHighCardIv.setImageResource(R.drawable.highcard_holder);

        for (ImageView current : oppoHoldemChoiceIvs) {
            current.setImageResource(R.drawable.holdem_holder);
            current.setPadding(1, 0, 1, 0);
        }

        for (ImageView current : userHoldemChoiceIvs) {
            current.setImageResource(R.drawable.holdem_holder);
            current.setPadding(1, 0, 1, 0);
        }

        for (ImageView current : oppoOmahaChoiceIvs) {
            current.setImageResource(R.drawable.omaha_holder);
            current.setPadding(1, 0, 1, 0);
        }

        for (ImageView current : userOmahaChoiceIvs) {
            current.setImageResource(R.drawable.omaha_holder);
            current.setPadding(1, 0, 1, 0);
        }

        // total scores

    }

    @Override
    public void onStart() {
        super.onStart();
        Crashlytics.log(Log.DEBUG, TAG, "onStart()");
        cgm.setScoringActivity(this);

        User user = this.session.getCurrentUser();
        userNameTv.setText(user.getGamerTag());
        userNameTv.setVisibility(View.VISIBLE);

        if (session.getActiveGame().getCurrentRound().isReadyForScoring()) {
            beginScoring();
        } else {
            showWaitingDialog();
        }

        //testing only
//        beginScoring();

    }

    /**  Scoring Animation  */
    // called from OnResultsListener --> cgm.displayResults()
    public void beginScoring() {
        //called from cgm when results are received
        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
        skipButton.setVisibility(View.VISIBLE);
        User user = this.session.getCurrentUser();

//
//        //display user total score
//        Integer userTotalScore = this.session.getActiveGame().getPlayerScore(user.getGoogleId());
//        int drawId = getScoreDisplayColor(userTotalScore);

        scoringAnimator = buildScoringAnimation(
                this.session.getActiveGame().getCurrentRound().getRoundResults());

        // XXX: pop a little dialogue when the Animator is done running
        scoringAnimator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                Toast.makeText(ScoringActivity.this, "Fin", Toast.LENGTH_SHORT).show();
                exitButton.setVisibility(View.INVISIBLE);
                skipButton.setVisibility(View.INVISIBLE);
                showFinishedButton();
            }
        });
        scoringAnimator.start();

        // now update total scores as the animator has been constructed using the old totals
        // this prevents against losing the new totals for the final score dialog in case a player
        // drops and resumes, thus blowing away the currentRound.roundResults
        cgm.updateGameScores();
    }



    /**
     * Constructs a gigantic AnimatorSet object that, when run, displays all the animations
     * for the current scoring round.
     *
     * @param roundResults
     * @return
     */
    private Animator buildScoringAnimation(RoundResults roundResults) {
        User currentUser = this.session.getCurrentUser();
//        Log.d(TAG, "building the scoring animation");
        boolean firstRun = true;

        ArrayList<Animator> animators = new ArrayList<>();

        // fade in user card choices
        HashMap<String, ArrayList<Card>> userHandChoices =
                this.session.getActiveGame().getCurrentRound().getHandChoices(
                        currentUser.getGoogleId());

        animators.add(getHandChoicesAnimator(userHandChoices));

        // display total scores
        Integer userTotalScore =
                this.session.getActiveGame().getPlayerScore(currentUser.getGoogleId());

        animators.add(getTextViewFadeAnimator(userTotalScoreTv, userTotalScore.toString(), false, getScoreDisplayColor(userTotalScore)));

        Integer oppoTotalScore = 0;

        // start processing matchups
        Iterator<MatchupTransition> iterator = roundResults.iterator();
        while (iterator.hasNext()) {
            MatchupTransition mt = iterator.next();
            Matchup matchup = mt.getMatchup();

            // show new opponent data
            if (mt.isNewOpponent) {
                // hides all score & value tvs
                animators.add(getScoreValueTextViewHideAnimator());

                List<Animator> newOpponentAnimatorList = new ArrayList<>();

                // update opponent username TextView
//                Log.d(TAG, "oppoGamerTag: " + matchup.getOppoGamerTag());
                newOpponentAnimatorList.add(getTextViewFadeAnimator(opponentNameTv, matchup.getOppoGamerTag(), false));

                // opponent hole cards
//                Log.d(TAG, "oppo goog: " + matchup.getOppoGoogleId());
                HashMap<String, ArrayList<Card>> oppoHandChoices =
                        this.session.getActiveGame().getCurrentRound().getHandChoices(matchup.getOppoGoogleId());
                newOpponentAnimatorList.add(getOpponentHandChoicesAnimator(oppoHandChoices));

                // grab starting total score for new oppo & display
                oppoTotalScore = this.session.getActiveGame().getPlayerScore(matchup.getOppoGoogleId());
                int newDraw = getScoreDisplayColor(oppoTotalScore);
                newOpponentAnimatorList.add(
                        getTextViewFadeAnimator(oppoTotalScoreTv, oppoTotalScore.toString(), true, newDraw));

                AnimatorSet newOpponentAnimatorSet = new AnimatorSet();
                newOpponentAnimatorSet.playTogether(newOpponentAnimatorList);
                animators.add(newOpponentAnimatorSet);

            }

            if (mt.isNewBoard) {
                // hides all score & value tvs
                animators.add(getScoreValueTextViewHideAnimator());
                List<Animator> endBoardAnimatorList = new ArrayList<>();
//                Log.d(TAG, "new board --- oppoTotal = " + oppoTotalScore + " userTotal = " + userTotalScore);

                // update total score displays for both players simultaneously
                // fade in new total score TextView; switch color indicator if necessary
                if (!firstRun) {
                    if (!mt.isNewOpponent) {
                        int newOppoDraw = getScoreDisplayColor(oppoTotalScore);
                        endBoardAnimatorList.add(getTextViewFadeAnimator(
                                oppoTotalScoreTv, oppoTotalScore.toString(), true, newOppoDraw));
                    }

                    int newUserDraw = getScoreDisplayColor(userTotalScore);
                    endBoardAnimatorList.add(getTextViewFadeAnimator(
                            userTotalScoreTv, userTotalScore.toString(), true, newUserDraw));
                }

                // show the new board
                Board board = matchup.getBoard();
                endBoardAnimatorList.add(getNewBoardAnimators(board, firstRun));
                AnimatorSet endBoardAnimatorSet = new AnimatorSet();
                endBoardAnimatorSet.playTogether(endBoardAnimatorList);
                animators.add(endBoardAnimatorSet);

                if (firstRun) {
                    animators.add(getButtonFadeInAnimator(pauseButton));
                }
            }

            Integer oppoScore = matchup.getOpponentScore();
            Integer userScore = matchup.getScore();
//            Log.d(TAG, "pre-oppoTotalScore " + oppoTotalScore + " & oppoMatchupScore " + oppoScore);
//            Log.d(TAG, "pre-userTotalScore " + userTotalScore + " & userMatchupScore " + userScore);

            // show the matchup
            animators.add(getMatchupScoringAnimation(matchup));

            // add current matchup scores to total scores to be updated after all three game types
            oppoTotalScore += oppoScore;
            userTotalScore += userScore;
//            Log.d(TAG, "newOppoTotal: " + oppoTotalScore + " newUserTotal: " + userTotalScore);
            firstRun = false;

            // final total score update
            if (!iterator.hasNext()) {
                List<Animator> finalAnimatorList = new ArrayList<>();
                int newOppoDraw = getScoreDisplayColor(oppoTotalScore);
                finalAnimatorList.add(getTextViewFadeAnimator(
                        oppoTotalScoreTv, oppoTotalScore.toString(), true, newOppoDraw));
                int newUserDraw = getScoreDisplayColor(userTotalScore);
                finalAnimatorList.add(getTextViewFadeAnimator(
                        userTotalScoreTv, userTotalScore.toString(), true, newUserDraw));
                AnimatorSet finalAnimatorSet = new AnimatorSet();
                finalAnimatorSet.playTogether(finalAnimatorList);
                animators.add(finalAnimatorSet);
            }
        }
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        Crashlytics.log(Log.DEBUG, TAG, "done building animator list. total len was " + animators.size());

        return set;
    }

    //TODO make sure invisible pause button isn't clicked!
    private Animator getButtonFadeInAnimator(Button button) {
        final ObjectAnimator fadeIn = ObjectAnimator
                .ofFloat(button, "alpha", 1)
                .setDuration(500);
        fadeIn.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                button.setVisibility(View.VISIBLE);
            }});

        return fadeIn;
    }


    private Animator getScoreValueTextViewHideAnimator() {
        // fade out score & value TextViews simultaneously
        List<Animator> hideScoreValueTvList = new ArrayList<>();
        for (TextView tv : allScoreValueIndicatorTvs) {
            ObjectAnimator fadeOut = ObjectAnimator
                    .ofFloat(tv, "alpha", 0)
                    .setDuration(1500);
            hideScoreValueTvList.add(fadeOut);
        }
        AnimatorSet hideScoreValueTvSet = new AnimatorSet();
        hideScoreValueTvSet.playTogether(hideScoreValueTvList);
        return hideScoreValueTvSet;
    }

    /**
     * Returns an Animator to fade out a TextView, alter its text, and then fade it back in
     **/
    private Animator getTextViewFadeAnimator(final TextView textView, final String newLabel, boolean doFadeOut) {
        final ObjectAnimator fadeOut = ObjectAnimator
                .ofFloat(textView, "alpha", 0)
                .setDuration(500);
        final ObjectAnimator fadeIn = ObjectAnimator
                .ofFloat(textView, "alpha", 1)
                .setDuration(500);

        if (doFadeOut) {
            fadeOut.addListener(new BaseAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setText(newLabel);
                }
            });
        } else {
            fadeIn.addListener(new BaseAnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    textView.setText(newLabel);
                }
            });
        }

        List<Animator> animators = new ArrayList<>();
        if (doFadeOut) {
            animators.add(fadeOut);
        }
        animators.add(fadeIn);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        return set;
    }

    /**
     * Returns an Animator to fade out a TextView, alter its text, change its drawable,
     * and then fade it back in. Also makes textView visible if it wasn't already.
     **/
    private Animator getTextViewFadeAnimator(final TextView textView, final String newLabel, boolean doFadeOut, final int newDrawableId) {
        final ObjectAnimator fadeOut = ObjectAnimator
                .ofFloat(textView, "alpha", 0)
                .setDuration(500);
        final ObjectAnimator fadeIn = ObjectAnimator
                .ofFloat(textView, "alpha", 1)
                .setDuration(500);

        if (doFadeOut) {
//            fadeOut.addListener(new BaseAnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    textView.setVisibility(View.VISIBLE);
//                }
//            });

            fadeOut.addListener(new BaseAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setText(newLabel);
                    textView.setBackgroundResource(newDrawableId);
                }
            });
        } else {
            fadeIn.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                textView.setText(newLabel);
                textView.setBackgroundResource(newDrawableId);
            }});
        }

        List<Animator> animators = new ArrayList<>();
        if (doFadeOut) {
            animators.add(fadeOut);
        }
        animators.add(fadeIn);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        return set;
    }

    /**
     * Returns an Animator that will fade out several image views at once, update their target
     * drawables, and then fade them back in.
     *
     * @param viewsMap map from ImageViews to the new drawableId they should be updated with
     */
    private Animator getMultiImageViewFadeAnimator(Map<ImageView, Integer> viewsMap, boolean doFadeOut) {
        List<Animator> fadeIns = new ArrayList<>(viewsMap.size());
        List<Animator> fadeOuts = new ArrayList<>(viewsMap.size());

        for (final Map.Entry<ImageView, Integer> entry : viewsMap.entrySet()) {
            final ObjectAnimator fadeOut = ObjectAnimator
                    .ofFloat(entry.getKey(), "alpha", 0)
                    .setDuration(750);
            final ObjectAnimator fadeIn = ObjectAnimator
                    .ofFloat(entry.getKey(), "alpha", 1)
                    .setDuration(750);

            if (doFadeOut) {
                fadeOut.addListener(new BaseAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        entry.getKey().setImageResource(entry.getValue());
                    }
                });
            } else {
                fadeIn.addListener(new BaseAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        entry.getKey().setImageResource(entry.getValue());
                    }
                });
            }

            fadeIns.add(fadeIn);
            fadeOuts.add(fadeOut);
        }

        AnimatorSet fadeInSet = new AnimatorSet();
        AnimatorSet fadeOutSet = new AnimatorSet();
        fadeOutSet.playTogether(fadeOuts);
        fadeInSet.playTogether(fadeIns);

        List<Animator> finalList = new ArrayList<>();
        if (doFadeOut) {
            finalList.add(fadeOutSet);
        }
        finalList.add(fadeInSet);

        AnimatorSet finalSet = new AnimatorSet();
        finalSet.playSequentially(finalList);

        return finalSet;
    }

    /**
     * Returns the animator to display the user's hole cards.
     *
     * @param userHandChoices
     * @return
     */
    private Animator getHandChoicesAnimator(HashMap<String, ArrayList<Card>> userHandChoices) {
        HashMap<ImageView, Integer> viewsToResources = new HashMap<>();
        viewsToResources.put(userHighCardIv, userHandChoices.get("high_card").get(0).getResourceId());
        viewsToResources.put(userHoldemIv1, userHandChoices.get("holdem").get(0).getResourceId());
        viewsToResources.put(userHoldemIv2, userHandChoices.get("holdem").get(1).getResourceId());
        viewsToResources.put(userOmahaIv1, userHandChoices.get("omaha").get(0).getResourceId());
        viewsToResources.put(userOmahaIv2, userHandChoices.get("omaha").get(1).getResourceId());
        viewsToResources.put(userOmahaIv3, userHandChoices.get("omaha").get(2).getResourceId());
        viewsToResources.put(userOmahaIv4, userHandChoices.get("omaha").get(3).getResourceId());

        return getMultiImageViewFadeAnimator(viewsToResources, true);
    }

    /**
     * Returns the animator to display the opponent's hole cards. Just points to different views
     * than addHandChoicesAnimators() does.
     *
     * @param oppoHandChoices
     * @return
     */
    private Animator getOpponentHandChoicesAnimator(HashMap<String, ArrayList<Card>> oppoHandChoices) {
        HashMap<ImageView, Integer> viewsToResources = new HashMap<>();
        viewsToResources.put(oppoHighCardIv, oppoHandChoices.get("high_card").get(0).getResourceId());
        viewsToResources.put(oppoHoldemIv1, oppoHandChoices.get("holdem").get(0).getResourceId());
        viewsToResources.put(oppoHoldemIv2, oppoHandChoices.get("holdem").get(1).getResourceId());
        viewsToResources.put(oppoOmahaIv1, oppoHandChoices.get("omaha").get(0).getResourceId());
        viewsToResources.put(oppoOmahaIv2, oppoHandChoices.get("omaha").get(1).getResourceId());
        viewsToResources.put(oppoOmahaIv3, oppoHandChoices.get("omaha").get(2).getResourceId());
        viewsToResources.put(oppoOmahaIv4, oppoHandChoices.get("omaha").get(3).getResourceId());

        return getMultiImageViewFadeAnimator(viewsToResources, true);
    }

    private Animator getNewBoardAnimators(Board board, boolean firstBoard) {
        HashMap<ImageView, Integer> viewsToResources = new HashMap<>();

        for (int i=0; i<5; i++) {
            viewsToResources.put(boardCardsIvs.get(i), board.getCards().get(i).getResourceId());
        }

        Animator boardCards = getMultiImageViewFadeAnimator(viewsToResources, !firstBoard);
        Animator boardIdAnimator;
        // TODO: these should run simultaneously i believe
//        Animator boardIdShow = getTextViewVisibilityAnimator(boardIndicatorTv, View.VISIBLE);
//        Log.d(TAG, "board ID: " + board.getBoardDisplayName());
        boardIdAnimator = getTextViewFadeAnimator(boardIndicatorTv, board.getBoardDisplayName(), !firstBoard);

        List<Animator> animators = new ArrayList<>();

        animators.add(boardCards);
//        animators.add(boardIdShow);
        animators.add(boardIdAnimator);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        return set;
    }



    private Animator getMatchupScoringAnimation(Matchup matchup) {
        List<Animator> animators = new ArrayList<>();

        Integer oppoScore = matchup.getOpponentScore();
        Integer userScore = matchup.getScore();

        String oppoValue = matchup.getOpponentHandType();
        String userValue = matchup.getHandType();

        int newOppoDrawId = getScoreDisplayColor(oppoScore);
        int newUserDrawId = getScoreDisplayColor(userScore);

//        Log.d(TAG, "gametype: " + matchup.getGameType().toString() + " oppoScore: " + oppoScore.toString());

        TextView userValueTv = null;
        TextView userScoreTv = null;
        TextView oppoValueTv = null;
        TextView oppoScoreTv = null;
        switch (matchup.getGameType()) {
            case HIGHCARD:
                userValueTv = userHighcardValueTv;
                userScoreTv = userHighcardScoreTv;
                oppoValueTv = oppoHighcardValueTv;
                oppoScoreTv = oppoHighcardScoreTv;
                break;
            case HOLDEM:
                userValueTv = userHoldemValueTv;
                userScoreTv = userHoldemScoreTv;
                oppoValueTv = oppoHoldemValueTv;
                oppoScoreTv = oppoHoldemScoreTv;
                break;
            case OMAHA:
                userValueTv = userOmahaValueTv;
                userScoreTv = userOmahaScoreTv;
                oppoValueTv = oppoOmahaValueTv;
                oppoScoreTv = oppoOmahaScoreTv;
                break;
        }

        // show the hand values together
        List<Animator> valuesAnimatorList = new ArrayList<>();
        valuesAnimatorList.add(getTextViewFadeAnimator(oppoValueTv, oppoValue, false));
        valuesAnimatorList.add(getTextViewFadeAnimator(userValueTv, userValue, false));
        AnimatorSet valuesAnimatorSet = new AnimatorSet();
        valuesAnimatorSet.playTogether(valuesAnimatorList);
        animators.add(valuesAnimatorSet);

        // show the scores together
        List<Animator> scoresAnimatorList = new ArrayList<>();
        scoresAnimatorList.add(
                getTextViewFadeAnimator(userScoreTv, userScore.toString(), false, newUserDrawId));
        scoresAnimatorList.add(
                getTextViewFadeAnimator(oppoScoreTv, oppoScore.toString(), false, newOppoDrawId));
        AnimatorSet scoresAnimatorSet = new AnimatorSet();
        scoresAnimatorSet.playTogether(scoresAnimatorList);
        animators.add(scoresAnimatorSet);

        // show values and then scores
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        return set;
    }

    /**
     * Wraps the changing of a TextView's text inside an Animator object
     *
     * @param textView
     * @param newLabel
     * @return
     */
    private Animator getTextViewUpdateAnimator(final TextView textView, final String newLabel) {
        // TODO: this is a hack: we dont really want to do any fading, but we need a non-empty
        // animation to avoid crashing inside the android code
        final ObjectAnimator fadeIn = ObjectAnimator
                .ofFloat(textView, "alpha", 1);
        fadeIn.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setText(newLabel);
            }
        });
        return fadeIn;
    }

    /**
     * Wraps the changing of a TextView's visibility inside an Animator object
     *
     * @param textView
     * @param visibility target visibility
     * @return
     */
    private Animator getTextViewVisibilityAnimator(final TextView textView, final int visibility) {
        // TODO: this is a hack: we dont really want to do any fading, but we need a non-empty
        // animation to avoid crashing inside the android code
        final ObjectAnimator fadeIn = ObjectAnimator
                .ofFloat(textView, "alpha", 1);
        fadeIn.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(visibility);
            }
        });
        return fadeIn;
    }

    public void changeScoreIndicator(Integer score, TextView scoreIndicator) {
        if (score < 0) {
            scoreIndicator.setBackgroundResource(R.drawable.score_negative_background);
        } else {
            scoreIndicator.setBackgroundResource(R.drawable.score_positive_background);
        }
        scoreIndicator.setText(score.toString());
        scoreIndicator.setVisibility(View.VISIBLE);
    }

    public int getScoreDisplayColor(Integer score) {
        if (score < 0) {
            return R.drawable.score_negative_background;
        }
        return R.drawable.score_positive_background;
    }

    /**  Dialogs  */
    public void showAlertDialog(String messageType, String gamerTag) {
        if (scoringAnimator != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scoringAnimator.pause();
        }

        if (currentAlertDialog != null) {
            currentAlertDialog.dismiss();
        }

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

//          this is how to change dimensions/position
//        final AlertDialog connectedDialog = builder.create();
//        connectedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        WindowManager.LayoutParams wmlp = connectedDialog.getWindow().getAttributes();
//        wmlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        switch (messageType) {
            case "playerConnected":
                builder.setMessage(gamerTag + "has connected.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (session.getActiveGame().isAllPlayersActive() && waitingDialog != null) {
                            waitingDialog.dismiss();
                            resumeAnimations();
                        }
                    }
                });
                break;
            case "playerSuspended":
                builder.setMessage(gamerTag + "has suspended.");
                builder.setPositiveButton("Wait", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        resumeAnimations();
//                        showWaitingDialog();
                    }
                });
                builder.setNegativeButton("Return to Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnToMenu();
                    }
                });
                break;
            case "playerLeft":
                builder.setMessage(gamerTag + "has left the game permanently. Round will be restarted.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: make sure this works!
                        dialog.dismiss();
                    }
                });
                break;
            case "socketDisconnect":
                dismissDialogs();

                builder.setMessage(
                        "Your connection to the game server has been severed. " +
                                "The current round scores will be saved and a new round will begin when the game is resumed by all players.");
                builder.setPositiveButton("Return to Menu", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        returnToMenu();
                    }
                });
                builder.setNegativeButton("Finish Viewing Scoring", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        resumeAnimations();
                        waitingDialog.dismiss();
                    }
                });
                break;
            case "playerWaiting":
                builder.setMessage(gamerTag + " is waiting for you to resume a game. Check your games list to join them.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        resumeAnimations();
                        dialog.dismiss();
                    }
                });
                break;
            case "confirmExit":
                builder.setMessage("Are you sure you want to exit the game and return to the Menu?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resumeAnimations();
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        returnToMenu();
                    }
                });
                break;
        }
        currentAlertDialog = builder.show();
    }


    @OnClick(R.id.pause_button)
    public void pausePlayAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (scoringAnimator.isPaused()) {
                scoringAnimator.resume();
                pauseButton.setBackgroundResource(R.drawable.pause_circle);
            } else {
                scoringAnimator.pause();
                pauseButton.setBackgroundResource(R.drawable.play_circle);
            }
        }
    }

    public void resumeAnimations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (scoringAnimator.isPaused()) {
                scoringAnimator.resume();
                pauseButton.setBackgroundResource(R.drawable.pause_circle);
            }
        }
    }


    // animated finished button at the end of scoring animations
    public void showFinishedButton() {
        skipButton.setVisibility(View.INVISIBLE);
        exitButton.setVisibility(View.INVISIBLE);
        pauseButton.setVisibility(View.INVISIBLE);
        finishedButton.setClickable(true);

        ArrayList<Animator> animators = new ArrayList<>();

        animators.add(getTextViewFadeAnimator(finishedButton, "Finished", false));
        animators.add(getTextViewFadeAnimator(finishedButton, "Finished", true));
        animators.add(getTextViewFadeAnimator(finishedButton, "Finished", true));

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        set.start();
    }

    @OnClick(R.id.exit_button)
    public void showConfirmExitDialog(View view) {
        showAlertDialog("confirmExit", null);
    }

    @OnClick(R.id.skip_button)
    public void skipScoring(View view) {
        showFinalDialog(view);
    }

    @OnClick(R.id.finished_button)
    public void showFinalDialog(View view) {
        //TODO: this will be wrong
        // if accessing it after a user has suspended and resumed before others have left the scoring act
        // because it references the new currentRound created when the resuming player triggers roundStart
        String roundNumberDisplay = this.session.getActiveGame().getCurrentRound().getRoundNumberDisplay();
        ScoreDialogCreator scoreDialogCreator = new ScoreDialogCreator();
        Bundle args = new Bundle();
        args.putString("round", roundNumberDisplay);
        args.putBoolean("scoring", true);
        scoreDialogCreator.setArguments(args);
        scoreDialogCreator.setTotalScores(this.session.getActiveGame().getScores());
        scoreDialogCreator.show(this.getFragmentManager(), "whooo");

    }

    private void showWaitingDialog() {
        dismissDialogs();

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
                returnToMenu();
            }
        });
        waitingDialog.show();
    }

    private void dismissDialogs() {
        if (currentAlertDialog != null) {
            currentAlertDialog.dismiss();
        }

        if (waitingDialog != null) {
            waitingDialog.dismiss();
        }
    }

    public void returnToMenu() {
        dismissDialogs();
        Intent i = new Intent(this, MenuActivity.class);

        //emit suspend game temp to server
        if (cgm.isSocketConnected()) {
            cgm.suspendGame();
        }

        //return to menu activity
        boolean fromChildActivity = true;
        i.putExtra("FROM_CHILD_ACTIVITY", fromChildActivity);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Crashlytics.log(Log.DEBUG, "CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Crashlytics.log(Log.DEBUG, TAG, "onBackPressed Called");
        returnToMenu();
    }

}
