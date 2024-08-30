package home.learning.server.test;

import home.learning.server.Server;
import home.learning.server.ServerConstants;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class ServerTest {

    public static final String RAW_HTTP_REQUEST = "GET /person HTTP/1.1" +
            "Host: localhost:7000" +
            "Connection: keep-alive" +
            "Accept: application/json" +
            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36" +
            "Accept-Encoding: gzip, deflate, br" +
            "Accept-Language: en-US,en;q=0.9";

    public static final String RAW_HTTP_ERROR_PATH_REQ = "GET /NO_PATH HTTP/1.1" +
            "Accept-Encoding: gzip, deflate, br" +
            "Accept-Language: en-US,en;q=0.9";

    public static final String RAW_HTTP_RESPONSE = "HTTP/1.1 200 OK" + ServerConstants.NEW_LINE +
            "Content-Type: application/json" + ServerConstants.NEW_LINE +
            "Content-Length: 82" + ServerConstants.NEW_LINE +
            "{\"name\":\"vignesh\",\"age\": 34,\"married\": true,\"country\": \"india\"}";


    public static final String RAW_HTTP_ERROR_RESPONSE = "HTTP/1.1 400 Bad Request" + ServerConstants.NEW_LINE +
            "Content-Type: application/json" + ServerConstants.NEW_LINE +
            "Content-Length: 58" + ServerConstants.NEW_LINE + ServerConstants.NEW_LINE +
            "{\"error\":true,\"message\":\"Only HTTP Protocol is supported\"}";

    public static final String RAW_HTTP_NO_MAPPING_FOUND_ERROR_RESPONSE = "HTTP/1.1 400 Bad Request" + ServerConstants.NEW_LINE +
            "Content-Type: application/json" + ServerConstants.NEW_LINE +
            "Content-Length: 60" + ServerConstants.NEW_LINE + ServerConstants.NEW_LINE +
            "{\"error\":true,\"message\":\"No mapping found for request Path\"}";

    private static final String RAW_PERSON_DATA = "{\"name\": \"vignesh\",\"age\": 34,\"married\": true,\"country\": \"india\"}";

    @Test
    public void testReturnErrorResponseToClient() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Server server = new Server();
        Method errorResponseMethod = Server.class
                .getDeclaredMethod("returnErrorResponseToClient", OutputStream.class);
        errorResponseMethod.setAccessible(true);
        errorResponseMethod.invoke(server, outputStream);

        String response = outputStream.toString();
        Assert.assertNotNull(response);
        Assert.assertEquals(response, RAW_HTTP_ERROR_RESPONSE);

    }

    @Test
    public void testLoadServerProperties() throws Exception {
        Server server = new Server();
        Method loadServerPropertiesMethod = Server.class.getDeclaredMethod("loadServerProperties");
        loadServerPropertiesMethod.setAccessible(true);
        loadServerPropertiesMethod.invoke(server);
        Assert.assertNotNull(server.getServerProperties());
        Assert.assertTrue(server.getServerProperties().size() != 0);
    }

    @Test
    public void testServerProperties() {
        Server server = new Server();
        Assert.assertTrue(server.getServerProperties().size() != 0);
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
        Assert.assertNotNull(response);
        String excepted = "Hello ! Vignesh , This is a test input from client".trim();
        String actual = response.trim();
        Assert.assertEquals(excepted, actual);
    }

    @Test
    public void testReadInputFromClientNegate() throws Exception {
        Server server = new Server();
        Assert.assertThrows(AssertionError.class, () -> server.readInputFromClient(null));
    }

    @Test
    public void testRespondHttpDataToClient() throws Exception {
        Server server = new Server();
        Method respondHttpDataToClientMethod = Server.class.getDeclaredMethod("respondHttpDataToClient", String.class, OutputStream.class);
        respondHttpDataToClientMethod.setAccessible(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        respondHttpDataToClientMethod.invoke(server, RAW_HTTP_REQUEST, outputStream);
        String response = outputStream.toString();
        Assert.assertNotNull(response);
        String excepted = RAW_HTTP_RESPONSE.replaceAll("\r\n", "").replaceAll(" ", "");
        String actual = response.replaceAll("\r\n", "").replaceAll(" ", "");
        Assert.assertEquals(excepted, actual);
    }


    @Test
    public void testRespondNegateHttpDataToClient() throws Exception {
        Server server = new Server();
        Method respondHttpDataToClientMethod = Server.class.getDeclaredMethod("respondHttpDataToClient", String.class, OutputStream.class);
        respondHttpDataToClientMethod.setAccessible(true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        respondHttpDataToClientMethod.invoke(server, RAW_HTTP_ERROR_PATH_REQ, outputStream);
        String response = outputStream.toString();
        Assert.assertNotNull(response);
        String excepted = RAW_HTTP_NO_MAPPING_FOUND_ERROR_RESPONSE.replaceAll("\r\n", "").replaceAll(" ", "");
        String actual = response.replaceAll("\r\n", "").replaceAll(" ", "");
        Assert.assertEquals(excepted, actual);
    }

    @Test
    public void testReadJsonFile() throws Exception {
        Server server = new Server();
        Method readJsonFileMethod = Server.class.getDeclaredMethod("readJsonFile", String.class);
        readJsonFileMethod.setAccessible(true);
        String response = (String) readJsonFileMethod.invoke(server, "person");
        Assert.assertNotNull(response);
        String excepted = RAW_PERSON_DATA.replaceAll("\r\n", "").replaceAll(" ", "");
        String actual = response.replaceAll("\r\n", "").replaceAll(" ", "");
        Assert.assertEquals(excepted, actual);
    }

    @Test
    public void testNegateReadJsonFile() throws Exception {
        Server server = new Server();
        Method readJsonFileMethod = Server.class.getDeclaredMethod("readJsonFile", String.class);
        readJsonFileMethod.setAccessible(true);
        String response = (String) readJsonFileMethod.invoke(server, "NO_PATH");
        Assert.assertNull(response);
    }

    @Test
    public void testGetJsonFileDirectory() throws Exception {
        Server server = new Server();
        Method getJsonFileDirectoryMethod = Server.class.getDeclaredMethod("getJsonFileDirectory");
        getJsonFileDirectoryMethod.setAccessible(true);
        String mappingPath = (String) getJsonFileDirectoryMethod.invoke(server);
        Assert.assertNotNull(mappingPath);
    }

    @Test
    public void testNegateGetJsonFileDirectory() throws Exception {
        Server server = new Server();
        Properties serverProperties = server.getServerProperties();
        serverProperties.remove("json.file.location");
        Method getJsonFileDirectoryMethod = Server.class.getDeclaredMethod("getJsonFileDirectory");
        getJsonFileDirectoryMethod.setAccessible(true);
        String mappingPath = (String) getJsonFileDirectoryMethod.invoke(server);
        Assert.assertNotNull(mappingPath);
    }

    @Test
    public void testNegate2GetJsonFileDirectory() throws Exception {
        Server server = new Server();
        Properties serverProperties = server.getServerProperties();
        serverProperties.put("json.file.location","./JSON_Files");
        Method getJsonFileDirectoryMethod = Server.class.getDeclaredMethod("getJsonFileDirectory");
        getJsonFileDirectoryMethod.setAccessible(true);
        String mappingPath = (String) getJsonFileDirectoryMethod.invoke(server);
        Assert.assertTrue(mappingPath.contains("JSON_Files"));
    }

    @Test
    public void testGetJsonFileDirectoryForCurrentDir() throws Exception {
        Server server = new Server();
        Properties serverProperties = server.getServerProperties();
        serverProperties.put("json.file.location","./json_folder");
        Method getJsonFileDirectoryMethod = Server.class.getDeclaredMethod("getJsonFileDirectory");
        getJsonFileDirectoryMethod.setAccessible(true);
        String mappingPath = (String) getJsonFileDirectoryMethod.invoke(server);
        Assert.assertNull(mappingPath);
    }

    @Test
   public void testGetJsonFileDirectoryForAbsolutePath() throws Exception {
        Server server = new Server();
        Properties serverProperties = server.getServerProperties();
        serverProperties.put("json.file.location", "D:/test");
        Method getJsonFileDirectoryMethod = Server.class.getDeclaredMethod("getJsonFileDirectory");
        getJsonFileDirectoryMethod.setAccessible(true);
        String mappingPath = (String) getJsonFileDirectoryMethod.invoke(server);
        Assert.assertEquals("D:/test",mappingPath);
    }

}