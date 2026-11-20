package co.edu.uptc.net;

import co.edu.uptc.enums.MessageCategory;

public class Message {
    private MessageCategory category;
    private String text;

    public Message(MessageCategory category, String text) {
        this.category = category;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageCategory getCategory() {
        return category;
    }
}
