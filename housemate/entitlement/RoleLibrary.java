package cscie97.asn4.housemate.entitlement;

/**
 * Handles the assignment of Role to User. Returns a Role that coresponds to the
 * string indicator passed in (e.g. “adult” results in a Role of type Adult
 * being returned). There are five types of Roles and they each only have one
 * available instance: Admin, Adult, Child, Pet, Unknown
 */
public class RoleLibrary {

	/**
	 * Takes in a string describing the type of user (e.g. adult, child) and
	 * returns the appropriate role for said user.
	 * 
	 * @param userType
	 */
	public static Role findRole(String userType) {
		Role r;

		if (userType.equalsIgnoreCase("admin")) {
			r = Role.getAdmin();
		} else if (userType.equalsIgnoreCase("adult")) {
			r = Role.getAdult();
		} else if (userType.equalsIgnoreCase("child")) {
			r = Role.getChild();
		} else if (userType.equalsIgnoreCase("pet")) {
			r = Role.getPet();
		} else {
			r = Role.getUnknown();
		}

		return r;
	}

}