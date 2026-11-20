package co.edu.uptc.interfaces;

public interface IPasswordHasher {
    String hash(String password);

    boolean verify(String password, String storedHash);
}
