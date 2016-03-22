package webServer;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/* 
 * SimpleWebServer.java is a simple webserver.
 * 
 * To run SimpleWebServer.java run:
 * java SimpleWebServer <directory>
 * where directory is the directory that you wish to serve files from.
 * 
 * The server runs until the program is terminated, for example by a CONTROL-C.
 * The server responds to GET requests on a given port number and return the contents of the directory it is running in
 * The server returns the files and subdirectories from the input directory on a initial request.
 * The user can then select any of the files or subdirectories listed. When a file is selected, 
 * it returns the file contents, when a sudirectory is selected it returns and lists files and
 * subdirectories.
 * It does not support CGI, cookies, authentication, keepalive, etc.
 * 
 * Client requests are logged to standard output.
 * 
 */
public class SimpleWebServer {

	static final int LISTENING_PORT = 80;

	public static void main(String[] args) {
        
        File directory;        // The initial directory from which the webserver will serve requests
        ServerSocket listener; // Listens for connection requests.
        Socket connection;     // A socket for communicating with a client
  
        if (args.length == 0) {
           System.out.println("Usage:  java SimpleWebServer <directory>");
           return;
        }
        
        directory = new File(args[0]);
        if ( ! directory.exists() ) {
           System.out.println("Specified directory does not exist.");
           return;
        }
        if (! directory.isDirectory() ) {
           System.out.println("The specified file is not a directory.");
           return;
        }
        
        /* Listen for connection requests from clients.  For each connection, create a
          connectionHandler to handle it. The server runs until the
           program is terminated, for example by a CONTROL-C. */             
        try {
           listener = new ServerSocket(LISTENING_PORT);
           System.out.println("Listening on port " + LISTENING_PORT);
           while (true) {
              connection = listener.accept();
              ConnectionHandler connectionHandler = new ConnectionHandler(directory,connection);
              connectionHandler.handleRequest();
              
           }
        }
        catch (Exception e) {
           System.out.println("Server shut down unexpectedly.");
           System.out.println("Error:  " + e);
           return;
        }
        
     }
}
