package server.game;
import java.io.*;
import java.util.*;

public class BaccaratEngine implements DataFileDir{

    Player player;
    Banker banker;
    Deck deck;

    public BaccaratEngine(int numOfDecks) throws IOException {
        this.deck = new Deck(numOfDecks);
        shuffleDeck(deck);

        this.player = new Player();
        this.banker = new Banker();
    }
    
    public String takeInput(String command) throws IOException{
        String retStatement = "";

        while (!player.isLoggedIn()){
            if (command.contains("login")){
                String[] userInfo = command.split(" ");
                String name = userInfo[1];
                String value = userInfo[2];
                String userFile = dataFileDir + name + ".db";
                
                player.setName(name);
                player.setLoggedIn(true);
            
                File file = new File(userFile);
                if (file.exists()){
                    // if user has an account, add the amount of money into existing account
                    FileReader fr = new FileReader(userFile);
                    BufferedReader br = new BufferedReader(fr);
        
                    String existingAmount = "";
                    while ((existingAmount = br.readLine())!=null){
                        int newAmount = Integer.parseInt(existingAmount) + Integer.parseInt(value);   
                        FileWriter fw = new FileWriter(userFile);
                        BufferedWriter bw = new BufferedWriter(fw); 
                        bw.write(Integer.toString(newAmount));
                        bw.flush();
                    }
                    retStatement = "Account exist. Added " + value + " to account";
                }
                else{
                    FileWriter fw = new FileWriter(userFile);
                    BufferedWriter bw = new BufferedWriter(fw); 
                    bw.write(value);
                    bw.flush();
                    retStatement = "User file created. Account value: " + value;
                }
                return retStatement;
            }
            else {
                retStatement = "Log in with: login <name> <amount>";
                return retStatement;
            }
        }
       
        if (command.contains("bet")){
            
            String userFile = dataFileDir + player.getName() + ".db";
            String[] bet = command.split(" ");
            int betAmount = Integer.parseInt(bet[1]);
            // check if account has sufficient value
            FileReader fr = new FileReader(userFile);
            BufferedReader br = new BufferedReader(fr);
            String existingAmount = br.readLine();
            if (Integer.parseInt(existingAmount) >= betAmount){
                player.setBetAmount(betAmount);
                retStatement = "Current bet is " + betAmount;   
            }
            else{
                retStatement = "Insufficient amount in account. Current balance is: " + existingAmount;
            }
            return retStatement;
        }
        else if (command.contains("deal")){
            String[] deal = command.split(" ");
            String playerBet = deal[1];
            String winner = "";
            player.setBet(playerBet);
            String playerCards = "P|";
            String bankerCards = "B|";
    
            List<Card> dealtCards = new ArrayList<>();

            System.out.printf("Player bets %d on %s winning.\n", player.getBetAmount(), player.getBet());
            // first draw 4 cards. deal to player first

            for (int i = 0 ; i < 4 ; i++){
                Card cardDrawn = drawCard();
                if (i%2==0){
                    player.addToHand(cardDrawn);
                    System.out.println("Player draws: " + cardDrawn);
                    playerCards = playerCards + cardDrawn.getFaceValue().substring(0, cardDrawn.getFaceValue().length()-2) + "|";
                }
                else{
                    banker.addToHand(cardDrawn);
                    System.out.println("Banker draws: " + cardDrawn);
                    bankerCards = bankerCards + cardDrawn.getFaceValue().substring(0, cardDrawn.getFaceValue().length()-2) + "|";
                }
                dealtCards.add(cardDrawn);
            }
            // check the sum for player and banker.
            int pSum = sumHand(player.getHand());
            int bSum = sumHand(banker.getHand());
            System.out.println("player hand sum: "+ pSum);
            System.out.println("banker hand sum: "+ bSum);
            if (pSum<=15){
                Card cardDrawn = drawCard();
                player.addToHand(cardDrawn);
                System.out.println("Player draws: " + cardDrawn);
                playerCards = playerCards + cardDrawn.getFaceValue().substring(0, cardDrawn.getFaceValue().length()-2) + "|";
            }
            if (bSum<=15){
                Card cardDrawn = drawCard();
                banker.addToHand(cardDrawn);
                System.out.println("Banker draws: " + cardDrawn);
                bankerCards = bankerCards + cardDrawn.getFaceValue().substring(0, cardDrawn.getFaceValue().length()-2) + "|";
            }
            if (checkScore(player.getHand()) == checkScore(banker.getHand())){
                player.setDraw(true);
                retStatement = checkWinner(winner);
            }
            else {
                if (checkScore(player.getHand()) > checkScore(banker.getHand())){
                    winner = "p";
                    System.out.printf("Total score for player: %d.\n", checkScore(player.getHand()));
                    System.out.printf("Total score for banker: %d.\n", checkScore(banker.getHand()));
                    System.out.printf("Player bet on %s winning. \n", player.getBet());
                }
                else if (checkScore(player.getHand()) < checkScore(banker.getHand())){
                    winner = "b";
                    System.out.printf("Total score for player: %d.\n", checkScore(player.getHand()));
                    System.out.printf("Total score for banker: %d.\n", checkScore(banker.getHand()));
                    System.out.printf("Player bet on %s winning. \n", player.getBet());
                }
                retStatement = playerCards + "," + bankerCards;
                retStatement += "       " + checkWinner(winner, player);

                FileReader fr = new FileReader(dataFileDir + player.getName() + ".db");
                BufferedReader br = new BufferedReader(fr);
                String accountValue = br.readLine();
                retStatement += "    " + "Account value: " + accountValue;
            }
            if (player.isWinBet()){
                if (checkSixCardRule()){

                    player.setPayout(player.getBetAmount()/2);
                }
                player.setPayout(player.getBetAmount());
            }
            // end of each round, update account value, clear player's hands, and shuffle deck
            updateUserFile(player.isWinBet(), player.isDraw());
            player.getHand().clear();
            banker.getHand().clear();
            shuffleDeck(deck);
            return retStatement;
        }
        else if (command.equals("close")){
            retStatement = "Game closed";
            player.setLoggedIn(false);

            return retStatement;
        }
        else{
            retStatement = "Enter either: bet <amount>, deal <p or b>, or close";
            return retStatement;
        }
    }
// ==========================================================================================
    public void shuffleDeck(Deck deck) throws IOException{
        
        List<Card> cards = deck.getCards();

        Random random = new Random();
        Collections.shuffle(cards, random);

        String deckFilePath = dataFileDir + "cards.db";
        try (FileWriter fw = new FileWriter(deckFilePath)) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                for (Card c : cards){
                    bw.write(c.getFaceValue());
                    bw.newLine();
                    bw.flush();
                }
            }
        }
    }
