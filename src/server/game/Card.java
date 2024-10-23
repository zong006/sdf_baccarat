package server.game;

public class Card {
    private String faceValue;

    public String getFaceValue() {
        return faceValue;
    }


    public Card(String faceValue) {
        this.faceValue = faceValue;
    }
    

    @Override
    public String toString(){
        return faceValue;
    }
}
