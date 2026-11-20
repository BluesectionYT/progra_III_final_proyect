package co.edu.uptc.interfaces;

import co.edu.uptc.enums.MessageCategory;

public interface Observer {
    public void update(MessageCategory category, String data);
}
