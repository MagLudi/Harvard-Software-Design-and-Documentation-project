/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class Refrigerator extends Appliance {
	private double temperature; // measured in fahrenheit
	private int beerCount;
	private boolean cleaning;

	public Refrigerator(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.temperature = 50;
		this.beerCount = 0;
		this.cleaning = false;
	}

	public double getTemperature() {
		return temperature;
	}

	public void changeTemperature(double temperature) {
		this.temperature = temperature;
	}

	public boolean isCleaning() {
		return cleaning;
	}

	public void setCleaning(boolean cleaning) {
		this.cleaning = cleaning;
	}

	public int getBeerCount() {
		return beerCount;
	}

	public void addBeer(int beer) {
		beerCount = beer;
		
		if(beerCount < 0){
			beerCount = 0;
		}
	}

}
