package server.game;

import java.util.ArrayList;
import java.util.List;

public class Banker {
    List<Card> hand = new ArrayList<>();

    public List<Card> getHand() {
        return hand;
    }

    public void addToHand(Card cardDrawn){
        hand.add(cardDrawn);
    }
}
