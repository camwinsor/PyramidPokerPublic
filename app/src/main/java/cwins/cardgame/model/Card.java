package cwins.cardgame.model;


import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.lang.reflect.Field;

import cwins.cardgame.CardValues;
import cwins.cardgame.R;

public class Card {
    private Integer resourceId;
    private Integer holeCardId;
    private String cardName;

    public Card(String cardName) {
        this.cardName = cardName;

        CardValues cvs = new CardValues();
        String card = cvs.cards.get(cardName);

        try {
            Class res = R.drawable.class;
            Field field = res.getField(card);
            resourceId = field.getInt(null);
        }
        catch (Exception e) {
            Crashlytics.log(Log.DEBUG, "Card Model Error: ", "failed to retrieve card resource ID.");
        }
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setHoleCardId(Integer holeCardId) {
        this.holeCardId = holeCardId;
    }

    public Integer getHoleCardId() {
        return holeCardId;
    }
}
/**

    HashMap<String, String> ihand;
ihand = hand;
        iforscore = forscore;
        CardValues cvs = new CardValues();
        Crashlytics.log(Log.DEBUG, TAG, "showCard/iforscor: " + iforscore.toString());

        for (int i=0; i<7; i++) {
        //cast each card in hand as String, convert server-card-id to local-card-id with cvs, then form & load resID
        String c = (String) hand.get(Integer.toString(i));
        String x = cvs.cards.get(c);
        Integer resID = this.getResources().getIdentifier(x, "drawable", this.getPackageName());

        //Crashlytics.log(Log.DEBUG, TAG, "showCard/resID: " + resID.toString());

        //load card images into PIV slots
        PIVs.get(i).setImageResource(resID);
        PIVs.get(i).setAlpha((float) 1);

        /**
         * Tags associated with each dealt card follow card from PIV slot to SIV slot
         *
         * Set Tag k,v legend:
         *
         * (R.id.original, thisviewID)
         * (R.id.card_id, card ID in server format -- Ah, Tc, 9d, ...)
         * (R.id.local_card, R.drawable.cardID in local format)
         *


        PIVs.get(i).setTag(R.id.original, PIVs.get(i));
        PIVs.get(i).setTag(R.id.card_id, ihand.get(Integer.toString(i)));
        PIVs.get(i).setTag(R.id.local_card, resID);
        }
*/