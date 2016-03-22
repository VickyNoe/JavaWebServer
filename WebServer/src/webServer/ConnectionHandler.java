package webServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/* 
 * ConnectionHandler handles connection requests.
 * The requests are parsed to obtain the command and directory or file name.
 * If a directory, the contents of the directory returned, if a file the contents
 * or the file are returned.
 */
public class ConnectionHandler {

	private static final String FORWARD_SLASH = "/";
	private static final String GET = "GET";
	File directory;
	Socket connection;
	PrintWriter outPrintWriter;

	public ConnectionHandler(File directory, Socket connection) {
		this.directory = directory;
		this.connection = connection;
	}

	/**
	 * Handles GET requests from the client. All other types of requests are
	 * ignored. The input is parsed, and if a valid, passed to
	 * the appropriate handling method.
	 */
	public void handleRequest() {
		String request = "Request not read";
		try {
			StringTokenizer tokenizedLine = parseIncomingRequest();
			request = tokenizedLine.nextToken();
			if (request.equals(GET)) {
				String fileName = parseFileName(tokenizedLine);
				File requestedFile = new File(directory, fileName);
				if (requestedFile.isDirectory()) {
					handleDirectory(requestedFile);
				} else {
					handleFile(requestedFile);
				}
			} else {
				System.out.println("Invalid request received.  Only GET requests are handled");
			}
			System.out.println("OK    " + connection.getInetAddress() + " " + request);

		} catch (Exception e) {
			System.out.println("Error handling request" + connection.getInetAddress() + " " + request + " " + e);
		} finally {
			try {
				connection.close();
			} catch (IOException e) {
				System.out.println("Error closing connection" + connection.getInetAddress() + " " + request + " " + e);
			}
		}

	}


	/**
	 * Parses the file name form the incoming tokenizedLine
	 * 
	 * @param tokenizedLine - the line to be parsed to obtain the fileName
	 * @return the fileName from the incoming request
	 */
	private String parseFileName(StringTokenizer tokenizedLine) {
		String fileName = tokenizedLine.nextToken();
		if (fileName.startsWith(FORWARD_SLASH))
			fileName = fileName.substring(1);
		return fileName;
	}

	/**
	 * Parse the incoming request into a tokenized line
	 * 
	 * @return StringTokenizer - tokens from the incoming request
	 * @throws IOException
	 */
	private StringTokenizer parseIncomingRequest() throws IOException {
		InputStreamReader inputReader = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(inputReader);
		String requestMessageLine = reader.readLine();
		// Log the incoming request
		System.out.println(requestMessageLine);

		StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
		return tokenizedLine;
	}


	/**
	 * Handles a request for a file. If the file doesn't exist, send the message
	 * "404 Not Found". Otherwise, send the message "200 OK" followed by the file
	 * contents
	 * 
	 * @param file - the requested file
	 * @throws Exception
	 */
	private void handleFile(File file) throws Exception {
		DataOutputStream outputWriter = new DataOutputStream(connection.getOutputStream());
		if ((!file.exists()) || file.isDirectory()) {
			writeFileNotFoundResponseHeader(outputWriter);
		} else {
			int numOfBytes = (int) file.length();
			byte[] fileInBytes = readFile(file, numOfBytes);
			writeFileResponseHeader(file, outputWriter, numOfBytes);
			writeFile(outputWriter, numOfBytes, fileInBytes);
		}
		outputWriter.flush();
		outputWriter.close();
	}


	/**
	 * Write a file not found response header to the output stream
	 * 
	 * @param outputWriter
	 * @throws IOException
	 */
	private void writeFileNotFoundResponseHeader(DataOutputStream outputWriter) throws IOException {
		outputWriter.writeBytes("HTTP/1.1 404 \r\n");
	}


	/**
	 * Write the file to the output stream
	 * 
	 * @param outputWriter
	 * @param numOfBytes
	 * @param fileInBytes
	 * @throws IOException
	 */
	private void writeFile(DataOutputStream outputWriter, int numOfBytes, byte[] fileInBytes) throws IOException {
		outputWriter.write(fileInBytes, 0, numOfBytes);
	}

	/**
	 * Write the response header when handling a request for a file
	 * 
	 * @param file
	 * @param outputWriter
	 * @param numOfBytes
	 * @throws IOException
	 */
	private void writeFileResponseHeader(File file, DataOutputStream outputWriter, int numOfBytes) throws IOException {
		outputWriter.writeBytes("HTTP/1.1 200 OK \r\n");
		if (file.getName().endsWith(".jpg"))
			outputWriter.writeBytes("Content-Type:image/jpeg\r\n");
		if (file.getName().endsWith(".gif"))
			outputWriter.writeBytes("Content-Type:image/gif\r\n");
		outputWriter.writeBytes("Content-Length: " + numOfBytes + "\r\n");
		outputWriter.writeBytes("\r\n");
	}


	/**
	 * Read in the specified file
	 * 
	 * @param file - the specified file
	 * @param numOfBytes - number of bytes in the file
	 * @return byte[]
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private byte[] readFile(File file, int numOfBytes) throws FileNotFoundException, IOException {
		FileInputStream inFile = new FileInputStream(file);
		byte[] fileInBytes = new byte[numOfBytes];
		inFile.read(fileInBytes);
		return fileInBytes;
	}

	/**
	 * Handle a request to see the contents of the given directory
	 * 
	 * @param requestedDirectory - the requested directory
	 * @throws Exception
	 */
	private void handleDirectory(File requestedDirectory) throws Exception {
		System.out.println("Returning the contents of the directory");
		outPrintWriter = new PrintWriter(connection.getOutputStream());
		PageWriter pageWriter = new PageWriter(outPrintWriter);
		writeDirectoryResponseHeader();
		writeDirectoryPage(requestedDirectory, pageWriter);
		outPrintWriter.flush();
		outPrintWriter.close();
		if (outPrintWriter.checkError())
			throw new Exception("Error while transmitting data.");
	}

	/**
	 * Write the contents of html page to display the contents of the requested directory
	 * 
	 * @param requestedDirectory - the requested directory
	 * @param pageWriter - - the pageWriter
	 */
	private void writeDirectoryPage(File requestedDirectory, PageWriter pageWriter) {
		pageWriter.writePageContent(requestedDirectory, directory);
	}


	/**
	 * Write the response header when serving a directory
	 */
	private void writeDirectoryResponseHeader() {
		outPrintWriter.println("HTTP/1.1 200 OK");
		outPrintWriter.println("Content-Type: text/html");
		// this blank line signals the end of the headers
		outPrintWriter.println("");
	}

}
