/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Oven extends Appliance {
	private double temperature; // measured in fahrenheit
	private boolean power;
	private double timer; // measured in minutes

	public Oven(String id, Room location) {
		super(id, location);
		this.temperature = 0;
		this.power = false;
		this.timer = 0;
	}

	public double getTemperature() {
		return temperature;
	}

	public void changeTemperature(double temperature) {
		if (power) {
			this.temperature = temperature;
		}
	}

	public boolean isPower() {
		return power;
	}

	public void setPower(boolean power) {
		this.power = power;
	}

	public double getTimer() {
		return timer;
	}

	public void setTimer(double timer) {
		if (power) {
			this.timer = timer;
		}
	}

}
