SimpleWebServer.java is a simple webserver.
  
In order to compile SimpleWebServer.java, change directory to the directory containing SimpleWebServer and all of the other java files and run:
javac *.java

To run SimpleWebServer.java run:
java webServer.SimpleWebServer <directory>
where directory is the directory that you wish to serve files from.

N.B you may need to run java with the classpath option
java -cp .  webServer.SimpleWebServer <directory>
To kill the server, type Ctrl-C.

When your server is successfully running you should see:
Listening on port 80

The server returns the files and subdirectories from this directory on a initial request.
The user can then select any of the files or subdirectories listed. When a file is selected, 
it returns the file contents, when a sudirectory is selected it returns and lists files and
subdirectories.

Client requests are logged to standard output.

Webserver accepts HTTP GET requests that begin with:

  GET /filename

If the file exists, it responds with:

  HTTP/1.1 200 OK\r\n
  Content-Type: text/html\r\n
  \r\n
  (contents of filename)

where \r\n is a carriage return and line feed. 

If the file does not exist or the request is not understood, the server
sends an HTTP "404 Not Found" error message back to the client.
