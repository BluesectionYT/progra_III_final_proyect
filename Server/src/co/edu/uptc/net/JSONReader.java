package co.edu.uptc.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONReader {
    private Gson gsonInstance;

    public JSONReader() {
        this.gsonInstance = new GsonBuilder().create();
    }

    public String messageToJson(Message message) {
        return gsonInstance.toJson(message);
    }

    public Message jsonToMessage(String jsonMessage) {
        return gsonInstance.fromJson(jsonMessage, Message.class);
    }
}
