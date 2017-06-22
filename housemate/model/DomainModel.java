/**
 * Created: Oct 4, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A more intricate and complex model for the House Mate System compared to the
 * KnowledeGraph. There are pointers from various instances to one another
 * symbolizing their relationship with each other. Each instance also contains a
 * unique identifier. For rooms, appliances, and sensors, part of the id is
 * formed from the location of the instance in question. For example, if
 * bedroom1 is located in house1, then its id would be house1:bedroom1.
 * 
 * @author anna
 *
 */
public class DomainModel {
	private static volatile DomainModel instance;

	private HashMap<String, Home> homes;
	private HashMap<String, Occupant> occupants;
	private HashMap<String, Room> rooms;
	private HashMap<String, IOTDevice> iot;

	private KnowledgeGraph kg;

	private DomainModel() {
		this.homes = new HashMap<String, Home>();
		this.occupants = new HashMap<String, Occupant>();
		this.rooms = new HashMap<String, Room>();
		this.iot = new HashMap<String, IOTDevice>();
		this.kg = KnowledgeGraph.getInstance();
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of HouseMateModel doesn't already exist. If
	 * it does exist it will simply return the instance, otherwise it will
	 * create one and return that.
	 * 
	 * @return
	 */
	public static synchronized DomainModel getInstance() {
		if (instance == null) {
			instance = new DomainModel();
		}

		return instance;
	}

	/**
	 * Updating the model by adding a new instance (or in the case of the add
	 * command, a new relation). The nodes in the KnowledgeGraph are updated by
	 * setting their payloads.
	 * 
	 * @param command
	 */
	public void addToModel(String command) {
		String[] parts = command.split(" ");

		/* adding new occupant/house relation */
		if (parts[0].equalsIgnoreCase("add")) {
			Home h = homes.get(parts[4].toLowerCase());
			Occupant o = occupants.get(parts[2].toLowerCase());

			if (parts.length == 7) {
				if (parts[6].equalsIgnoreCase("guest")) {
					h.addGuest(o);
				} else if (parts[6].equalsIgnoreCase("burglar")) {
					h.addBurglar(o);
				} else {
					h.addResident(o);
				}
			} else {
				h.addResident(o);
			}
		} else {
			/* creating a new instance */
			String name = parts[2];

			if ((parts[1].equalsIgnoreCase("occupant"))
					&& (!occupants.containsKey(name.toLowerCase()))) {
				/* new occupant */
				Occupant.OccupantType ot;

				if (parts[4].equalsIgnoreCase("adult")) {
					ot = Occupant.OccupantType.ADULT;
				} else if (parts[4].equalsIgnoreCase("child")) {
					ot = Occupant.OccupantType.CHILD;
				} else if (parts[4].equalsIgnoreCase("pet")) {
					ot = Occupant.OccupantType.PET;
				} else {
					ot = Occupant.OccupantType.UNKNOWN;
				}
				Occupant newOcc = new Occupant(name, ot);
				occupants.put(name, newOcc);
				kg.getNode(name).setPayload(newOcc);

			} else if ((parts[1].equalsIgnoreCase("house"))
					&& (!homes.containsKey(name.toLowerCase()))) {
				/* new home */
				Home h = new Home(name);
				homes.put(name, h);

				/*
				 * A house will always have one room so the house gets a hallway
				 * by default. That means keeping the list of rooms up to date.
				 */
				Room r = h.getRooms().get(0);
				rooms.put(r.getName(), r);

				/* Updating triples */
				Node n1 = kg.getNode(name);
				n1.setPayload(h);

				Node n2 = new Node(r.getName(), r);

				Predicate p1 = kg.getPredicate("contains");
				Predicate p2 = kg.getPredicate("is_located_on_floor_1");

				List<Triple> tl = new ArrayList<Triple>();
				tl.add(new Triple(n1.getIdentifier() + " contains "
						+ n2.getIdentifier(), n1, n2, p1));
				tl.add(new Triple(n1.getIdentifier()
						+ " is_located_on_floor_1 " + n2.getIdentifier(), n2,
						n1, p2));

				kg.importTriples(tl);

			} else if ((parts[1].equalsIgnoreCase("room"))
					&& (!rooms.containsKey((parts[8] + ":" + name)
							.toLowerCase()))) {
				/* new room */
				name = parts[8] + ":" + name;
				Home h = homes.get(parts[8]);

				while (h.getFloors() < Integer.parseInt(parts[4])) {
					h.addFloor();
				}

				Room.RoomType rt;

				if (parts[6].equalsIgnoreCase("kitchen")) {
					rt = Room.RoomType.KITCHEN;
				} else if (parts[6].equalsIgnoreCase("closet")) {
					rt = Room.RoomType.CLOSET;
				} else if (parts[6].equalsIgnoreCase("dinning_room")) {
					rt = Room.RoomType.DINNING_ROOM;
				} else if (parts[6].equalsIgnoreCase("living_room")) {
					rt = Room.RoomType.LIVING_ROOM;
				} else if (parts[6].equalsIgnoreCase("hallway")) {
					rt = Room.RoomType.HALLWAY;
				} else if (parts[6].equalsIgnoreCase("bed_room")) {
					rt = Room.RoomType.BED_ROOM;
				} else if (parts[6].equalsIgnoreCase("family_room")) {
					rt = Room.RoomType.FAMILY_ROOM;
				} else if (parts[6].equalsIgnoreCase("garage")) {
					rt = Room.RoomType.GARAGE;
				} else {
					rt = Room.RoomType.BATHROOM;
				}

				Room r = new Room(name, Integer.parseInt(parts[4]), rt, h);
				h.addRoom(r);
				rooms.put(name, r);

				kg.getNode(name).setPayload(r);

			} else if ((parts[1].equalsIgnoreCase("appliance"))
					&& (!iot.containsKey((parts[6] + ":" + name).toLowerCase()))) {
				/* new appliance */
				name = parts[6] + ":" + name;
				Room r = rooms.get(parts[6].toLowerCase());
				Appliance x;

				if (parts[4].equalsIgnoreCase("window")) {
					x = new Window(name, r);
				} else if (parts[4].equalsIgnoreCase("tv")) {
					x = new TV(name, r);
				} else if (parts[4].equalsIgnoreCase("door")) {
					x = new Door(name, r);
				} else if (parts[4].equalsIgnoreCase("pandora")) {
					x = new Pandora(name, r);
				} else if (parts[4].equalsIgnoreCase("light")) {
					x = new Light(name, r);
				} else if (parts[4].equalsIgnoreCase("oven")) {
					x = new Oven(name, r);
				} else if (parts[4].equalsIgnoreCase("refrigerator")) {
					x = new Refrigerator(name, r);
				} else {
					x = new Thermostat(name, r);
				}

				iot.put(name, x);

				kg.getNode(name).setPayload(x);
			} else if ((parts[1].equalsIgnoreCase("sensor"))
					&& (!iot.containsKey((parts[6] + ":" + name).toLowerCase()))) {
				/* new sensor */
				name = parts[6] + ":" + name;
				Room r = rooms.get(parts[6].toLowerCase());
				Sensor x;

				if (parts[4].equalsIgnoreCase("smoke_detector")) {
					x = new SmokeDetector(name, r);
				} else if (parts[4].equalsIgnoreCase("ava")) {
					x = new Ava(name, r);
				} else {
					x = new Camera(name, r);
				}
				iot.put(name, x);

				kg.getNode(name).setPayload(x);
			}
		}
	}

	public Home getHome(String id) {
		return homes.get(id);
	}

	public void addHome(Home home) {
		if (!homes.containsKey(home.getName())) {
			homes.put(home.getName(), home);
		}
	}

	public Occupant getOccupant(String id) {
		return occupants.get(id);
	}

	public void addOccupant(Occupant occ) {
		if (!occupants.containsKey(occ.getName())) {
			occupants.put(occ.getName(), occ);
		}
	}

	public Room getRoom(String id) {
		return rooms.get(id);
	}

	public void addRoom(Room room) {
		if (!rooms.containsKey(room.getName())) {
			rooms.put(room.getName(), room);
		}
	}

	public IOTDevice getIOTDevice(String id) {
		return iot.get(id);
	}

	public void addIOTDevice(IOTDevice iotDev) {
		if (!iot.containsKey(iotDev.getId())) {
			iot.put(iotDev.getId(), iotDev);
		}
	}

	/**
	 * Finds the IOTDevice and identifies which subclass the instance belongs
	 * to. It then passes the command and instance to the appropriate private
	 * method for that class type.
	 * 
	 * @param command
	 * @throws Exception
	 */
	public void setAppliance(String command) throws Exception {
		String[] parts = command.split(" ");
		Appliance ap = (Appliance) iot.get(parts[2]);

		if (ap.getClass().getSimpleName().contains("Door")) {
			setDoor((Door) ap, command);
		} else if (ap.getClass().getSimpleName().contains("Window")) {
			setWindow((Window) ap, command);
		} else if (ap.getClass().getSimpleName().contains("Light")) {
			setLight((Light) ap, command);
		} else if (ap.getClass().getSimpleName().contains("Thermostat")) {
			setThermostat((Thermostat) ap, command);
		} else if (ap.getClass().getSimpleName().contains("TV")) {
			setTV((TV) ap, command);
		} else if (ap.getClass().getSimpleName().contains("Pandora")) {
			setPandora((Pandora) ap, command);
		} else if (ap.getClass().getSimpleName().contains("Refrigerator")) {
			setRefrigerator((Refrigerator) ap, command);
		} else {
			setOven((Oven) ap, command);
		}
	}

	/**
	 * Controls the oven. Includes setting the timer.
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setOven(Oven ap, String command) throws Exception {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i - 2].equalsIgnoreCase("temperature")) {
			double temp = Double.parseDouble(parts[i]);
			ap.changeTemperature(temp);
		} else if (parts[i - 2].equalsIgnoreCase("power")) {
			boolean power;
			if (parts[i].equalsIgnoreCase("on")) {
				power = true;
			} else {
				power = false;
			}
			ap.setPower(power);
		} else if (parts[i - 2].equalsIgnoreCase("timer")) {
			double timer = Double.parseDouble(parts[i]);
			ap.setTimer(timer);
		} else {
			throw new Exception(
					"Error: invalid command "
							+ parts[i - 1]
							+ ". Can only control the temperature, power, or the timer.");
		}

	}

	/**
	 * Controls the refrigerator. Also keeps track of beer.
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setRefrigerator(Refrigerator ap, String command)
			throws Exception {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i - 2].equalsIgnoreCase("temperature")) {
			double temp = Double.parseDouble(parts[i]);
			ap.changeTemperature(temp);
		} else if (parts[i - 2].equalsIgnoreCase("clean")) {
			boolean clean;
			if (parts[i].equalsIgnoreCase("true")) {
				clean = true;
			} else {
				clean = false;
			}
			ap.setCleaning(clean);
		} else if (parts[i - 2].equalsIgnoreCase("beer_count")) {
			int beer = Integer.parseInt(parts[i]);
			ap.addBeer(beer);
		} else {
			throw new Exception(
					"Error: invalid command "
							+ parts[i - 1]
							+ ". Can only control the temperature, cleaning status, or beer count.");
		}
	}

	/**
	 * Controlling Pandora. Includes changing channels and volume as well as
	 * turning it on and off
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setPandora(Pandora ap, String command) throws Exception {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i - 2].equalsIgnoreCase("channel")) {
			ap.setChannel(parts[i]);
		} else if (parts[i - 2].equalsIgnoreCase("power")) {
			boolean power;
			if (parts[i].equalsIgnoreCase("on")) {
				power = true;
			} else {
				power = false;
			}
			ap.setPower(power);
		} else if (parts[i - 2].equalsIgnoreCase("volume")) {
			int volume = Integer.parseInt(parts[i]);
			ap.changeVolume(volume);
		} else {
			throw new Exception("Error: invalid command " + parts[i - 1]
					+ ". Can only control the volume, channel, or power.");
		}
	}

	/**
	 * Controlling the TV. Includes changing channels and volume as well as
	 * turning it on and off
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setTV(TV ap, String command) throws Exception {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i - 2].equalsIgnoreCase("channel")) {
			ap.setChannel(parts[i]);
		}
		if (parts[i - 2].equalsIgnoreCase("power")) {
			boolean power;
			if (parts[i].equalsIgnoreCase("on")) {
				power = true;
			} else {
				power = false;
			}
			ap.setPower(power);
		} else if (parts[i - 2].equalsIgnoreCase("volume")) {
			int volume = Integer.parseInt(parts[i]);
			ap.changeVolume(volume);
		} else {
			throw new Exception("Error: invalid command " + parts[i - 1]
					+ ". Can only control the volume, channel, or power.");
		}
	}

	/**
	 * Adjusting the thermostat
	 * 
	 * @param ap
	 * @param command
	 */
	private void setThermostat(Thermostat ap, String command) {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		double temp = Double.parseDouble(parts[i]);
		ap.changeTemperature(temp);
	}

	/**
	 * Adjusting the light.
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setLight(Light ap, String command) throws Exception {
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i - 2].equalsIgnoreCase("power")) {
			boolean power;
			if (parts[i].equalsIgnoreCase("on")) {
				power = true;
			} else {
				power = false;
			}
			ap.setPower(power);
		} else if (parts[i - 2].equalsIgnoreCase("intensity")) {
			int intensity = Integer.parseInt(parts[i]);
			ap.changeIntensity(intensity);
		} else {
			throw new Exception("Error: invalid command " + parts[i - 1]
					+ ". Can only control the intensity or power.");
		}
	}

	/**
	 * Setting the window.
	 * 
	 * @param ap
	 * @param command
	 * @throws Exception
	 */
	private void setWindow(Window ap, String command) throws Exception {
		boolean open;
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i].equalsIgnoreCase("open")) {
			open = true;
		} else if (parts[i].equalsIgnoreCase("closed")) {
			open = false;
		} else {
			throw new Exception("Error: invalid command " + parts[i]
					+ ". Only the commands open and closed are valid.");
		}
	}

	/**
	 * Setting the door.
	 * 
	 * @param d
	 * @param command
	 * @throws Exception
	 */
	private void setDoor(Door d, String command) throws Exception {
		Door.Status dr;
		String[] parts = command.split(" ");
		int i = parts.length - 1;

		if (parts[i].equalsIgnoreCase("open")) {
			dr = Door.Status.OPEN;
		} else if (parts[i].equalsIgnoreCase("closed")) {
			dr = Door.Status.CLOSED;
		} else if (parts[i].equalsIgnoreCase("locked")) {
			dr = Door.Status.LOCKED;
		} else {
			throw new Exception("Error: invalid command " + parts[i]
					+ ". Only the commands open, closed, and locked are valid.");
		}

		d.setStatus(dr);
	}

	/**
	 * Removes and occupant from a home. Occupant and House instances still
	 * exist but the house no longer recognizes the Occupant as a
	 * resident/guest/burglar
	 * 
	 * @param occupant
	 * @param home
	 */
	public void removeOccHouseRelation(String occupant, String home) {
		Home h = homes.get(home);
		Occupant o = occupants.get(occupant);

		h.removeBurglar(o);
		h.removeGuest(o);
		h.removeResident(o);
	}

	/**
	 * Removes an occupant from the model. Any homes that a relation with said
	 * occupant has that relation removed
	 * 
	 * @param id
	 */
	public void removeOcc(String id) {
		Occupant occ = occupants.get(id);
		Iterator<String> names = homes.keySet().iterator();

		while (names.hasNext()) {
			String name = names.next();
			Home h = homes.get(name);

			if (h.getBurglars().contains(occ)) {
				h.removeBurglar(occ);
			} else if (h.getGuests().contains(occ)) {
				h.removeGuest(occ);
			} else if (h.getResidents().contains(occ)) {
				h.removeResident(occ);
			}
		}

		occupants.remove(id);
	}

	/**
	 * Removes a home from the model completely. Any rooms and IOTDevices within
	 * the home are also removed
	 * 
	 * @param id
	 */
	public void removeHome(String id) {
		homes.remove(id);

		Iterator<String> names = rooms.keySet().iterator();
		while (names.hasNext()) {
			String n = names.next();

			if (n.contains(id)) {
				rooms.remove(n);
			}
		}

		names = null;
		names = iot.keySet().iterator();
		while (names.hasNext()) {
			String n = names.next();

			if (n.contains(id)) {
				iot.remove(n);
			}
		}
	}

	/**
	 * Removes a room from the model completely. Also any IOTDevices that were
	 * located in the room are also removed
	 * 
	 * @param id
	 */
	public void removeRoom(String id) {
		Room r = rooms.get(id);

		Iterator<String> names = homes.keySet().iterator();

		while (names.hasNext()) {
			String name = names.next();

			if (id.contains(name)) {
				homes.get(name).removeRoom(r);
				break;
			}
		}

		rooms.remove(id);

		names = null;
		names = iot.keySet().iterator();
		while (names.hasNext()) {
			String n = names.next();

			if (n.contains(id)) {
				iot.remove(n);
			}
		}
	}

	/**
	 * Removes an IOTDevice from the model completely
	 * 
	 * @param id
	 */
	public void removeIOTDevice(String id) {
		IOTDevice iotd = iot.get(id);
		Iterator<String> names = rooms.keySet().iterator();

		while (names.hasNext()) {
			String name = names.next();

			if (id.contains(name)) {
				rooms.get(name).removeIotDevice(iotd);
				break;
			}
		}
		iot.remove(id);
	}
}
