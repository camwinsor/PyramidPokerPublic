package cwins.cardgame.dialogs;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cwins.cardgame.R;

public class OkDialog extends CustomAlertDialog {
    private String type;
    private String gamerTag;
    private Activity activity;
    TextView dialogText;

    public OkDialog(Activity activity, int type, String gamerTag) {
        super(activity);
        this.activity = activity;
        this.type = activity.getApplicationContext().getResources().getString(type);
        this.gamerTag = gamerTag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        negative.setVisibility(View.GONE);
//        positive.setOnClickListener(this);
        String ok = activity.getApplicationContext().getResources().getString(R.string.ok);
        positive.setText(ok);
        positive.setTextColor(Color.parseColor("#FF2078C6"));
        positive.setPadding(30, 10, 30, 10);

        dialogText = (TextView) findViewById(R.id.dialog_text);

        if (gamerTag == null) {
            dialogText.setText(type);
        } else {
            dialogText.setText(gamerTag + type);
        }
    }

//    @Override
//    public void onClick(View v) {
//        dismiss();
//    }




}
