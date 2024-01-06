package ServerClient;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyRunnable implements Runnable {

    private final Socket client_;
    private final RoomManager rm_;
    private String username_;
    private String roomName_;
    private String message_;

    public MyRunnable(Socket client, RoomManager rm) {
        client_ = client;
        username_=null;
        roomName_=null;
        message_=null;
        rm_=rm;
    }

    private String decodeMessage() throws IOException {

        Boolean fin_;
        int opcode_;
        Boolean mask_;
        long payloadLen_;
        byte[] maskBytes = new byte[0];
        String message;

        DataInputStream in = new DataInputStream(client_.getInputStream());

            System.out.println("handling incoming message for: " + client_);

            byte[] input = in.readNBytes(2);

            fin_ = (input[0] & 0x80) > 0;

            opcode_ = (input[0] & 0x0F);

            mask_ = (input[1] & 0x80) > 0;

            payloadLen_ = (input[1] & 0x7f);

            if (payloadLen_ == 126) {
                payloadLen_ = in.readShort();
            } else if (payloadLen_ == 127) {
                payloadLen_ = in.readLong();
            }

            System.out.println("Masked: " + mask_ + " Length: " + payloadLen_);

            if (mask_) {
                maskBytes = in.readNBytes(4);
            }

            byte[] payloadArr = in.readNBytes((int) payloadLen_);

            if (mask_) {
                for (int i = 0; i < payloadArr.length; i++) {
                    payloadArr[i] = (byte) (payloadArr[i] ^ maskBytes[i % 4]);
                }
                message = new String(payloadArr, StandardCharsets.UTF_8);
                System.out.println("MESSAGE: " + message + "---------------------------------------------");
            } else {
                message = new String(payloadArr, StandardCharsets.UTF_8);
            }
            return message;
    }

    public void encodeMessage(String message) {
        try {
            DataOutputStream dataOut = new DataOutputStream(client_.getOutputStream());
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            // Construct a WebSocket text frame
            byte[] frame = new byte[messageBytes.length + 2];
            frame[0] = (byte) 0x81;  // Text frame opcode
            frame[1] = (byte) messageBytes.length;  // Length of the message

            System.arraycopy(messageBytes, 0, frame, 2, messageBytes.length);

            dataOut.write(frame);
            dataOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseMessType(String message) {
       String cmd_;
       String[] messArr = message.split(":");
       cmd_ = messArr[0];
        username_=messArr[1];
        roomName_=messArr[2];
       if(cmd_.equals("join")){
           rm_.joinRoom(this);
       }
       if (cmd_.equals("leave")){
           rm_.leaveRoom(this);
        }
      if (cmd_.equals("message")){
          message_=messArr[3];
            rm_.sendMess(message_, this);
        }
    }

    public String getUsername_(){
        return username_;
    }
    public String getRoomName_(){
        return roomName_;
    }
    public String getMessage_(){
        return message_;
    }

    public static String makeJoinMsg(String room, String name){
        return "{ \"type\": \"join\", \"room\": \"" + room + "\", \"user\": \"" + name + "\" }";
    }
    public static String makeMsgMsg(String room, String name, String message) {
        return "{ \"type\": \"message\", \"user\": \"" + name + "\", \"room\": \"" + room + "\", \"message\": \"" + message + "\" }";
    }
    public static String makeLeaveMsg(String room, String name) {
        return "{ \"type\": \"leave\", \"room\": \"" + room + "\", \"user\": \"" + name + "\" }";
    }

    @Override
    public void run() {
        //Create a response object
        HTTPResponse response = new HTTPResponse();
        //Create a request object
        HTTPRequest request = null;
        try {
            //Pass the client socket through for the request
            request = new HTTPRequest(client_);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            //Calls the parse method with the request object, and if it can't parse then says bed request
            //This was mostly used in testing when at one point it wasn't working
            if (!request.parse()) {
                System.out.println("bad req");
            }
            //If the request is a Websocket, then it enters this loop
            if (request.isWebSocket()) {
                response.sendWebSockHandshake(client_, request.getWebSocketKey());
                String endMessage = new String(new byte[]{3,-23});
                boolean done = false;
                while (!done) {

                    //this decodes the packet after the handshake was completed
                    String msg = decodeMessage();

                    if( msg.equals(endMessage)){
                        System.out.println("closing socket");
                        done = true;

                        //This leave room does the wrong name?
                        rm_.leaveRoom(this);

                        client_.close();
                    }
                    else{
                        parseMessType(msg);
                    }

                }
            }
            else {

                //creates the file that is required to send into the response.sendResponse
                File file = response.createFile(request.getParameter());

                //sends the response back to the client
                response.sendHTTPResponse(client_, file, request.getFileType());
                client_.close();

            }
        } catch (Exception e) {
            //if there are any errors above, the error page is sent to site to prevent crashing
            try {
                response.sendFailResponse(client_);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}






