/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Ava extends Sensor{
	public enum QueryType {
		COMMAND, QUETSION
	}
	
	public Ava(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
	}
	
}
