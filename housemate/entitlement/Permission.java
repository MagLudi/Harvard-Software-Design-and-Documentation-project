package cscie97.asn4.housemate.entitlement;

/**
 * Contains the type of device and what device settings are available. Also
 * contains a basic description
 */
public class Permission {
	public enum PermissionType {
		OVEN, REFRIGERATOR, THERMASTAT, WINDOW, DOOR, PANDORA, TV, LIGHT, AVA, CRUD
	}

	private PermissionType type;
	// private List<String> settings;
	private String permission;

	public Permission(String appliance) {
		if (!appliance.equalsIgnoreCase("define, add, set, remove, show")) {
			this.permission = "control_" + appliance;

			if (appliance.equalsIgnoreCase("tv")) {
				this.type = PermissionType.TV;
			} else if (appliance.equalsIgnoreCase("door")) {
				this.type = PermissionType.DOOR;
			} else if (appliance.equalsIgnoreCase("ava")) {
				this.type = PermissionType.AVA;
			} else if (appliance.equalsIgnoreCase("oven")) {
				this.type = PermissionType.OVEN;
			} else if (appliance.equalsIgnoreCase("light")) {
				this.type = PermissionType.LIGHT;
			} else if (appliance.equalsIgnoreCase("pandora")) {
				this.type = PermissionType.PANDORA;
			} else if (appliance.equalsIgnoreCase("window")) {
				this.type = PermissionType.WINDOW;
			} else if (appliance.equalsIgnoreCase("thermastat")) {
				this.type = PermissionType.THERMASTAT;
			} else {
				this.type = PermissionType.REFRIGERATOR;
			}
		} else{
			this.permission = appliance;
			this.type = PermissionType.CRUD;
		}
	}

	public PermissionType getType() {
		return this.type;
	}

	// public List<String> getSettings() {
	// return this.settings;
	// }

	public String getPermission() {
		return this.permission;
	}

}