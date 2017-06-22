/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Door extends Appliance{
	public enum Status {
		OPEN, CLOSED, LOCKED
	}
	
	private Status status;
	
	public Door(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.status = Status.CLOSED;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
