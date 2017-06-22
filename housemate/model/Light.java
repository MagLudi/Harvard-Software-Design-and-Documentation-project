/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Light extends Appliance{
	private int intensity;

	public Light(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.intensity = 0;
	}

	public int getIntensity() {
		return intensity;
	}

	public void changeIntensity(int intensity) {
		this.intensity = intensity;
		
		if(this.intensity > 100){
			this.intensity  = 100;
		} else if(this.intensity < 0){
			this.intensity  = 0;
		}
	}
	
	public void setPower(boolean on){
		if(on && (intensity == 0)){
			intensity = 50;
		} else if(!on){
			intensity = 0;
		}
	}

}
