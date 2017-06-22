/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * An abstract class that extends IOTDevice. The subclasses for Appliance are
 * the following: Pandora, Light, Window, TV, Door, Thermostat, Refrigerator,
 * and Oven. These classes can be interacted with as they have setters
 * available.
 * 
 * @author anna
 *
 */
public abstract class Appliance extends IOTDevice {

	public Appliance(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
	}

}
