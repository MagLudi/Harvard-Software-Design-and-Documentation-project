/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;

/**
 * @author anna
 *
 */
public class Room {
	public enum RoomType {
		KITCHEN, CLOSET, DINNING_ROOM, LIVING_ROOM, HALLWAY, BED_ROOM, FAMILY_ROOM, GARAGE, BATHROOM
	}

	private String name;
	private int floor;
	private RoomType type;
	private Home location;
	private ArrayList<IOTDevice> iotDevices;

	public Room(String name, int floor, RoomType type, Home location) {
		this.name = name;
		this.floor = floor;
		this.type = type;
		this.location = location;
		this.iotDevices = new ArrayList<IOTDevice>();
	}

	public String getName() {
		return name;
	}

	public int getFloor() {
		return floor;
	}

	public RoomType getType() {
		return type;
	}

	public Home getLocation() {
		return location;
	}

	public void addIotDevice(IOTDevice iotd) {
		iotDevices.add(iotd);
	}

	public ArrayList<IOTDevice> getIotDevices() {
		return iotDevices;
	}

	public void removeIotDevice(IOTDevice iotd) {
		iotDevices.remove(iotd);
	}
}
