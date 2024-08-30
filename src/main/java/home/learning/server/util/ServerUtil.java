package home.learning.server.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerUtil {

    public ServerSocket getHandledServerSocket(int port){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
        }catch (Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
        }
        return serverSocket;
    }


    public Socket getSocket(ServerSocket serverSocket){
        Socket clientSocket = null;
        try{
            clientSocket = serverSocket.accept();
        }catch (Exception e){
            e.printStackTrace(System.err);
            System.exit(1);
        }
        return clientSocket;
    }

    public void safeCloseSocket(Socket serverSocket){
        try{
             serverSocket.close();
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
    }

    public InputStream getInputStream(Socket socket){
        InputStream stream = null;
        try{
            stream = socket.getInputStream();
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
        return stream;
    }

    public OutputStream getOutputStream(Socket socket){
        OutputStream stream = null;
        try{
            stream = socket.getOutputStream();
        }catch (Exception e){
            e.printStackTrace(System.err);
        }
        return stream;
    }

}
