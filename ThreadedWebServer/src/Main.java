import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import ServerClient.ChatRoom;
import ServerClient.MyRunnable;
import ServerClient.RoomManager;

public class Main {
    public static void main(String[] args) throws IOException {

        // Create an ArrayList to store MyRunnable objects
        ArrayList<MyRunnable> runnArr = new ArrayList<>();

        //The RoomManager, which manages the rooms, is owned by Main
        RoomManager roomManager=new RoomManager();

        // Create the ServerSocket that waits for a client request at a certain port (8080)
        ServerSocket server = new ServerSocket(8080);

        // Continuously wait for client connections
        while (true) {

            // Accept a new client connection and create a Socket instance for communication
            Socket client = server.accept();

            // Create a new Thread to handle the client connection using MyRunnable
            // MyRunnable represents the logic for processing client interactions
            // It's passed the client Socket and a RoomManager for handling room-related tasks
            Thread thread = new Thread(new MyRunnable(client, roomManager));

            // Start the new thread to run the MyRunnable logic for this client
            thread.start();

        }
    }
}