package home.learning.server.test;

import home.learning.server.TCPServer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;


public class TCPServerTest {

    @Test
    public void testServerProperties() {
        TCPServer server = new TCPServer();
        Assert.assertTrue(server.serverProperties.size() != 0);
    }


    @Test
    public void testGetResponseToHttpClient() throws Exception {
        TCPServer server = new TCPServer();
        Method getResponseMethod = TCPServer.class.getDeclaredMethod("getResponseToHttpClient", String.class, String.class, String.class);
        getResponseMethod.setAccessible(true);
        String response = (String) getResponseMethod.invoke(server, "application/json", "200 OK", "{\"Name\":\"Vignesh\"}");
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("Vignesh"));
    }

    @Test
    public void testReadInputFromClient() throws Exception {
        TCPServer server = new TCPServer();
        String testInput = "Hello ! Vignesh , This is a test input from client";
        InputStream stream = new ByteArrayInputStream(testInput.getBytes(StandardCharsets.UTF_8));
        String response = server.readInputFromClient(stream);
        System.out.println(response);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.contains("This is a test input from client"));
    }

   

}