package co.edu.uptc.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONReader {
    private Gson gsonInstance;

    public JSONReader() {
        this.gsonInstance = new GsonBuilder().create();
    }

    public String messageToJson(MessageCategory category ,String message){
        Message temporalMessage = new Message(category, message);
        String result =  gsonInstance.toJson(temporalMessage);
        return result;
    }

    public Message jsonToMessage(String jsonMessage){
        return gsonInstance.fromJson(jsonMessage, Message.class);
    }
}
