package server.game;

import java.util.*;

public class Deck {
    
    private List<Card> cards;

    // constructor
    public Deck(int numOfDecks) {
        cards = new ArrayList<>();
        generateDeck(numOfDecks);
    }

    private void generateDeck(int numOfDecks){
        for (int i = 0 ; i < numOfDecks ; i++){
            for (int j = 1 ; j <= 13 ; j++ ){
                for (int k = 1 ; k <= 4 ; k++){
                    Card card = new Card(Integer.toString(j)+"."+Integer.toString(k));
                    cards.add(card);
                }
            }
        }

    }

    public List<Card> getCards(){
        return cards;
    }


}
