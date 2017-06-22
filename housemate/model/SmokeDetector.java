/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class SmokeDetector extends Sensor{
	
	private boolean fire;

	public SmokeDetector(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		
		this.fire = false;
	}

	public boolean isFire() {
		return fire;
	}
	
	/**
	 * @param fire
	 */
	public void detectedFire(boolean f){
		fire = f;
	}

}
