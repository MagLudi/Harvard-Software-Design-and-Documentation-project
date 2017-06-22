package cscie97.asn4.housemate.model;

/**
 * Represents instances of Subjects and Objects for a Triple. Contains a String
 * id (e.g. Coffee, Mary_Sue). Nodes are to be treated as unique. When comparing
 * id's they are to be treated as case insensitive.
 * 
 * Created: Sep 6, 2015
 */

public class Node {
	private String identifier;
	private Object payload;

	public Node(String identifier) {
		this.identifier = identifier;
		this.payload = null;
	}
	
	public Node(String identifier, Object payload) {
		this.identifier = identifier;
		this.payload = payload;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Object getPayload() {
		return payload;
	}
	
	public void setPayload(Object payload){
		if(this.payload == null){
			this.payload = payload;
		}
	}
	
}
