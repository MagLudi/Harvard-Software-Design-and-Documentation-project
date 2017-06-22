package cscie97.asn4.housemate.controller;

import java.util.*;

/**
 * A Cause. If the cause holds true, then the following action will be executed.
 * To check the validity, some checks must be made. See Clause class for
 * details.
 */
public class Rule {

	List<Action> actions;
	List<Clause> predicate;

	public Rule() {
		this.actions = new ArrayList<Action>();
		this.predicate = new ArrayList<Clause>();
	}

	public List<Action> getActions() {
		return actions;
	}

	public List<Clause> getPredicate() {
		return predicate;
	}

	public void addClause(Clause clause) {
		predicate.add(clause);
	}

	public void addAction(Action action) {
		actions.add(action);
	}

}