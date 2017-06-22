package cscie97.asn4.housemate.controller;

import cscie97.asn4.housemate.model.Triple;

/**
 * Contains a query (Triple), a comparison type, and a value to compare to. If
 * the value the query returns passes, Then the Rule the Clause is part of is
 * closer to being fired.
 */
public class Clause {
	public enum Opperator {
		LESS, GREATER, EQUAL, NOT_EQUAL
	}

	private Object passValue; // The value needed for the check to pass
	private Triple query; // The query needed to retrieve the appropriate triple
							// for comparison
	private Opperator opperator;

	public Clause(Object passValue, Triple query, Opperator opperator) {
		this.passValue = passValue;
		this.query = query;
		this.opperator = opperator;
	}

	public Opperator getOpperator() {
		return opperator;
	}

	public Object getPassValue() {
		return this.passValue;
	}

	/**
	 * Sometimes the pass value of a clause changes when there is a change to
	 * the overall system. This makes things more modular.
	 * 
	 * @param newVal
	 */
	public void updatePassValue(Object newVal) {
		passValue = newVal;
	}

	public Triple getQuery() {
		return this.query;
	}

}