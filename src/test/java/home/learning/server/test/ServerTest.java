package home.learning.server.test;

import home.learning.server.Server;
import home.learning.server.ServerConstants;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;


public class ServerTest {

    public static final String RAW_HTTP_REQUEST = "GET /person HTTP/1.1" +
                                            "Host: localhost:8080" +
                                            "Connection: keep-alive" +
                                            "Accept: application/json" +
                                            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36" +
                                            "Accept-Encoding: gzip, deflate, br" +
                                            "Accept-Language: en-US,en;q=0.9";



    public void testReturnErrorResponseToClient() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Server server = new Server();
        Method errorResponseMethod = Server.class
                .getDeclaredMethod("returnErrorResponseToClient", OutputStream.class);
        errorResponseMethod.setAccessible(true);
        errorResponseMethod.invoke(server,outputStream);

        String response =outputStream.toString();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains(ServerConstants.HTTP_METHOD_ONLY_SUPPORTED));

    }


    @Test
    public void testServerProperties() {
        Server server = new Server();
        Assert.assertTrue(server.serverProperties.size() != 0);
    }


    @Test
    public void testGetResponseToHttpClient() throws Exception {
        Server server = new Server();
        Method getResponseMethod = Server.class.getDeclaredMethod("getResponseToHttpClient", String.class, String.class, String.class);
        getResponseMethod.setAccessible(true);
        String response = (String) getResponseMethod.invoke(server, "application/json", "200 OK", "{\"Name\":\"Vignesh\"}");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("Vignesh"));
    }

    @Test
    public void testReadInputFromClient() throws Exception {
        Server server = new Server();
        String testInput = "Hello ! Vignesh , This is a test input from client";
        InputStream stream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
        String response = server.readInputFromClient(stream);
        System.out.println(response);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("This is a test input from client"));
    }


    @Test
    public  void testRespondHttpDataToClient() throws Exception {
        Server server = new Server();
        Method respondHttpDataToClientMethod = Server.class.getDeclaredMethod("respondHttpDataToClient", String.class, OutputStream.class);
        respondHttpDataToClientMethod.setAccessible(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        respondHttpDataToClientMethod.invoke(server, RAW_HTTP_REQUEST, outputStream);
        String response =outputStream.toString();
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains(ServerConstants.REQUEST_OK));
    }


}