package cwins.cardgame.dialogs;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.games.multiplayer.Invitation;

import cwins.cardgame.MenuActivity;
import cwins.cardgame.R;

public class InviteDialog extends CustomAlertDialog {
    private String gamerTag;
    private MenuActivity activity;
    private Resources resources;
    private Invitation invite;

    TextView dialogText;

    public InviteDialog(MenuActivity activity, Invitation invite, String gamerTag) {
        super(activity);
        this.activity = activity;
        this.invite = invite;
        this.gamerTag = gamerTag;
        this.resources = activity.getApplicationContext().getResources();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        negative.setOnClickListener(this);
        positive.setOnClickListener(this);
        String back = resources.getString(R.string.back_button);
        String join = resources.getString(R.string.join_game);
        negative.setText(back);
        positive.setText(join);

        dialogText = (TextView) findViewById(R.id.dialog_text);
        String text = resources.getString(R.string.invite_received);
        dialogText.setText(gamerTag + text);
    }

    @Override
    public void onClick(View v) {
        final Invitation inv = invite;
        switch (v.getId()) {
            case R.id.btn_positive:
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.acceptInvite(inv);
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
