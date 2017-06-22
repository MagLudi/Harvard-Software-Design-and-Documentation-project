package cscie97.asn4.housemate.entitlement;

import java.util.*;

import cscie97.asn4.housemate.controller.ControlerHandler;

/**
 * The type of role the user has and what permissions they have as a result. For
 * instance, the admin will have full permission to every aspect of the House
 * Mate System while an Adult will only have full permission to the resources of
 * any house they are a resident off. The singleton pattern is used to ensure
 * that there is only one type of each role. It implements the Composite pattern
 * since it uses a hierachy when handling the Permissions. For example, an Admin
 * will have the same and more privlages as an Adult while the Adult will have
 * the privileges that a Child has plus some additional ones. The result of that
 * would be the Admin having Adult as a sub Role and the Adult having the Child
 * as its.
 */
public class Role {
	private static volatile Role admin;
	private static volatile Role adult;
	private static volatile Role child;
	private static volatile Role pet;
	private static volatile Role unknown;

	public enum RoleType {
		ADMIN, ADULT, CHILD, PET, UNKNOW
	}

	private List<Permission> permissions;
	private RoleType type;
	private List<Role> subRoles;
	private List<User> users;

	private Role(RoleType rt) {
		this.type = rt;
		this.users = new ArrayList<User>();
		this.permissions = new ArrayList<Permission>();
		this.subRoles = new ArrayList<Role>();

		if (rt == RoleType.ADMIN) {
			this.subRoles.add(getAdult());
			this.permissions.add(new Permission(
					"define, add, set, remove, show"));
		} else if (rt == RoleType.ADULT) {
			this.subRoles.add(getChild());
			this.permissions.add(new Permission("oven"));
			this.permissions.add(new Permission("thermostat"));
		} else {
			if (rt == RoleType.CHILD) {
				this.permissions.add(new Permission("tv"));
				this.permissions.add(new Permission("lights"));
				this.permissions.add(new Permission("ava"));
				this.permissions.add(new Permission("window"));
				this.permissions.add(new Permission("door"));
				this.permissions.add(new Permission("pandora"));
				this.permissions.add(new Permission("refrigerator"));
			}
		}
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Role representing the admin doesn't
	 * already exist. If it does exist it will simply return the instance,
	 * otherwise it will create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Role getAdmin() {
		if (admin == null) {
			admin = new Role(RoleType.ADMIN);
		}

		return admin;
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Role representing the adult doesn't
	 * already exist. If it does exist it will simply return the instance,
	 * otherwise it will create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Role getAdult() {
		if (adult == null) {
			adult = new Role(RoleType.ADULT);
		}

		return adult;
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Role representing the child doesn't
	 * already exist. If it does exist it will simply return the instance,
	 * otherwise it will create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Role getChild() {
		if (child == null) {
			child = new Role(RoleType.CHILD);
		}

		return child;
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Role representing the pet doesn't
	 * already exist. If it does exist it will simply return the instance,
	 * otherwise it will create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Role getPet() {
		if (pet == null) {
			pet = new Role(RoleType.PET);
		}

		return pet;
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of Role representing the unknown doesn't
	 * already exist. If it does exist it will simply return the instance,
	 * otherwise it will create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized Role getUnknown() {
		if (unknown == null) {
			unknown = new Role(RoleType.UNKNOW);
		}

		return unknown;
	}

	public List<Permission> getPermissions() {
		List<Permission> all = new ArrayList<Permission>();
		all.addAll(permissions);

		for (int i = 0; i < subRoles.size(); i++) {
			all.addAll(subRoles.get(i).getPermissions());
		}

		return all;
	}

	public RoleType getType() {
		return this.type;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void addUser(User user) {
		users.add(user);
	}

	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

}