package cscie97.asn4.housemate.entitlement;

import java.util.*;

/**
 * Contains information on the user (id, password, voice sig, role, etc). Used
 * to validate commands and make sure that the user has the permission to
 * execute a particular command.
 */
public class User {

	private Role role;
	private List<ResourceRole> resourceRoles;
	private VoiceSig voiceSig;
	private String name;
	private String id;
	private String password;
	private AccessToken at;
	
	public User(String name, String id, String password, Role role) {
		this.name = name;
		this.id = id;
		this.password = password;
		this.role = role;
		this.at = null;
		this.resourceRoles = new ArrayList<ResourceRole>();
		this.voiceSig = new VoiceSig("--"+name.trim()+"--");
	}

	public String getName() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public String getPassword() {
		return this.password;
	}

	public AccessToken getAt() {
		return at;
	}

	/**
	 * User has logged in
	 * @param at
	 */
	public void accept(AccessToken at) {
		this.at = at;
	}

	public Role getRole() {
		return this.role;
	}

	public List<ResourceRole> getResourceRoles() {
		return this.resourceRoles;
	}
	
	public void addResourceRole(ResourceRole rr){
		resourceRoles.add(rr);
	}

	public VoiceSig getVoiceSig() {
		return this.voiceSig;
	}
	
	/**
	 * User has logged off
	 */
	public void removeAt(){
		at = null;
	}

}