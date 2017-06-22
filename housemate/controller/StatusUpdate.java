package cscie97.asn4.housemate.controller;

/**
 * Contains the information (device type, address, device name, stimulus) that
 * explains the status change.
 */
public class StatusUpdate {

	private String deviceType;
	private String address;
	private String deviceID;
	private String stimulus;
	private String val;

	/**
	 * 
	 * @param deviceType
	 * @param address
	 * @param deviceID
	 * @param stimulus
	 * @param val
	 */
	public StatusUpdate(String deviceType, String address, String deviceID,
			String stimulus, String val) {
		this.deviceType = deviceType;
		this.address = address;
		this.deviceID = deviceID;
		this.stimulus = stimulus;
		this.val = val;
	}

	public String getDeviceType() {
		return this.deviceType;
	}

	public String getAddress() {
		return this.address;
	}

	public String getDeviceID() {
		return this.deviceID;
	}

	public String getStimulus() {
		return this.stimulus;
	}

	public String getVal() {
		return val;
	}

}