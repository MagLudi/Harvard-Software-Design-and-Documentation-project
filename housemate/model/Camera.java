/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Camera extends Sensor {
	private boolean detectedOcc;
	private boolean occLeaving;

	public Camera(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.detectedOcc = false;
		this.occLeaving = false;
	}

	public boolean isDetectedOcc() {
		return detectedOcc;
	}

	public void setDetectedOcc(boolean detectedOcc) {
		this.detectedOcc = detectedOcc;
	}

	public boolean isOccLeaving() {
		return occLeaving;
	}

	public void setOccLeaving(boolean occLeaving) {
		this.occLeaving = occLeaving;
	}
	
}
