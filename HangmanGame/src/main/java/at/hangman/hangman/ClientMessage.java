package at.hangman.hangman;

import java.util.ArrayList;

public class ClientMessage {
    private ArrayList<HangmanMessage> messages = new ArrayList<>();

    public ArrayList<HangmanMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<HangmanMessage> messages) {
        this.messages = messages;
    }
    public HangmanMessage getFirstMessage(){
        if(messages ==null || messages.size()==0){
            return null;
        }
        return messages.get(0);
    }
    public void addMessage(HangmanMessage message){
        messages.add(message);
    }

    public ClientMessage() {
    }
    public ClientMessage(HangmanMessage message) {
        addMessage(message);
    }
}
