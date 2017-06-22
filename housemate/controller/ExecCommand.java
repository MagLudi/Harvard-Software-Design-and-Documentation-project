package cscie97.asn4.housemate.controller;

import java.sql.Timestamp;

import cscie97.asn4.housemate.entitlement.AccessToken;
import cscie97.asn4.housemate.test.TestDriver;

/**
 * Contains the information required to execute the command (the target device,
 * the status type, the new status, and when it is to be executed). It also
 * contains the entire command as a string in the format required so that the
 * Model can update. Use of that element is optional and was added due to the
 * nature of the project.
 */
public class ExecCommand {

	private String targetDevice;
	private String statusType;
	private String newStatus;
	private String executionTime;
	private String command;
	private AccessToken authToken;

	/**
	 * 
	 * @param targetDevice
	 * @param statusType
	 * @param newStatus
	 * @param executionTime
	 * @param command
	 */
	public ExecCommand(String targetDevice, String statusType,
			String newStatus, String executionTime, String command,
			AccessToken authToken) {
		this.targetDevice = targetDevice;
		this.statusType = statusType;
		this.newStatus = newStatus;
		this.executionTime = executionTime;
		this.command = command;
		this.authToken = authToken;

		log(" New command: " + command);
	}

	public String getTargetDevice() {
		return this.targetDevice;
	}

	public String getStatusType() {
		return this.statusType;
	}

	public String getNewStatus() {
		return this.newStatus;
	}

	public String getExecutionTime() {
		return this.executionTime;
	}

	public String getCommand() {
		return this.command;
	}

	public AccessToken getAuthToken() {
		return authToken;
	}

	/**
	 * Have the command executed when the conditions for the executionTime (if
	 * any) is completed.
	 * 
	 * @throws Exception
	 */
	public void executeCommand() throws Exception {
		TestDriver.newCommand(authToken, command);
	}

	/**
	 * Prints out a message that a new command has been created and provides the
	 * time stamp
	 * 
	 * @param message
	 */
	private void log(String message) {
		java.util.Date date = new java.util.Date();
		String msg = new Timestamp(date.getTime()) + message;
		System.out.println(msg);
		FileLogger.getLogger().logMsg(msg);
	}

}