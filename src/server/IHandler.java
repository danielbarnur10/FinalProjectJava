package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IHandler {
    void handle(InputStream inputFromUser, OutputStream inputToUser) throws IOException, ClassNotFoundException;
}
