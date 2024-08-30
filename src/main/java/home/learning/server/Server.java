package home.learning.server;

import home.learning.server.util.ServerUtil;
import org.junit.Assert;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Server {

    public static final int port = 7000;

    public Properties serverProperties = new Properties();
    private static final Server server = new Server();

    private static final ServerUtil serverUtil = new ServerUtil();


    /*
     * Initialize the server properties. Load the server.properties file
     * and create a properties Object will be
     * held in memory for the server.
     *
     * If loading fails , Server will not start.
     * */
    public Server() {
        try {
            serverProperties = loadServerProperties();
        } catch (IOException e) {
            appendLog("Unable to load the server properties", LogLevel.ERROR);
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
    public void runServer() {
        ServerSocket serverSocket = serverUtil.getHandledServerSocket(port);
        appendLog("Server is listening on port ...",LogLevel.INFO);
        try {
            int count = 0;
            while (true) {
                Socket socket = serverUtil.getSocket(serverSocket);
                appendOneTimeLog("Server will accept new connections from now on Port: " + port, count++);

                InputStream stream = serverUtil.getInputStream(socket);

                String request = readInputFromClient(stream);
                boolean isHttpRequest = isHttpRequest(request);

                OutputStream oStream = serverUtil.getOutputStream(socket);
                if (!isHttpRequest) {
                    returnErrorResponseToClient(oStream);
                    serverUtil.safeCloseSocket(socket);
                } else {
                    appendLog("Handling HTTP Request ", LogLevel.INFO);
                    respondHttpDataToClient(request, oStream);
                    appendLog("Completed HTTP Request ", LogLevel.INFO);
                }
            }
        }catch (IOException e){
            appendLog("Input to Output Stream failed. Unable to handle the request. Internal Server Error.",LogLevel.ERROR);
            e.printStackTrace(System.err);
        }catch (Exception e){
            appendLog("Unable to handle the request. Internal Server Error.",LogLevel.ERROR);
            e.printStackTrace(System.err);

        }

    }


    /*  This method return error response to client , if the request is not a HTTP request
     *@Param oStream : The output stream to write the response to
     *@Throws IOException : If there is an error writing to the output stream
     */
    private void returnErrorResponseToClient(OutputStream oStream) throws IOException {
        PrintWriter writer = new PrintWriter(oStream, true);
        StringBuilder builder = new StringBuilder();
        builder.append(ServerConstants.BAD_REQUEST)
                .append(ServerConstants.NEW_LINE)
                .append(ServerConstants.CONTENT_TYPE_JSON)
                .append(ServerConstants.NEW_LINE)
                .append(ServerConstants.CONTENT_LENGTH)
                .append(ServerConstants.HTTP_METHOD_ONLY_SUPPORTED.length())
                .append(ServerConstants.NEW_LINE)
                .append(ServerConstants.HTTP_METHOD_ONLY_SUPPORTED);
        writer.write(builder.toString());
        writer.flush();
        writer.close();
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
        String pathStr = Server.class.getClassLoader().getResource("server.properties").getPath().replaceFirst("/", "");
        Path path = Paths.get(pathStr);
        boolean isServerProExists = Files.exists(path);
        if (!isServerProExists) {
            appendLog("Server Properties not exist ....",LogLevel.INFO);
            appendLog("Exiting the System....", LogLevel.ERROR);
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
    * This method will print the log message based on the log level
     * @Param log : The log message to be printed
     * @Param level : The log level of the message
     * @Throws IOException : If there is an error writing to the output stream
    * */
    private void appendLog(String log , LogLevel level ) {
        if(level == LogLevel.INFO) {
            System.out.println(log);
        }else if(level == LogLevel.ERROR) {
            System.err.println(log);
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

    /*
     * This method will respond the HTTP data to the client
     * @Param request : The request from the client
     * @Param oStream : The output stream to write the response to
     * @Throws IOException : If there is an error writing to the output stream
    * */
    private void respondHttpDataToClient(String request, OutputStream oStream) throws IOException{
        String[] requestPath =  request.split(ServerConstants.NEW_LINE);
        String path = requestPath[0].split(" ")[1];
        path=path.replaceFirst("/","");
        String response = readJsonFile(path);
        String httpStatus = ServerConstants.REQUEST_OK;
        if(response == null){
            response = sendBadResponse(path);
            httpStatus = ServerConstants.BAD_REQUEST;
        }
        PrintWriter writer = new PrintWriter(oStream, true);
        String responseToClient = getResponseToHttpClient(ServerConstants.CONTENT_TYPE_JSON, httpStatus,response);
        writer.write(responseToClient);
        writer.flush();
    }


    /*
    * This method will read the json file from the resources folder
     * @Param path : The path of the json file
     * @Return String : The content of the json file
     * @Throws IOException : If there is an error reading the json file
    * */
    private String readJsonFile(String path) throws IOException {
        String jsonData = null;
        String pathStr = getJsonFileDirectory();
        String fileName = serverProperties.getProperty(path);
        if(fileName != null) {
            Path pathObj = Paths.get(pathStr, fileName);
            boolean isFileExists = Files.exists(pathObj);
            if (isFileExists) {
                byte[] bytes = Files.readAllBytes(pathObj);
                jsonData = new String(bytes);
                appendLog("JSON data found and read.", LogLevel.INFO);
            }
        }
        return jsonData;
    }

    private String getJsonFileDirectory(){
        String mappingPath = serverProperties.getProperty("json.file.location");
        if(mappingPath == null){
            appendLog("JSON file location not found in server.properties. Moving back to default path", LogLevel.INFO);
            mappingPath =Server.class.getClassLoader().getResource("JSON_Files").getPath().replaceFirst("/", "");
        }else if(mappingPath.equalsIgnoreCase("./JSON_Files/")){
            appendLog("JSON file location found in server.properties but its mapped to default path", LogLevel.INFO);
            mappingPath =Server.class.getClassLoader().getResource("JSON_Files").getPath().replaceFirst("/", "");
        }else {
            if(mappingPath.startsWith("./")){
                appendLog("JSON file location found in server.properties but its mapped class file path. So trying to locate", LogLevel.INFO);
                mappingPath = mappingPath.replaceFirst("./","");
                mappingPath =Server.class.getClassLoader().getResource(mappingPath).getPath().replaceFirst("/", "");
                appendLog(" Final path of mapped location :" + mappingPath, LogLevel.INFO);
            }else {
                appendLog("JSON file location found in server.properties but noticed it with absolute path", LogLevel.INFO);
            }
        }
        return mappingPath;
    }

    /*
    * This method returns error response
    * when we cant read the JSON file for the requested Path.
    * */
    private String sendBadResponse(String path) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(ServerConstants.BAD_REQUEST);
        buffer.append(ServerConstants.NEW_LINE);
        buffer.append(ServerConstants.CONTENT_TYPE_JSON);
        buffer.append(ServerConstants.NEW_LINE);
        buffer.append(ServerConstants.CONTENT_LENGTH);
        buffer.append(ServerConstants.HTTP_NO_MAPPING_FOUND.length());
        buffer.append(ServerConstants.NEW_LINE);
        buffer.append(ServerConstants.HTTP_NO_MAPPING_FOUND);
        return buffer.toString();
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
        buffer.append(ServerConstants.NEW_LINE);
        buffer.append(ServerConstants.CONTENT_TYPE + contentType);
        buffer.append(ServerConstants.NEW_LINE);
        buffer.append(ServerConstants.CONTENT_LENGTH + payload.length());
        buffer.append(ServerConstants.NEW_LINE);
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
