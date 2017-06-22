package cscie97.asn4.housemate.entitlement;

/**
 * A globally unique identifier with a one time use. Enables the user to pass in
 * commands and queries if they are logged into the system. Has a limited
 * lifespan if user remains inactive.
 */
public class AccessToken {

	private String guid;
	private String timeout; // recorded in min

	public AccessToken(String guid) {
		this.guid = guid;
		this.timeout = "20";
	}

	public String getGuid() {
		return this.guid;
	}

	public String getTimeout() {
		return this.timeout;
	}

}