// ==========================================================================================
    public Card drawCard() throws IOException{
        Queue<Card> cards = new ArrayDeque<>();
        try (FileReader fr = new FileReader(dataFileDir + "cards.db")) {
            try (BufferedReader br = new BufferedReader(fr)) {
                String faceValue = "";

                while ((faceValue = br.readLine())!=null){
                    Card card = new Card(faceValue);
                    cards.add(card);
                }
            }
        } 

        Card cardDrawn = cards.remove();
        
        try (FileWriter fw = new FileWriter(dataFileDir + "cards.db")) {
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                for (Card nextCard : cards){
                    nextCard = cards.remove();
                    bw.write(nextCard.getFaceValue());
                    bw.newLine();
                    bw.flush();
                }
            }
        }
        return cardDrawn;
    }
// ==========================================================================================
    public int sumHand(List<Card> hand){

        int sum = 0;
        for (Card card : hand){
            String faceValue = card.getFaceValue();
            faceValue = faceValue.substring(0, faceValue.length()-2);
            sum += Integer.parseInt(faceValue);
        }
        return sum;
    }
// ==========================================================================================
    public int checkScore(List<Card> hand){
        int sumHand = sumHand(hand);
        return sumHand%10;
    }
// ==========================================================================================
    public String checkWinner(String winner, Player player){
        int bet = player.getBetAmount();
        if (player.getBet()==winner){
            player.setWinBet(true);
            
            return "Player wins the bet. Wins "+bet;
        }
        return "Player loses the bet. Lost "+ bet;
    }
// ==========================================================================================
    public String checkWinner(String winner){
        return "This round is a draw";
    }
// ==========================================================================================
    public boolean checkSixCardRule(){
        int pScore = checkScore(player.getHand());
        int bScore = checkScore(banker.getHand());

        if ((pScore < bScore) && bScore==6){
            return true;
        }
        return false;
    }
// ==========================================================================================
    public void updateUserFile(Boolean playerWins, Boolean draw) throws IOException{
        if (draw) return;
        else {
            String name = player.getName();
            String filePath = dataFileDir + name + ".db";
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);

            String line= "";
            int newValue = 0;
            while ((line = br.readLine())!=null){
                int accountValue = Integer.parseInt(line.trim());
                if (playerWins){
                    newValue = accountValue + player.getPayout();
                }
                else {
                    newValue = accountValue - player.getBetAmount();
                }
            }

            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Integer.toString(newValue));
            bw.flush();
            bw.close();
            fw.close();
            br.close();
            fr.close();
        }
    }
// ==========================================================================================
}