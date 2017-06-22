package cscie97.asn4.housemate.entitlement;

import java.sql.Timestamp;
import java.util.*;

import cscie97.asn4.housemate.controller.FileLogger;

/**
 * Contains all the users for the system. Since there are to ways to login,
 * there are two maps being used. One uses the password as the key to the User
 * while the other uses VoiceSig to map to the user.
 */
public class UserLibrary {
	private static volatile UserLibrary instance;

	private Map<String, User> usersMap;
	private Map<String, User> voiceMap;
	private Map<String, User> nameMap; // for adding resources

	private UserLibrary() {
		this.usersMap = new HashMap<String, User>();
		this.voiceMap = new HashMap<String, User>();
		this.nameMap = new HashMap<String, User>();
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of UserLibrary doesn't already exist. If it
	 * does exist it will simply return the instance, otherwise it will create
	 * one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized UserLibrary getInstance() {
		if (instance == null) {
			instance = new UserLibrary();
		}

		return instance;
	}

	/**
	 * Validate the password and username. For security reasons both are case
	 * sensitive.
	 * 
	 * @param userName
	 * @param password
	 * @throws AuthenticationException
	 */
	public User validateLogin(String userName, String password)
			throws AuthenticationException {
		User user = usersMap.get(password);

		if ((user == null) || (!user.getId().equals(userName))) {
			throw new AuthenticationException(
					"Error: invalid username and/or password. Username and password are case sensitive.");
		}

		return user;
	}

	/**
	 * Validate the voice pattern
	 * 
	 * @param voicePattern
	 * @throws AuthenticationException
	 */
	public User validateLogin(String voicePattern)
			throws AuthenticationException {
		User user = voiceMap.get(voicePattern);

		if (user == null) {
			throw new AuthenticationException(
					"Error: voice pattern not recognized. Unable to fufill command/query.");
		}

		return user;
	}

	/**
	 * 
	 * @param name
	 * @param id
	 * @param password
	 * @param type
	 * @return
	 */
	public User addUser(String name, String id, String password, String type) {
		User u = new User(name, id, password, RoleLibrary.findRole(type));

		usersMap.put(password, u);
		voiceMap.put(u.getVoiceSig().getVoiceSig(), u);
		nameMap.put(name, u);
		
		String msg = "New user:	name: " + name + "	id: " + id
				+ "	password: " + password + "	type: " + type;
		log(msg);

		return u;
	}

	/**
	 * @param name
	 * @param house
	 * @param relation
	 */
	public void addResource(String name, String house, String relation) {
		User u = nameMap.get(name);
		String rr = house + "_"
				+ u.getRole().getType().toString().toLowerCase() + "_"
				+ relation;
		u.addResourceRole(new ResourceRole(rr));
	}
	
	/**
	 * checks to see if a user instance exists
	 * @param name
	 * @return
	 */
	public boolean checkUser(String name){
		if(nameMap.get(name) == null){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Prints out a message that a new user has been created and provides the
	 * time stamp
	 * 
	 * @param message
	 */
	private void log(String message) {
		java.util.Date date = new java.util.Date();
		String msg = new Timestamp(date.getTime()) + message;
		System.out.println(msg);
		FileLogger.getLogger().logMsg(msg);
	}

}