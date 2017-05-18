package cwins.cardgame.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private String boardId;
    private ArrayList<Card> cards;
    private HashMap<String, String> boardDisplayNames = new HashMap<>();

    public Board(String boardId) {
        this.boardId = boardId;
        cards = new ArrayList<>(5);

        // TODO: this will break if we ever have more than 5 boards
        boardDisplayNames.put("board_0", "Board 1");
        boardDisplayNames.put("board_1", "Board 2");
        boardDisplayNames.put("board_2", "Board 3");
        boardDisplayNames.put("board_3", "Board 4");
        boardDisplayNames.put("board_4", "Board 5");
        boardDisplayNames.put("board_5", "Board 6");
    }

    public void addCard(String cardName) {
        Card card = new Card(cardName);
        cards.add(card);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getBoardDisplayName() {
        return boardDisplayNames.get(boardId);
    }



}
