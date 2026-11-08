package co.edu.uptc.net;

public class Message {
    private String category;
    private String text;

    public Message(String category, String text) {
        this.category = category;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }
}
