package home.learning.server;

public class ServerConstants {

    public static final String HTTP_METHOD_ONLY_SUPPORTED = "{'error':true,'message':'Only HTTP Protocol is supported'}";
    public static final String HTTP_NO_MAPPING_FOUND = "{'error':true,'message':'No mapping found for request Path'}";

    public static final String NEW_LINE = "\r\n";
    public static final String CONTENT_TYPE = "Content-Type:";
    public static final String CONTENT_LENGTH = "Content-Length:";

    public static final String BAD_REQUEST = "HTTP/1.1 400 Bad Request";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String REQUEST_OK = "HTTP/1.1 200 OK";
}
