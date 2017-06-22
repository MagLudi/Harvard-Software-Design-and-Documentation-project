/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * An abstract class that extends IOTDevice. This class is extended by Camera,
 * SmokeDetector, and Ava. Beyond initializing and removing instances of these
 * classes, there is currently no means of interacting with them.
 * 
 * @author anna
 *
 */
public abstract class Sensor extends IOTDevice {

	public Sensor(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
	}

}
