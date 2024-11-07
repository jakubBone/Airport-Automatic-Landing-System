package communication;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Messenger {
    private Gson gson;
    public Messenger() {
        this.gson = new Gson();
    }

    public void send(Object message, ObjectOutputStream out) throws IOException {
        if (message instanceof Integer) {
            // Send the enum as a plain string
            out.writeObject(((Integer) message).toString());
        } else {
            // Serialize other objects as JSON
            String jsonMessage = gson.toJson(message);
            out.writeObject(jsonMessage);
        }
        out.flush();

    }

    public String receive(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public <T> T parse(String jsonMessage, Class<T> type) {
        return gson.fromJson(jsonMessage, type);
    }
}
