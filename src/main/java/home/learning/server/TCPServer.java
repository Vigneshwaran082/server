package home.learning.server;

import org.junit.Assert;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class TCPServer {

    public static final int port = 7000;
    public static final String NEW_LINE = "\r\n";
    public static final String CONTENT_TYPE = "Content-Type:";
    public static final String CONTENT_LENGTH = "Content-Length:";
    public Properties serverProperties = new Properties();
    private static final TCPServer server = new TCPServer();


    /*
     * Initialize the server properties. Load the server.properties file
     * and create a properties Object will be
     * held in memory for the server.
     *
     * If loading fails , Server will not start.
     * */
    public TCPServer() {
        try {
            serverProperties = loadServerProperties();
        } catch (IOException e) {
            System.err.println("Unable to load the server properties");
            System.exit(1);
        }
    }

    /*
     * This method will start the server and listen to the incoming requests.
     * This method act as the starting port to start the server and do all the actions
     * */
    public static void start() throws Exception {
        server.runServer();
    }

    /*
     * This method will run the server and listen to the incoming requests.
     * @Throws UnknownHostException : If the host is not found
     * @Throws IOException : If there is an error reading the input stream
     *  @Throws IOException : If there is an error reading the input stream
     *  @Throws IOException : If there is an error reading the input stream
     *  @Throws IOException : If there is an error reading the input stream
     *  @Throws IOException : If there is an error reading the input stream
     * */
    public void runServer() throws java.net.UnknownHostException, java.io.IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port ...");
        int count = 0;
        while (true) {
            Socket socket = serverSocket.accept();
            appendOneTimeLog("Server will accept new connections from now on Port: " + port, count++);
            InputStream stream = socket.getInputStream();

            String request = readInputFromClient(stream);
            boolean isHttpRequest = isHttpRequest(request);

            OutputStream oStream = socket.getOutputStream();
        }
    }

    /*
     * This method will load the server.properties file from the resources folder
     * @Return Properties : The properties object loaded from the server.properties file
     * @Throws IOException : If there is an error reading the server.properties file
     * @Throws IOException : If there is an error reading the server.properties file
     * @Throws IOException : If there is an error reading the server.properties file
     * */
    private Properties loadServerProperties() throws IOException {
        Properties response = new Properties();
        String pathStr = TCPServer.class.getClassLoader().getResource("server.properties").getPath().replaceFirst("/", "");
        Path path = Paths.get(pathStr);
        boolean isServerProExists = Files.exists(path);
        if (!isServerProExists) {
            System.out.println("Server Properties not exist ....");
            System.err.println("Exiting the System....");
            System.exit(1);
        }
        response.load(new FileReader(path.toFile()));
        return response;
    }

    /*
     * This method will print the log message only once
     * @Param log : The log message to be printed
     * @Param count : The count to check if the log should be printed or not
     * */
    private void appendOneTimeLog(String log, int count) {
        if (count == 1) {
            System.out.println(log);
        }
    }

    /*
     * Method to read the input from the client
     * @Param stream : The input stream from the client
     * @Return String : The input from the client as a String
     * @Throws IOException : If there is an error reading the input stream
     * */
    public String readInputFromClient(InputStream stream) throws IOException {
        Assert.assertNotNull(stream);

        BufferedReader buffReader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer response = new StringBuffer(); //Thread safety
        String line = null;
        while ((line = (buffReader.readLine())) != null) {
            response.append(line);
            response.append("\n");
        }
        return response.toString();
    }


    void respondToClient() {

    }

    /*
     * Response looks like the below example :
     * "HTTP/1.1 200 OK\r\n" +
     * "Content-Type: text/plain\r\n" +
     * "Content-Length: 28\r\n" +
     * "\r\n" +
     * "Hello, this is a simple HTTP server!";
     * */
    private String getResponseToHttpClient(String contentType, String httpStatusCode, String payload) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("HTTP/1.1  ");
        buffer.append(httpStatusCode);
        buffer.append(NEW_LINE);
        buffer.append(CONTENT_TYPE + contentType);
        buffer.append(NEW_LINE);
        buffer.append(CONTENT_LENGTH + payload.length());
        buffer.append(NEW_LINE);
        buffer.append(payload);
        return buffer.toString();
    }

    /*
     * This method check is the current request is HTTP request by checking the first line of the request
     * against the 8 HTTP Methods like GET , POST & so on .
     * This method will be used to identify if the server should respond to the client in HTTP format or not.
     *
     * @Param firstLine : The first line of the request
     * @Return boolean : true if the request is HTTP request else false
     * */
    private boolean isHttpRequest(String firstLine) {
        //Check if the first line starts with a valid HTTP method and contains HTTP version
        return firstLine.startsWith("GET") || firstLine.startsWith("POST") || firstLine.startsWith("PUT") || firstLine.startsWith("DELETE") || firstLine.startsWith("HEAD") || firstLine.startsWith("OPTIONS") || firstLine.startsWith("PATCH") || firstLine.startsWith("CONNECT") || firstLine.startsWith("TRACE");
    }


    public static void main(String[] args) throws Exception {
        start();
    }


}
