/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * An abstract method used to represent house hold items that fall under the
 * category of the Internet of Things. Currently stores the location and id of
 * the device. This class is extended by Appliance and Sensor.
 * 
 * @author anna
 *
 */
public abstract class IOTDevice {
	private String id;
	private Room location;

	public IOTDevice(String id, Room location) {
		this.id = id;
		this.location = location;
	}

	public String getId() {
		return id;
	}

	public Room getLocation() {
		return location;
	}

}
