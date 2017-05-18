package cwins.cardgame.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cwins.cardgame.MainActivity;
import cwins.cardgame.R;

public class PauseDialog extends CustomAlertDialog {
    private String type;
    private String gamerTag;
    private MainActivity activity;
    TextView dialogText;

    public PauseDialog(MainActivity activity, int type, String gamerTag) {
        super(activity);
        this.activity = activity;
        this.type = activity.getApplicationContext().getResources().getString(type);
        this.gamerTag = gamerTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        negative.setOnClickListener(this);
        positive.setOnClickListener(this);
        String wait = activity.getApplicationContext().getResources().getString(R.string.wait);
        String menuReturn = activity.getApplicationContext().getResources().getString(R.string.menu_return);
        positive.setText(wait);
        negative.setText(menuReturn);

        dialogText = (TextView) findViewById(R.id.dialog_text);

        if (gamerTag == null) {
            dialogText.setText(type);
        } else {
            dialogText.setText(gamerTag + type);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                dismiss();
                break;
            case R.id.btn_negative:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.returnToMenu(false);
                    }
                });
                break;
            default:
                break;
        }
        dismiss();
    }

}
