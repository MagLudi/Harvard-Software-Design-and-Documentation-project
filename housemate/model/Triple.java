package cscie97.asn4.housemate.model;

/**
 * An instance of a subject (Node), predicate (Predicate), and object (Node). It
 * also contains a Sting id. Triples are to be treated as unique. When comparing
 * id's they are to be treated as case insensitive.
 * 
 * Created: Sep 6, 2015
 */

public class Triple {
	private String identifier;
	private Node subject;
	private Node object;
	private Predicate predicate;

	public Triple(String identifier, Node subject, Node object,
			Predicate predicate) {
		this.identifier = identifier;
		this.subject = subject;
		this.object = object;
		this.predicate = predicate;
	}

	public String getIdentifier() {
		return identifier;
	}

	public Node getSubject() {
		return subject;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public Node getObject() {
		return object;
	}

}
