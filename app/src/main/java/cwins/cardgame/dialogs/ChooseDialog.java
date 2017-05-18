package cwins.cardgame.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cwins.cardgame.CardGameManager;
import cwins.cardgame.MainActivity;
import cwins.cardgame.R;

public class ChooseDialog extends CustomAlertDialog{
    private MainActivity activity;
    private String text;
    private CardGameManager cgm;
    TextView dialogText;

    public ChooseDialog(MainActivity activity, int type, CardGameManager cgm) {
        super(activity);
        this.activity = activity;
        this.cgm = cgm;
        this.text = activity.getApplicationContext().getResources().getString(type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        negative.setOnClickListener(this);
        positive.setOnClickListener(this);
        String yes = activity.getApplicationContext().getResources().getString(R.string.yes);
        String no = activity.getApplicationContext().getResources().getString(R.string.no);
        positive.setText(yes);
        negative.setText(no);

        dialogText = (TextView) findViewById(R.id.dialog_text);
        dialogText.setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                cgm.submitHandChoices();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.showScoresActivity();
                    }
                });
                break;
            case R.id.btn_negative:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
