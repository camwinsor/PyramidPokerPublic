package cwins.cardgame.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import cwins.cardgame.R;


public class CustomAlertDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Dialog dialog;
    public Button positive, negative;

    public CustomAlertDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog);
        positive = (Button) findViewById(R.id.btn_positive);
        negative = (Button) findViewById(R.id.btn_negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                dismiss();
                break;
            case R.id.btn_negative:
                // completed in subclass
                break;
            default:
                break;
        }
        dismiss();
    }

}
