package cscie97.asn4.housemate.entitlement;

/**
 * The id of a particular resource (in this case house) and all of its sub
 * resources (the appliances)
 */
public class Resource {

	private String house;

	// private List<String> resources;

	public Resource(String house) {
		this.house = house;
	}
	
	public String getHouse() {
		return this.house;
	}

	// public List<String> getResources() {
	// return this.resources;
	// }

}