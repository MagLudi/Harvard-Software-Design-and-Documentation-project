/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;

import cscie97.asn4.housemate.model.Room.RoomType;

/**
 * @author anna
 *
 */
public class Home {
	private ArrayList<Occupant> residents;
	private ArrayList<Occupant> guests;
	private ArrayList<Occupant> burglars;
	private ArrayList<Room> rooms;
	private int floors;
	private String name;
	
	public Home(String name) {
		this.name = name;
		this.floors = 1;
		this.residents = new ArrayList<Occupant>();
		this.guests = new ArrayList<Occupant>();
		this.burglars = new ArrayList<Occupant>();
		this.rooms = new ArrayList<Room>();
		
		rooms.add(new Room(name+":hallway1", 1, RoomType.HALLWAY, this));
	}
	
	public void addResident(Occupant resident){
		residents.add(resident);
	}

	public ArrayList<Occupant> getResidents() {
		return residents;
	}
	
	public void removeResident(Occupant resident){
		residents.remove(resident);
	}
	
	public void addGuest(Occupant guest){
		guests.add(guest);
	}

	public ArrayList<Occupant> getGuests() {
		return guests;
	}
	
	public void removeGuest(Occupant guest){
		guests.remove(guest);
	}
	
	public void addBurglar(Occupant burglar){
		burglars.add(burglar);
	}

	public ArrayList<Occupant> getBurglars() {
		return burglars;
	}
	
	public void removeBurglar(Occupant burglar){
		burglars.remove(burglar);
	}
	
	public void addRoom(Room r){
		rooms.add(r);
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}
	
	public void removeRoom(Room r){
		rooms.remove(r);
	}
	
	public void addFloor(){
		floors++;
	}

	public int getFloors() {
		return floors;
	}

	public String getName() {
		return name;
	}
	
	
}
