package ServerClient;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HTTPRequest {

    private String verb_;
    private String parameter_;
    private String header_;
    private String fileType_;
    private final InputStream inputStream_;

    private String fileName;

    Map headers = new HashMap<String,String>();

    //the initial constructor which requires a socket, finds the socket input stream, and assigns that as a variable
    public HTTPRequest(Socket socket) throws IOException {
        verb_=null;
        parameter_=null;
        header_=null;
        fileType_=null;
        inputStream_=socket.getInputStream();

    }

    //all of these get functions allow you to access the saved variable
    public String getVerb(){
        return verb_;
    }
    public String getParameter(){
        return parameter_;
    }
    public String getHeader(){
        return header_;
    }
    public String getFileType(){
        return fileType_;
    }

    /* This finds the class inputStream and puts it into the scanner. It scans through the text and parses it, assigning
    the verb GET, the parameter, and the header. If the parameter is the generic "/" it changes it to "/index.html",
    then it defines the fileType by saving everything after the end of the .*/
    public Boolean parse() {
        //store the get, the file, and the http, throw an exception if it fails
        Scanner sc = new Scanner(inputStream_);

        if (sc.hasNext()) {
            String[] inputArray = sc.nextLine().split(" ");
            if( inputArray.length != 3)
                return false;
            verb_ = inputArray[0];
            parameter_ = inputArray[1];
            header_ = inputArray[2];

            if (parameter_.equals("/")) {
                parameter_ = "/";
            }

            int i = parameter_.lastIndexOf('.');
            if (i > 0) {
                fileType_ = parameter_.substring(i + 1);
                System.out.println(fileType_);
            }

            boolean done = false;
            while( !done ) {

                String requestLine = sc.nextLine();
                System.out.println(requestLine);
                if( requestLine.length() != 0 ) {
                    String[] pieces = requestLine.split(": ");
                    String key = pieces[0];
                    String value = pieces[1];
                    headers.put(key, value);
                }
                else {
                    done = true;
                }
            }

        }
        return true;
    }

    //Check if a request is a WebSocket request
    public Boolean isWebSocket(){
        //boolean checks for if the WebSocket key is present
        boolean isKeyPresent = headers.containsKey("Sec-WebSocket-Key");
        //returns the boolean
        return isKeyPresent;
    }
    public String getWebSocketKey()
    {
        return (String)headers.get("Sec-WebSocket-Key");
    }

}



