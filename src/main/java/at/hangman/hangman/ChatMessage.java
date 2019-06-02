package at.hangman.hangman;

public class ChatMessage {
    private MessageType type;
    private String room;
    private String content;
    private String sender;

    public enum MessageType {
        CHAT,
        JOIN,
        ERROR,
        LEAVE
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
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