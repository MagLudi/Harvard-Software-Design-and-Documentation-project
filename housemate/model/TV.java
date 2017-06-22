/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

/**
 * @author anna
 *
 */
public class TV extends Appliance {
	private String channel;
	private boolean power;
	private int volume;
	private boolean mute;

	public TV(String id, Room location) {
		super(id, location);
		// TODO Auto-generated constructor stub
		this.channel = "TV Guide";
		this.power = false;
		this.volume = 50;
		this.mute = false;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isPower() {
		return power;
	}

	public void setPower(boolean power) {
		this.power = power;
	}

	public int getVolume() {
		return volume;
	}

	public void changeVolume(int volume) {
		this.volume = volume;
		
		if(this.volume > 100){
			this.volume  = 100;
		} else if(this.volume < 0){
			this.volume  = 0;
		}
	}

	public boolean isMute() {
		return mute;
	}

	public void muter() {
		if (mute) {
			mute = false;
		} else {
			mute = true;
		}
	}
}
