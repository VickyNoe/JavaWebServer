package webServer;

import java.io.File;
import java.io.PrintWriter;

public class PageWriter {

	PrintWriter outPrintWriter;

	PageWriter(PrintWriter printWriter) {
		outPrintWriter = printWriter;
	}
		
	/**
	 * Write the html page, constructing the links to all the files and subdirectories
	 * 
	 * @param requestedDirectory - directory containing the links
	 * @param homeDirectory
	 */
	public void writePageContent(File requestedDirectory, File homeDirectory) {
		outPrintWriter.println("<html>");
		outPrintWriter.println("<head>");
		outPrintWriter.println("<title>Index of /</title> ");
		outPrintWriter.println("</head>");
		outPrintWriter.println("<body>");
		outPrintWriter.println("<h1>Index of " + requestedDirectory.getName() + " </h1>");
		outPrintWriter.println("<pre><h2>Name</h2><hr>");
		if (!requestedDirectory.getName().equals(homeDirectory.getName())) {
			outPrintWriter.println("<a href=\"/\"> Parent Directory </a>");
		}
		String[] fileList = requestedDirectory.list();
		for (int i = 0; i < fileList.length; i++) {
			File file = new File(fileList[i]);
			if (!requestedDirectory.getName().equals(homeDirectory.getName())) {
				outPrintWriter.println("<a href=\"" + requestedDirectory.getName() + System.getProperty("file.separator") + fileList[i] + "\">"
						+ fileList[i] + "</a><br>");
			} else {
				outPrintWriter.println("<a href=\"" + fileList[i] + "\">" + fileList[i] + "</a><br>");
			}
			System.out.println(fileList[i] + file.lastModified() + file.getTotalSpace());
		}
		outPrintWriter.println("<hr></pre>");
		outPrintWriter.println("</body></html>");
	}

}
