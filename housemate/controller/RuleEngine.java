package cscie97.asn4.housemate.controller;

import java.sql.Timestamp;
import java.util.*;

import cscie97.asn4.housemate.entitlement.Importer;
import cscie97.asn4.housemate.model.KnowledgeGraph;
import cscie97.asn4.housemate.model.Node;
import cscie97.asn4.housemate.model.Predicate;
import cscie97.asn4.housemate.model.Triple;

/**
 * Contains a map of rules that can be accessed through their corresponding
 * stimulant. When a rule is triggered, it uses the KnowledgeGraph to see if its
 * corresponding actions can be taken into affect. If so, those actions are
 * returned to the caller so more commands can be converted into commands.
 */
public class RuleEngine {
	public static String defaultRuleFile = "./default.rule";
	private static volatile RuleEngine instance;

	private Map<String, Rule> rules;
	private KnowledgeGraph kg;

	private RuleEngine() throws Exception {
		this(defaultRuleFile);
	}

	private RuleEngine(String ruleFile) throws Exception {
		this.rules = new HashMap<String, Rule>();
		this.kg = KnowledgeGraph.getInstance();

		generateRules(ruleFile);
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of ControlerHandler doesn't already exist.
	 * If it does exist it will simply return the instance, otherwise it will
	 * create one and return that.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized RuleEngine getInstance() throws Exception {
		if (instance == null) {
			instance = new RuleEngine();
		}

		return instance;
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of ControlerHandler doesn't already exist.
	 * If it does exist it will simply return the instance, otherwise it will
	 * create one and return that. This one takes in a file name of a rule file
	 * for the RuleEngine instance
	 * 
	 * @return
	 * @throws Exception
	 */
	public static synchronized RuleEngine getInstance(String fileName)
			throws Exception {
		if (instance == null) {
			instance = new RuleEngine(fileName);
		}

		return instance;
	}

	/**
	 * Takes in a file to generate rules with. After the file is parsed it goes
	 * through each element of the ArrayList<ArrayList<String>> to generate a
	 * Rule. After the Rule is created, the first element of that
	 * ArrayList<String> is used as a key for the Map used to store the rules.
	 * 
	 * @param file
	 * @throws Exception
	 */
	private void generateRules(String file) throws Exception {
		ArrayList<ArrayList<String>> ruleList = Importer.loadRuleFile(file);

		// generating rules and adding them to map
		for (int i = 0; i < ruleList.size(); i++) {
			ArrayList<String> r = ruleList.get(i);
			Rule rule = new Rule();

			String[] parts = r.get(0).split(":");

			String stimulus = parts[1].trim().toLowerCase();

			// creating clauses
			int j = 1;
			while (r.get(j).contains("clause:")) {
				parts = r.get(j).split(":");

				String[] t = parts[1].trim().split(" ");

				// generating triple
				String subjectID = t[0].trim().toLowerCase();
				String predicateID = t[1].trim().toLowerCase();
				String objectID = t[2].trim().toLowerCase();

				String id = subjectID + " " + predicateID + " " + objectID
						+ ".";

				Node subject = kg.getNode(subjectID);
				Predicate predicate = kg.getPredicate(predicateID);
				Node object = kg.getNode(objectID);

				Triple query = new Triple(id, subject, object, predicate);
				// retrieving comparison operator
				Clause.Opperator op;

				if (t[3].trim().equals("=")) {
					op = Clause.Opperator.EQUAL;
				} else if (t[3].trim().equals("!=")) {
					op = Clause.Opperator.NOT_EQUAL;
				} else if (t[3].trim().equals(">")) {
					op = Clause.Opperator.GREATER;
				} else {
					op = Clause.Opperator.LESS;
				}

				// getting value for comparison
				Object val = t[4].trim();

				Clause c = new Clause(val, query, op);
				rule.addClause(c);
				j++;
			}

			// creating actions
			while (j < r.size()) {
				parts = r.get(j).split(":");
				String ac;
				if (r.get(j).contains("message")) {
					ac = parts[1].trim() + ": " + parts[2].trim();
				} else {
					ac = parts[1].trim();
				}
				Action a = new Action(ac);
				rule.addAction(a);
				j++;
			}

			rules.put(stimulus, rule);
		}

	}

	/**
	 * Checks to see which Rules are applicable by using the stimulus and
	 * deviceType to find the appropriate Rules. If any of the Rules hold to be
	 * true, then their corresponding Actions are returned.
	 * 
	 * @param su
	 */
	public List<Action> evaluateStimulus(StatusUpdate su) {
		String device = su.getDeviceID();
		String stimulus = su.getStimulus();
		String deviceType = su.getDeviceType();
		Rule r = rules.get(deviceType.toLowerCase() + " "
				+ stimulus.toLowerCase());

		java.util.Date date = new java.util.Date();
		String msg = new Timestamp(date.getTime()) + "	Device: " + device
				+ "	Stimulus: " + stimulus + "	Actions:";

		if (validateRule(r, su)) {
			List<Action> a = r.getActions();

			//logging
			for (int i = 0; i < a.size(); i++) {
				if (i == 0) {
					msg = msg + " " + a.get(i).getAction();
				} else {
					msg = msg + ", " + a.get(i).getAction();
				}
			}

			FileLogger.getLogger().logMsg(msg);
			return a;
		}
		//logging
		msg = msg + " none";
		FileLogger.getLogger().logMsg(msg);
		return null;
	}

	/**
	 * Uses the KnowledgeGraph to see if the Rule is valid.
	 * 
	 * @param rule
	 * @param su
	 */
	private boolean validateRule(Rule rule, StatusUpdate su) {
		String device = su.getDeviceID();
		String stimulus = su.getStimulus();
		String deviceType = su.getDeviceType();
		String newVal = su.getVal();

		// if occupants are involved then check to see if there are none left in
		// the room. If there are none, then it can continue on as normal
		if (stimulus.contains("OCCUPANT")) {
			// getting location
			String[] p = device.split(":");

			String loc = p[0] + ":" + p[1];

			// checking for others
			Triple t2 = new Triple("? is_a occupant.", kg.getNode("?"),
					kg.getNode("occupant"), kg.getPredicate("is_a"));
			Triple t3 = new Triple("? has_location " + loc + ".",
					kg.getNode("?"), kg.getNode(loc),
					kg.getPredicate("has_location"));

			Set<Triple> r1 = kg.executeQuery(t2);
			Set<Triple> r2 = kg.executeQuery(t3);

			Iterator<Triple> i1 = r1.iterator();
			Iterator<Triple> i2 = r2.iterator();

			ArrayList<Triple> l1 = new ArrayList<Triple>();
			while (i1.hasNext()) {
				l1.add(i1.next());
			}

			while (i2.hasNext()) {
				Triple t = i2.next();
				if (!t.getSubject().getIdentifier().equalsIgnoreCase(newVal)) {
					for (int x = 0; x < l1.size(); x++) {
						Triple u = l1.get(x);
						if (t.getSubject()
								.getIdentifier()
								.equalsIgnoreCase(
										u.getSubject().getIdentifier())) {
							// another person found
							return false;
						}
					}
				}
			}
		}
		boolean pass = true;

		List<Clause> predicates = rule.getPredicate();

		// checking predicates for validity
		for (int i = 0; i < predicates.size(); i++) {
			Clause c = predicates.get(i);

			Set<Triple> results = kg.executeQuery(c.getQuery());

			// if its not null we need to filter out the valid results and see
			// if the conflict
			if (results != null) {
				Iterator<Triple> list = results.iterator();
				ArrayList<Triple> wanted = new ArrayList<Triple>();

				while (list.hasNext()) {
					Triple t = list.next();

					String x;

					if (deviceType.equalsIgnoreCase("ava")
							|| deviceType.equalsIgnoreCase("camera")
							|| deviceType.equalsIgnoreCase("smoke_detector")) {
						int index = device.lastIndexOf(":"); // second colon
						x = device.substring(0, index).trim();
					} else {
						x = device;
					}

					if (t.getSubject().getIdentifier().contains(x)) {
						wanted.add(t);
					}
				}
				// all predicates are invalid due to improper type. no valid
				// action to take
				if (wanted.size() == 0) {
					pass = false;
					break;
				} else {
					// does return value for remaining predicates pass the check
					for (int j = 0; j < wanted.size(); j++) {
						String object = wanted.get(j).getObject()
								.getIdentifier();
						String val = (String) c.getPassValue();
						if ((c.getOpperator() == Clause.Opperator.GREATER)
								&& (!(Double.parseDouble(object) > Double
										.parseDouble(val)))) {
							wanted.remove(j);
							j--;
						} else if ((c.getOpperator() == Clause.Opperator.LESS)
								&& (!(Double.parseDouble(object) < Double
										.parseDouble(val)))) {
							wanted.remove(j);
							j--;
						} else if ((c.getOpperator() == Clause.Opperator.EQUAL)
								&& (!object.equalsIgnoreCase(val))) {
							if (!stimulus.contains("volume")
									&& !stimulus.contains("channel")
									&& !stimulus.contains("temperature")) {
								wanted.remove(j);
								j--;
							} else if (!object.equalsIgnoreCase(newVal)) {
								wanted.remove(j);
								j--;
							} else {
								if (newVal != null) {
									c.updatePassValue(newVal);
								}
							}
						} else if ((c.getOpperator() == Clause.Opperator.NOT_EQUAL)
								&& (object.equalsIgnoreCase(val))) {
							if (!stimulus.contains("volume")
									&& !stimulus.contains("channel")
									&& !stimulus.contains("temperature")) {
								wanted.remove(j);
								j--;
							} else if (object.equalsIgnoreCase(newVal)) {
								wanted.remove(j);
								j--;
							} else {
								if (newVal != null) {
									c.updatePassValue(newVal);
								}
							}
						}
					}
					// none of the values pass
					if (wanted.isEmpty()) {
						pass = false;
						break;
					}
				}
			}
		}

		return pass;
	}

	public void addRule(Rule rule, String device, String stimulous) {
		rules.put(device + " " + stimulous, rule);
	}

}