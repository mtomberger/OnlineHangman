package at.hangman.hangman;

import java.util.ArrayList;

public class HangmanMessage {
    private MessageType type;

    private String content;

    //client id
    private String senderId;
    //username
    private String sender;

    public enum MessageType {
        PLAY,
        ID,
        JOIN,
        INIT,
        ERROR,
        LEAVE
    }

    public HangmanMessage() {
    }

    public HangmanMessage(MessageType type, String content, String senderId, String sender) {
        this.type = type;
        this.content = content;
        this.senderId = senderId;
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}