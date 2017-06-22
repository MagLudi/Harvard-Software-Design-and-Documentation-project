package cscie97.asn4.housemate.controller;

import java.io.*;

public class FileLogger {
	private static FileLogger logger;
	protected FileWriter fw;
	protected PrintWriter pw;
	protected  File logFile;

	public static FileLogger getLogger() {
		if (logger == null) {
			logger = new FileLogger(".", "HMS.log");
		}
		return logger;
	}

	/** Creates new log file */
	public FileLogger(String destDir, String logFile) {
		createOutputFile(destDir, logFile);
	}

	public void logMsg(String newMsg) {
		pw.println(newMsg);
		try {
			pw.flush();
		} catch (Exception e) {
			System.out.println("Exception flushing log file: " + e);
		}
	}

	protected void createOutputFile(String destDir, String file) {
		// Make sure the path exists
		checkPath(destDir);
		String outFile = destDir + "/" + file;
		logFile = new File(outFile);
		validate(logFile);
		try {
			fw = new FileWriter(logFile);
			pw = new PrintWriter(fw);
		} catch (Exception e) {
			System.out.println("Exception opening log file: " + e);
		}
		logMsg("Log file opened");
	}

	public void flush() {
		pw.flush();
	}

	public void close() {
		try {
			fw.close();
		} catch (Exception e) {
			System.out.println("Exception closing log file: " + e);
		}
	}

	protected void checkPath(String dirPath) {
		File testDir = new File(dirPath);
		if (!testDir.exists()) {
			testDir.mkdirs();
		}
	}

	protected void validate(File myFile) {
		if (myFile.canWrite()) {
		} else {
			try {
				myFile.createNewFile();
			} catch (Exception e) {
				System.out.println("Problem creating file for log: " + e);
			}
		}
	}

}
