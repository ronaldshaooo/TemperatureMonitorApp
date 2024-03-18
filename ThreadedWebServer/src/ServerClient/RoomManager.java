package ServerClient;

import java.util.ArrayList;

public class RoomManager {
    ArrayList<ChatRoom> chatRmArr = new ArrayList<>();
    public RoomManager() {

    }
    private ChatRoom findRoom(String roomName) {

        for (ChatRoom room : chatRmArr) {
            if (room.name_.equals(roomName)) {
                return room;
            }
        }
        return null;
    }

    public void joinRoom(String msg, MyRunnable runnable) {

        ChatRoom room = findRoom(runnable.getRoomName_());

        if (room != null) {
            room.addClient(runnable, msg);
        } else {
            ChatRoom newChatRoom = new ChatRoom(runnable.getRoomName_());
            chatRmArr.add(newChatRoom);
            newChatRoom.addClient(runnable, msg);
        }
    }

    public void leaveRoom(MyRunnable runnable) {

        ChatRoom room = findRoom(runnable.getRoomName_());

        if (room != null) {
            room.removeClient(runnable);
            //if the room is empty, delete it here?
        }

    }

    public void sendMess(String msg, MyRunnable runnable) {
        ChatRoom room = findRoom(runnable.getRoomName_());
        if (room != null) {
            room.sendMessage(runnable, msg);
        }
    }
}
