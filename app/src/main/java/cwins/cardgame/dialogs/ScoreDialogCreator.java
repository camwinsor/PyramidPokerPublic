package cwins.cardgame.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import cwins.cardgame.R;
import cwins.cardgame.model.UserScore;

public class ScoreDialogCreator extends DialogFragment {
    private String roundNumber;
    private boolean isScoreActivity;
    private ArrayList<UserScore> totalScores;
    private FrameLayout fl;

    public void setTotalScores(ArrayList<UserScore> totalScores) {
        this.totalScores = totalScores;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle b = getArguments();
        this.roundNumber = b.getString("round");
        this.isScoreActivity = b.getBoolean("scoring");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.score_dialog_layout, null);
//        fl = (FrameLayout) findViewById(android.R.id.custom);
//        fl.addView(view, new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        TextView finalDialogTitle = (TextView) fl.findViewById(R.id.final_dialog_title);
        if (isScoreActivity) {
            finalDialogTitle.setText("End of round " + roundNumber);
        } else {
            finalDialogTitle.setText("Start of round " + roundNumber);
        }
        finalDialogTitle.setTypeface(Typeface.MONOSPACE);

        TextView player1 = (TextView) fl.findViewById(R.id.player_1);
        TextView player2 = (TextView) fl.findViewById(R.id.player_2);
        TextView player3 = (TextView) fl.findViewById(R.id.player_3);
        TextView player4 = (TextView) fl.findViewById(R.id.player_4);
        TextView player5 = (TextView) fl.findViewById(R.id.player_5);
        TextView player6 = (TextView) fl.findViewById(R.id.player_6);
        ArrayList<TextView> playerTags = new ArrayList<>();
        playerTags.add(player1);
        playerTags.add(player2);
        playerTags.add(player3);
        playerTags.add(player4);
        playerTags.add(player5);
        playerTags.add(player6);

        TextView score1 = (TextView) fl.findViewById(R.id.score_1);
        TextView score2 = (TextView) fl.findViewById(R.id.score_2);
        TextView score3 = (TextView) fl.findViewById(R.id.score_3);
        TextView score4 = (TextView) fl.findViewById(R.id.score_4);
        TextView score5 = (TextView) fl.findViewById(R.id.score_5);
        TextView score6 = (TextView) fl.findViewById(R.id.score_6);
        ArrayList<TextView> playerScores = new ArrayList<>();
        playerScores.add(score1);
        playerScores.add(score2);
        playerScores.add(score3);
        playerScores.add(score4);
        playerScores.add(score5);
        playerScores.add(score6);

        Iterator<UserScore> it = totalScores.iterator();

        for (int i=0; i<=totalScores.size(); i++) {
            if (it.hasNext()) {
                UserScore current = it.next();

                Integer currentScore = current.getTotalScore();
                String currentTag = current.getUser().getGamerTag();

                Log.d("SCOREDIALOGCREATOR: ", currentTag + " -- " + currentScore);
                TextView tagTv = playerTags.get(i);
                TextView scoreTv = playerScores.get(i);

                tagTv.setText(currentTag);
                tagTv.setTypeface(Typeface.MONOSPACE);
                tagTv.setTextSize(13);
                tagTv.setVisibility(View.VISIBLE);
                scoreTv.setText(currentScore.toString());
                scoreTv.setTypeface(Typeface.MONOSPACE);
                scoreTv.setTextSize(13);
                scoreTv.setVisibility(View.VISIBLE);
            }
        }

        builder.setView(fl);


        if (isScoreActivity) {
            builder.setNegativeButton("Next Round", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
        } else {
            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });
        }

        return builder.create();
    }

}