package cwins.cardgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cwins.cardgame.model.Game;
import cwins.cardgame.model.Session;
import cwins.cardgame.model.UserScore;

/**
 * fix back button
 * parse date
 * redesign dialog
 * add 'active' / 'suspended' status indicator to each tag
 *
 *
 * **/

public class GamesListActivity extends Activity {

    private Boolean games_list_visual_inspection = false;
    private String TAG = "GamesList--";
    LinearLayout linearLayout;

    HashMap<Integer, Game> savedGames;
    @Inject CardGameManager cgm;
    @Inject Session session;

    @BindView(R.id.LL_within_SV) LinearLayout gameHolder;


//    @BindView(R.id.return_button) Button returnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((FlashPokerApplication) getApplication()).component().inject(this);
        setContentView(R.layout.activity_games_list);
        ButterKnife.bind(this);
        cgm.setGamesListActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cgm.setGamesListActivity(this);
        linearLayout = (LinearLayout) findViewById(R.id.LL_within_SV);

        retrieveGames();
    }

    public void retrieveGames() {
        //updates games list from server
        cgm.fetchState();
        savedGames = session.getSavedGames();

        for (Integer gameId : savedGames.keySet()) {
            addGame(gameId, savedGames.get(gameId));
        }
    }



    public void addGame(Integer gameId, Game game) {
        String dateTimeStarted = game.getStartedAt();
        Integer roundsPlayed = game.getRoundsPlayed();
        Integer numPlayers = game.getNumPlayers();
        Integer userScore = game.getPlayerScore(session.getCurrentUser().getGoogleId());

        TextView gameTextView = new TextView(this);
        gameTextView.isClickable();
        gameTextView.setTextSize(20);
        gameTextView.setTextColor(Color.WHITE);
        gameTextView.setTag(gameId);

        //highlight game border if there are active players waiting
        if (game.checkActivePlayers() > 0) {
            gameTextView.setBackgroundResource(R.drawable.game_tv_highlighted);
        } else {
            gameTextView.setBackgroundResource(R.drawable.game_tv_background);
        }

        gameTextView.setText(dateTimeStarted + "\nPlayers: " + numPlayers + "   Score: " + userScore);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 20, 5, 20);
        gameTextView.setPadding(15, 25, 15, 25);
        gameTextView.setTypeface(Typeface.MONOSPACE);
        gameTextView.setLayoutParams(params);
        gameTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        linearLayout.addView(gameTextView);

        gameTextView.setOnClickListener(rejoinGameListener);
    }

    private View.OnClickListener rejoinGameListener = new View.OnClickListener() {
        public void onClick(View v) {
            Integer gameId = (Integer) v.getTag();
            showGameDetails(gameId, v);
        }
    };

    public void showGameDetails(Integer id, View v) {
        final Game game = savedGames.get(id);
        final Integer gameId = id;
        final View gameView = v;

        List<String> activePlayers = new ArrayList<>();
        List<String> leftPlayers = new ArrayList<>();
        List<String> userScoresForGrid = new ArrayList<>();

        Crashlytics.log(Log.DEBUG, TAG, "Game " + id + " selected. State = " + game.getGameState());

        for (UserScore userScore : game.getScores()) {
            String gamerTag = userScore.getUser().getGamerTag();
            String googleId = userScore.getUser().getGoogleId();
            String score = userScore.getTotalScore().toString();
            String prs = game.getPlayerState(googleId);

            userScoresForGrid.add(gamerTag);
            userScoresForGrid.add(score);

            Crashlytics.log(Log.DEBUG, TAG, gamerTag + " = " + prs);
            if (prs.equals("active")) {
                activePlayers.add(gamerTag);
            } else if (prs.equals("left")) {
                leftPlayers.add(gamerTag);
            }
        }

        GridView gridView = new GridView(this);
        final List<String> active = activePlayers;
        final List<String> left = leftPlayers;

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userScoresForGrid) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                if (active.contains(view.getText())) {
                    view.setTextColor(Color.GREEN);
                } else if (left.contains(view.getText())) {
                    view.setTextColor(Color.RED);
                }
                return view;
            }
        };

        gridView.setAdapter(arrayAdapter);
        gridView.setNumColumns(2);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);

        Integer roundNumber = game.getRoundsPlayed();
        String dateTimeStarted = game.getStartedAt();

        builder.setTitle("Rounds Played " + roundNumber + "\nStarted " + dateTimeStarted);
        builder.setNegativeButton("Delete Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showDeleteGameDialog(gameId, gameView);
            }
        });
        builder.setPositiveButton("Rejoin Game", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Crashlytics.log(Log.DEBUG, TAG, "rejoining game: " + gameId.toString());
                rejoinGame(gameId);
            }
        });
        builder.show();
    }

    public void showDeleteGameDialog(Integer id, View v) {
        final Integer gameId = id;
        final View gameView = v;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you wish to permanently delete this game?");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel delete
            }
        });
        builder.setPositiveButton("YES, delete forever", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Crashlytics.log(Log.DEBUG, TAG, "Deleting game: " + gameId);
                cgm.leaveGame(gameId);
                gameView.setVisibility(View.GONE);
            }
        });
        builder.show();
    }

    private void rejoinGame(Integer id) {
        session.setCurrentUserResuming(true);
        session.setActiveGame(session.getSavedGames().get(id));
        final Intent resumeIntent = new Intent(this, MainActivity.class);
        startActivity(resumeIntent);
    }

    public void showSocketDisconnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error: Server Disconnection");
        builder.setCancelable(false);
        builder.setMessage(
                "We're sorry, it seems your connection to the game server has been severed. " +
                        "Please return to the main menu and you will be reconnected.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Crashlytics.log(Log.DEBUG, TAG, "Sending player back to main menu after disconnect.");
                finish();
            }
        });
        builder.show();
    }

    public void showPlayerResumedAndWaiting(HashMap<String, String> player_res_details) {
        final String player = player_res_details.get("gamer_tag");
        final String game = player_res_details.get("game_id");
        String date_time = player_res_details.get("date_created");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("A player wishes to resume one of your current games");
        builder.setMessage(player + " is waiting for you resume play in a game created on " + date_time
                + "\nRefresh your list of current games to see.");
        builder.setPositiveButton("Refresh games", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Crashlytics.log(Log.DEBUG, TAG, "Player acknowledged game " + game + " resumed by " + player);
                retrieveGames();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @OnClick(R.id.return_button)
    public void returnToMenu(Button button) {
        Intent i = new Intent(this, MenuActivity.class);

        //return to menu activity
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    //dev only
    @OnClick(R.id.delete_all)
    public void deleteAll() {
        for (Integer gameId : savedGames.keySet()) {
            cgm.leaveGame(gameId);
        }
        gameHolder.removeAllViews();
    }



}
