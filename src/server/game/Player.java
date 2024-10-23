package server.game;

import java.util.*;

public class Player {

    List<Card> hand = new ArrayList<>();
    private String name;
    private String bet;
    private boolean hasPlacedBet = false;
    private int betAmount;
    private boolean winBet = false;
    private int payout;
    private boolean loggedIn = false;

    public boolean isHasPlacedBet() {
        return hasPlacedBet;
    }
    public void setHasPlacedBet(boolean hasPlacedBet) {
        this.hasPlacedBet = hasPlacedBet;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }
    public int getPayout() {
        return payout;
    }
    public void setPayout(int payout) {
        this.payout = payout;
    }
    public boolean isWinBet() {
        return winBet;
    }
    public void setWinBet(boolean winBet) {
        this.winBet = winBet;
    }
    public int getBetAmount() {
        return betAmount;
    }
    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }
    public String getBet() {
        return bet;
    }
    public void setBet(String bet) {
        this.bet = bet;
    }
    public void addToHand(Card cardDrawn){
        hand.add(cardDrawn);
    }
    public List<Card> getHand() {
        return hand;
    }
    
    
}
