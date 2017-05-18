package cwins.cardgame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class MatchupTransitionIterator implements Iterator<MatchupTransition> {
    private ArrayList<Matchup> matchups;
    private Iterator<Matchup> matchupIterator;
    private MatchupTransition previous;

    public MatchupTransitionIterator(ArrayList<Matchup> matchups) {
        this.matchups = matchups;

        // TODO: super important that this list is sorted before we start iterating over it
        Collections.sort(this.matchups);
        this.matchupIterator = this.matchups.iterator();
    }

    @Override
    public boolean hasNext() {
        return this.matchupIterator.hasNext();
    }

    @Override
    public MatchupTransition next() {
        // returns a new MatchupTransition for every 3-tuple of (opponent, board, game_type)
        // as well as tracking if this is the same opponent/board as the last MatchupTransition
        boolean newOpponent = false;
        boolean newBoard = false;

        // initial iteration
        if (this.previous == null) {
            Matchup next = this.matchupIterator.next();
            MatchupTransition mt = new MatchupTransition(true, true, next);
            this.previous = mt;
            return mt;
        }

        Matchup next = this.matchupIterator.next();
        if (!next.getOppoGoogleId().equals(previous.getMatchup().getOppoGoogleId())) {
            newOpponent = true;
        }

        if (!next.getBoard().getBoardId().equals(previous.getMatchup().getBoard().getBoardId())) {
            newBoard = true;
        }

        MatchupTransition mt = new MatchupTransition(newOpponent, newBoard, next);
        this.previous = mt;
        return mt;
    }
}
