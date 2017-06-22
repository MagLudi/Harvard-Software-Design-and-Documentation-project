/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Window extends Appliance{
	private boolean open;

	public Window(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.open = false;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
