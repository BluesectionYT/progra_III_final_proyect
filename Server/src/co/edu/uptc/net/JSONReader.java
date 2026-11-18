package co.edu.uptc.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONReader {
    private Gson gsonInstance;

    public JSONReader() {
        this.gsonInstance = new GsonBuilder().create();
    }

    public String messageToJson(String category ,String message){
        Message temporalMessage = new Message(category, message);
        String result =  gsonInstance.toJson(temporalMessage);
        return result;
    }

    public String[] jsonToMessage(String jsonMessage){
        Message temporalMessage = gsonInstance.fromJson(jsonMessage, Message.class);
        String[] result = new String[] {temporalMessage.getCategory(), temporalMessage.getText()};
        return result;
    }
}
