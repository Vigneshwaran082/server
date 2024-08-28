package home.learning.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class TCPServer {

    public static final int port = 7000;
    public static final String NEW_LINE = "\r\n";
    public static final String CONTENT_TYPE="Content-Type:";
    public static final String CONTENT_LENGTH="Content-Length:";
    public  Properties serverProperties = new Properties();



    public static void main(String[] args) throws Exception {
       new TCPServer().runServer();
    }

    void runServer() throws java.net.UnknownHostException, java.io.IOException{
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port ...");
        int count = 0;
        while (true) {
            Socket socket = serverSocket.accept();
            appendOneTimeLog("Server will accept new connections from now on Port: "+port,count++);
            InputStream stream =socket.getInputStream();

            String request =getInputFromClient(stream);
            boolean isHttpRequest =isHttpRequest(request);

            OutputStream oStream = socket.getOutputStream();


        }
    }


    private Properties loadServerProperties() throws IOException{
        Properties response = new Properties();
        Path path = Paths.get(".","server.properties");
        boolean isServerProExists = Files.exists(path);
        if(!isServerProExists){
            System.out.println("Server Properties not exist ....");
            System.err.println("Exiting the System....");
            System.exit(1);
        }
        response.load(new FileReader(path.toFile()));
       return response;




    }



    void appendOneTimeLog(String log,int count){
        if(count ==1){
            System.out.println(log);
        }
    }

    String getInputFromClient(InputStream stream) throws IOException{
        BufferedReader buffReader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer response = new StringBuffer(); //Thread safety
        while(buffReader.readLine() != null){
           response.append(buffReader.readLine());
           response.append("\n");
        }
        return response.toString();
    }


    void respondToClient(){

    }

    /*
    * Response looks like the below example :
    * "HTTP/1.1 200 OK\r\n" +
    * "Content-Type: text/plain\r\n" +
    * "Content-Length: 28\r\n" +
    * "\r\n" +
    * "Hello, this is a simple HTTP server!";
    * */
    private String getRespondToHttpClient(String contentType , String httpStatusCode,String payload){
        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1  ");
        buffer.append(httpStatusCode);
        buffer.append(NEW_LINE);
        buffer.append(CONTENT_TYPE+contentType);
        buffer.append(NEW_LINE);
        buffer.append(CONTENT_LENGTH+payload.length());
        buffer.append(NEW_LINE);
        buffer.append(payload);
        return buffer.toString();
    }


    private boolean isHttpRequest(String firstLine){
    //Check if the first line starts with a valid HTTP method and contains HTTP version
            return firstLine.startsWith("GET") ||
                    firstLine.startsWith("POST") ||
                    firstLine.startsWith("PUT") ||
                    firstLine.startsWith("DELETE") ||
                    firstLine.startsWith("HEAD") ||
                    firstLine.startsWith("OPTIONS") ||
                    firstLine.startsWith("PATCH") ||
                    firstLine.startsWith("CONNECT") ||
                    firstLine.startsWith("TRACE");
        }


    }
