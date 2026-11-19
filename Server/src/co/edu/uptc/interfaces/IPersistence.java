package co.edu.uptc.interfaces;

import co.edu.uptc.persistence.StateSnapshot;

public interface IPersistence {
    StateSnapshot readState(String path);

    void saveState(String path, StateSnapshot stateToSave);
}
