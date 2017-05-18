package cwins.cardgame.model;

import java.util.HashMap;
import java.util.Map;

public class HoleCards {
//    private ArrayList<Card> cards;
    private HashMap<Integer, Card> cards;

    public HoleCards(HashMap<Integer, Card> cards) {

        this.cards = cards;

        for (Map.Entry<Integer, Card> each : cards.entrySet()) {
            Card card = each.getValue();
            card.setHoleCardId(each.getKey());
        }
    }

    public HashMap<Integer, Card> getCards() {
        return cards;
    }

    public void setCards(HashMap<Integer, Card> cards) {
        this.cards = cards;
    }
}
