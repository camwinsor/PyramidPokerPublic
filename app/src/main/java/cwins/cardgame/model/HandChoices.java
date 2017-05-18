package cwins.cardgame.model;

import java.util.ArrayList;
import java.util.HashMap;


public class HandChoices {
    private HashMap<Integer, Card> holeCards;
    private ArrayList<Integer> highCard;
    private ArrayList<Integer> holdem;
    private ArrayList<Integer> omaha;

    public HandChoices(ArrayList<Integer> highCard, ArrayList<Integer> holdem, ArrayList<Integer> omaha, HoleCards hole) {
        this.highCard = highCard;
        this.holdem = holdem;
        this.omaha = omaha;
        this.holeCards = hole.getCards();
    }

    public ArrayList<Integer> getHighCardId() {
        return highCard;
    }

    public ArrayList<Integer> getHoldemId() {
        return holdem;
    }

    public ArrayList<Integer> getOmahaId() {
        return omaha;
    }

    public void setHighCard(ArrayList<Integer> highCard) {
        this.highCard = highCard;
    }

    public void setHoldem(ArrayList<Integer> holdem) {
        this.holdem = holdem;
    }

    public void setOmaha(ArrayList<Integer> omaha) {
        this.omaha = omaha;
    }
}
