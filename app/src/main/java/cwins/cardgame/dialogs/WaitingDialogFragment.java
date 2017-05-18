package cwins.cardgame.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cwins.cardgame.R;

/**
 * Created by CWins on 12/2/16.
 */

/** XXX CONSIDER RUNNING IN MENU **/

public class WaitingDialogFragment extends DialogFragment {
//XXX BROKEN
    LinearLayout wait_frag_ll;

    public WaitingDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static WaitingDialogFragment newInstance(String title, ArrayList<String> players) {
        WaitingDialogFragment frag = new WaitingDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putStringArrayList("players", players);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.waiting_frag_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("WAITING DIALOG", "SUCCESS");

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> players = getArguments().getStringArrayList("players");
        for (String current : players) {
            TextView cp_tv = new TextView(getDialog().getContext());
            cp_tv.setText(current);
            cp_tv.setTextColor(Color.RED);
            getDialog().addContentView(cp_tv, params);
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        wait_frag_ll.findViewById(R.id.wait_frag_ll);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> players = getArguments().getStringArrayList("players");
        for (String current : players) {
            //need to reference these individually
            TextView cp_tv = new TextView(getDialog().getContext());
            cp_tv.setText(current);
            cp_tv.setTextColor(Color.RED);
            wait_frag_ll.addView(cp_tv);
        }

        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(wait_frag_ll);


        return alertDialogBuilder.create();
    }

}
