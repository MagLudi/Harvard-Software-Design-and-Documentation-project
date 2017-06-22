package cscie97.asn4.housemate.model;

/**
 * Represents the predicate portion of a Triple. Contains a String id (e.g.
 * plays_sport, works_at). Predicates are to be treated as unique. When
 * comparing id's they are to be treated as case insensitive.
 * 
 * Created: Sep 6, 2015
 */

public class Predicate {
	private String identifier;

	public Predicate(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}
}
