/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Thermostat extends Appliance {
	private double temperature; // measured in fahrenheit

	public Thermostat(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.temperature = 70;
	}

	public double getTemperature() {
		return temperature;
	}

	public void changeTemperature(double temperature) {
		this.temperature = temperature;
	}
	
	

}
