/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Occupant {
	public enum OccupantType {
		ADULT, CHILD, PET, UNKNOWN
	}

	public enum ActiveStatus {
		ACTIVE, SLEEPING
	}
	
	private String name;
	private ActiveStatus status;
	private OccupantType type;
	private Room location;
	
	public Occupant(String name, OccupantType type) {
		this.name = name;
		this.type = type;
		this.status = ActiveStatus.ACTIVE;
		this.location = null;
	}

	public ActiveStatus getStatus() {
		return status;
	}

	public void setStatus(ActiveStatus status) {
		this.status = status;
	}

	public Room getLocation() {
		return location;
	}

	public void setLocation(Room location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public OccupantType getType() {
		return type;
	}
	
}
