package cscie97.asn4.housemate.entitlement;

import java.io.IOException;
import java.util.*;

import cscie97.asn4.housemate.model.CommandHandler;

/**
 * Keeps track of all the active users that are currently logged on the system
 * while only allowing registered users to access the system. An AccessToken is
 * required to access any user related information for any purpose (e.g. seeing
 * if a command is valid). Implements the Visitor pattern.
 */
public class Gatekeeper {
	private static volatile Gatekeeper instance;
	public static Random rn = new Random();
	public static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890"; // for
																										// random
																										// password
																										// generation
																										// when
																										// creating
																										// a
																										// new
																										// user

	private List<AccessToken> usedAT;
	private List<String> usedPasswords;
	private UserLibrary ul;
	private Map<AccessToken, User> loggedIn;

	private Gatekeeper() {
		this.usedAT = new ArrayList<AccessToken>();
		this.ul = UserLibrary.getInstance();
		this.loggedIn = new HashMap<AccessToken, User>();
		this.usedPasswords = new ArrayList<String>();
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Gatekeeper doesn't already exist. If it
	 * does exist it will simply return the instance, otherwise it will create
	 * one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Gatekeeper getInstance() {
		if (instance == null) {
			instance = new Gatekeeper();
		}

		return instance;
	}

	/**
	 * Standard login class (e.g. needs username and password)
	 * 
	 * @param id
	 * @param password
	 */
	public User logIn(String id, String password) {
		User u = null;
		try {
			u = ul.validateLogin(id, password);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (u != null) {
			u.accept(generateAccessToken());
			loggedIn.put(u.getAt(), u);
		}
		return u;
	}

	/**
	 * Voice based login. Uses voice recognition to identify who is giving a
	 * verbal command/query through Ava
	 * 
	 * @param voice
	 */
	public User logIn(String voice) {
		User u = null;
		try {
			u = ul.validateLogin(voice);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		if (u != null) {
			u.accept(generateAccessToken());
			loggedIn.put(u.getAt(), u);
		}
		return u;
	}

	/**
	 * Checks to see if a command is valid. Uses to AccessToken to see which
	 * User has that token. If their is a user assigned to said token, then it
	 * will check to see what privileges that user has and compares the command
	 * to it. Returns true if everything checks out.
	 * 
	 * @param at
	 * @param command
	 * @throws InvalidAccessTokenException
	 * @throws AccessDeniedException
	 */
	public boolean visit(AccessToken at, String command)
			throws InvalidAccessTokenException, AccessDeniedException {
		User u = loggedIn.get(at);
		if (u == null) {
			throw new InvalidAccessTokenException(
					"Error: invalid access token passed in (guid: "
							+ at.getGuid() + ")");
		}

		boolean valid = false;

		Role r = u.getRole();

		if (!r.getType().equals(Role.RoleType.ADMIN)) {
			String[] parts = command.split(" ");
			if ((!parts[0].equalsIgnoreCase("set"))
					&& (!parts[0].equalsIgnoreCase("show"))) {
				throw new AccessDeniedException(
						"User "
								+ u.getName()
								+ " does not have permission to give the following command: "
								+ command);
			} else {
				if (parts[0].equalsIgnoreCase("set")) {
					String[] p = parts[2].split(":");
					List<ResourceRole> rr = u.getResourceRoles();

					boolean guest = false;
					boolean resident = false;

					for (int i = 0; i < rr.size(); i++) {
						String house = rr.get(i).getResource().getHouse();
						if (house.equalsIgnoreCase(p[0].trim())) {
							String[] m = rr.get(i).getType().split("_");

							if (m[2].equalsIgnoreCase("guest")) {
								guest = true;
							} else if (m[2].equalsIgnoreCase("resident")) {
								resident = true;
							}
							break;
						}
					}

					if ((!guest) && (!resident)) {
						throw new AccessDeniedException(
								"User "
										+ u.getName()
										+ " does not have permission to give the following command: "
										+ command);
					} else {
						String appliance;
						if (p[2].contains("ava")) {
							// command given through ava
							if (command.contains("?")) {
								// query made
								return true;
							} else if (command.contains("lights")) {
								appliance = "light";
							} else {
								String[] l = command.split("'");
								String[] m = l[1].split(" ");
								appliance = m[1].trim();
							}
						} else {
							appliance = p[2].substring(0, p[2].length() - 1);
						}

						if (guest
								&& ((!appliance.equalsIgnoreCase("tv"))
										&& (!appliance.equalsIgnoreCase("door"))
										&& (!appliance
												.equalsIgnoreCase("window")) && (!appliance
											.equalsIgnoreCase("pandora")))) {
							// guests are only allowed entertainment, window,
							// and door privileges.
							throw new AccessDeniedException(
									"User "
											+ u.getName()
											+ " does not have permission to give the following command: "
											+ command);
						} else {
							List<Permission> perm = u.getRole()
									.getPermissions();
							boolean hasPermission = false;
							for (int i = 0; i < perm.size(); i++) {
								String ap = perm.get(i).getPermission();
								if (ap.contains(appliance.trim())) {
									hasPermission = true;
									break;
								}
							}
							if (!hasPermission) {
								throw new AccessDeniedException(
										"User "
												+ u.getName()
												+ " does not have permission to give the following command: "
												+ command);
							}
						}
					}
				}
				valid = true;
			}
		} else {
			// user is an admin, checking to see if I need to add a resource to
			// another user or add a new user.
			if (command.contains("define occupant")) {
				String[] parts = command.split(" ");

				// making sure user instance hasn't already been created from a
				// file
				if (!ul.checkUser(parts[2].trim())) {
					String id = parts[2].trim();
					String name = id;
					String type = parts[4].trim();

					// generating password
					String password = generatePassword();
					boolean used = true;
					while (used) {
						used = false;
						for (int i = 0; i < usedPasswords.size(); i++) {
							if (password.equals(usedPasswords.get(i))) {
								used = true;
								password = generatePassword();
								break;
							}
						}
					}

					ul.addUser(name, id, password, type);
				}

			} else if (command.contains("add occupant")) {
				String[] parts = command.split(" ");

				if (parts.length == 5) {
					ul.addResource(parts[2].trim(), parts[4].trim(), "resident");
				} else {
					ul.addResource(parts[2].trim(), parts[4].trim(),
							parts[6].trim());
				}
			}
			valid = true;
		}

		return valid;
	}

	/**
	 * Add in a list of users from a file. Only and admin can use this
	 * particular command.
	 * 
	 * @param at
	 * @param fileName
	 * @throws Exception
	 */
	public void addUsersFromFile(AccessToken at, String fileName)
			throws Exception {
		User u = loggedIn.get(at);
		if (u == null) {
			throw new InvalidAccessTokenException(
					"Error: invalid access token passed in (guid: "
							+ at.getGuid() + ")");
		}

		// checking that user is and admin
		Role r = u.getRole();
		if (!r.getType().equals(Role.RoleType.ADMIN)) {
			throw new AccessDeniedException("User " + u.getName()
					+ " does not have permission to add users from file");
		}

		ArrayList<ArrayList<String>> users = Importer.loadAuthenFile(fileName);

		// adding users to model and library
		CommandHandler ch = new CommandHandler();
		for (int i = 0; i < users.size(); i++) {
			ArrayList<String> user = users.get(i);
			String name = user.get(0).split(":")[1].trim();
			String id = user.get(1).split(":")[1].trim();
			String password = user.get(2).split(":")[1].trim();
			String type = user.get(3).split(":")[1].trim();

			ul.addUser(name, id, password, type);

			String command = "define occupant " + name + " type " + type;
			
			ch.validateCommand(at, command);
		}
	}

	/**
	 * logout of system
	 * 
	 * @param at
	 * @throws InvalidAccessTokenException
	 */
	public void logOut(AccessToken at) throws InvalidAccessTokenException {
		User u = loggedIn.get(at);
		if (u == null) {
			throw new InvalidAccessTokenException(
					"Error: invalid access token passed in (guid: "
							+ at.getGuid() + ")");
		} else {
			usedAT.add(at);
			loggedIn.remove(at);
		}
	}

	/**
	 * Generates a guid password. The password is a guid due to the fact that
	 * Users are stored in a Map with a password for their key.
	 * 
	 * @return
	 */
	private String generatePassword() {
		char[] text = new char[10];
		for (int i = 0; i < 10; i++) {
			text[i] = characters.charAt(rn.nextInt(characters.length()));
		}
		return new String(text);
	}

	/**
	 * Generate a new access token with a guid for the user.
	 */
	private AccessToken generateAccessToken() {
		int n = rn.nextInt((100000 - 10000) + 1) + 10000;
		String guid = Integer.toString(n);
		boolean used = true;

		while (used) {
			used = false;
			for (int i = 0; i < usedAT.size(); i++) {
				AccessToken at = usedAT.get(i);
				if (at.getGuid().equals(guid)) {
					n = rn.nextInt((100000 - 10000) + 1) + 10000;
					guid = Integer.toString(n);
					used = true;
					break;
				}
			}
		}

		return new AccessToken(guid);
	}

}