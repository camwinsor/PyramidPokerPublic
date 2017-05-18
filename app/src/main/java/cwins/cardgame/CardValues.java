package cwins.cardgame;

import java.util.HashMap;


public class CardValues {
    public HashMap<String, String> cards = new HashMap<>(52);
    public CardValues() {

        String[] suits = new String[]{"h", "c", "d", "s"};
        String[] ranks = new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};
        for (String Rcurrent : ranks) {
            for (String Scurrent : suits) {
                cards.put(Rcurrent + Scurrent, Scurrent + "_" + Rcurrent.toLowerCase());
            }
        }
    }
}
