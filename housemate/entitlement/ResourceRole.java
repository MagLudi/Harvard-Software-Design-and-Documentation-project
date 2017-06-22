package cscie97.asn4.housemate.entitlement;

/**
 * Represents the user relation to a particular resource (e.g. child guest vs
 * child resident)
 */
public class ResourceRole {

	private Resource resource;
	private String type;

	public ResourceRole(String type) {
		this.type = type;

		String[] parts = type.split("_");
		this.resource = new Resource(parts[0].trim());
	}

	public String getType() {
		return this.type;
	}

	public Resource getResource() {
		return this.resource;
	}

}