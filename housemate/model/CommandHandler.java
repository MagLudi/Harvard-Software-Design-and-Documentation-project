/**
 * Created: Sep 30, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cscie97.asn4.housemate.controller.ControlerHandler;
import cscie97.asn4.housemate.controller.StatusUpdate;
import cscie97.asn4.housemate.entitlement.AccessToken;
import cscie97.asn4.housemate.entitlement.Gatekeeper;
import cscie97.asn4.housemate.entitlement.User;
import cscie97.asn4.housemate.model.KnowledgeGraph;
import cscie97.asn4.housemate.model.Node;
import cscie97.asn4.housemate.model.Predicate;
import cscie97.asn4.housemate.model.Triple;

/**
 * Validates the commands that are passed in by the controller. After validating
 * the command, they are passed to the appropriate private method for execution
 * and then immediately passed to the converter method in Translator.
 * 
 * @author anna
 *
 */
public class CommandHandler {
	private Translator translator;
	private KnowledgeGraph kg;
	private FormatOutput formatter;
	private DomainModel hmm;
	private ControlerHandler ch;
	private Gatekeeper gk;

	public CommandHandler() throws Exception {
		this.translator = new Translator();
		this.formatter = new FormatOutput();
		this.kg = KnowledgeGraph.getInstance();
		this.hmm = DomainModel.getInstance();
		this.ch = ControlerHandler.getInstance();
		this.gk = Gatekeeper.getInstance();
	}

	public CommandHandler(String ruleFile) throws Exception {
		this.translator = new Translator();
		this.formatter = new FormatOutput();
		this.kg = KnowledgeGraph.getInstance();
		this.hmm = DomainModel.getInstance();
		this.ch = ControlerHandler.getInstance(ruleFile);
		this.gk = Gatekeeper.getInstance();
	}

	/**
	 * Takes in a command and checks to see what command is being used before
	 * passing it to the appropriate method. An Exception is to be thrown in the
	 * event of an invalid command.
	 * 
	 * @param authToken
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public List<Triple> validateCommand(AccessToken authToken, String command)
			throws Exception {
		// MAKE SURE TO ADD SOMETHING HERE FOR VOICE QUERIES THROUGH AVA
		List<Triple> result = null;

		if ((command.contains("ava")) && (command.contains("set"))) {
			// command given through ava. Need to log person in,
			// validate command execute command, then log them out.
			String[] l = command.split("\"");
			String[] m = l[1].split(" ");
			String voice = "--" + m[0].trim() + "--";
			User u = gk.logIn(voice);
			if (gk.visit(u.getAt(), command)) {
				if (command.contains("?")) {
					result = retrieveConfig(command);
				} else {
					controlApplianceState(u.getAt(), command);
				}
			}
			gk.logOut(u.getAt());
		} else if (gk.visit(authToken, command)) {

			if ((command.contains("define")) || (command.contains("add"))
					|| (command.contains("remove"))
					|| (command.contains("OCCUPANT_LEAVING"))) {
				updateHouseConfig(command);
			} else if (command.contains("show") || command.contains("?")) {
				result = retrieveConfig(command);
			} else if (command.contains("set")) {
				controlApplianceState(authToken, command);
			} else {
				throw new Exception("Invalid command used: " + command);
			}
		}
		return result;
	}

	/**
	 * Takes in add, define, and remove commands. These commands are the ones
	 * that update the model. Also a set command with "OCCUPANT_LEAVING" is
	 * treated as a remove command
	 * 
	 * @param command
	 */
	private void updateHouseConfig(String command) {
		List<String> triples = null;
		try {
			triples = translator.convert(command);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage() + "\n");
			return;
		}

		if (!command.contains("remove")
				&& !command.contains("OCCUPANT_LEAVING")) {
			/* adding new element/relation to the model */
			List<Triple> t = stringToTriple(triples);
			kg.importTriples(t);

			hmm.addToModel(command);

		} else {
			if (triples == null) {
				/* I'm removing an element from the model */
				String[] parts = command.split(" ");
				kg.removeNode(parts[2].trim());

				if (parts[1].equalsIgnoreCase("occupant")) {
					hmm.removeOcc(parts[2].trim());
				} else if (parts[1].equalsIgnoreCase("house")) {
					hmm.removeHome(parts[2].trim());
				} else if (parts[1].equalsIgnoreCase("room")) {
					hmm.removeRoom(parts[2].trim());
				} else {
					hmm.removeIOTDevice(parts[2].trim());
				}

			} else {
				/* I'm removing a relation from the model */
				List<Triple> t = stringToTriple(triples);

				/* Use query to retrieve the correct triples to remove */
				List<Triple> remove = new ArrayList<Triple>();
				for (int i = 0; i < t.size(); i++) {
					Set<Triple> x = kg.executeQuery(t.get(i));
					if (x != null) {
						remove.addAll(x);
					}
				}

				/* Removing the triples */
				for (int i = 0; i < remove.size(); i++) {
					Triple triple = remove.get(i);
					kg.removeTriple(triple.getSubject(), triple.getPredicate(),
							triple.getObject());
				}
				String[] parts = command.split(" ");

				/* removing home/occupant relation */
				if (!parts[2].contains(":")) {
					hmm.removeOccHouseRelation(parts[2], parts[4]);
				}
			}
		}

	}

	/**
	 * All set commands are passed here. This handles controlling the setting of
	 * an appliance.
	 * 
	 * @param command
	 * @throws Exception
	 */
	private void controlApplianceState(AccessToken authToken, String command) {
		if (!command.contains("LISTENING")) {
			List<String> triples = null;
			try {
				triples = translator.convert(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage() + "\n");
				return;
			}

			List<Triple> t = stringToTriple(triples);

			/*
			 * Use query to retrieve the triples that need to be removed due to
			 * conflict
			 */
			List<String> findTriplesString = new ArrayList<String>();
			for (int i = 0; i < t.size(); i++) {
				Triple n = t.get(i);
				String s = n.getSubject().getIdentifier() + " "
						+ n.getPredicate().getIdentifier() + " ?.";
				findTriplesString.add(s);
			}
			List<Triple> findTriples = stringToTriple(findTriplesString);

			/* get the triples to remove */
			List<Triple> remove = new ArrayList<Triple>();
			for (int i = 0; i < findTriples.size(); i++) {
				Set<Triple> x = kg.executeQuery(findTriples.get(i));
				if (x != null) {
					remove.addAll(x);
				}
			}

			/* removing old triples */
			for (int i = 0; i < remove.size(); i++) {
				Triple m = remove.get(i);
				kg.removeTriple(m.getSubject(), m.getPredicate(), m.getObject());

			}

			/* Adding the new triples */
			kg.importTriples(t);

			String[] parts = command.split(" ");

			if (parts[1].equalsIgnoreCase("appliance")) {
				try {
					// hmm.setAppliance(command);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage() + "\n");
					return;
				}
			}
		}
		generateStatusUpdate(authToken, command);

	}

	/**
	 * All get commands are passed to this private method. Uses the executeQuery
	 * method in KnowledeGraph and returns the corresponding Triples after
	 * passing them through the Processor.
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	private List<Triple> retrieveConfig(String command) {
		List<String> triples = null;
		if (command.contains("LISTENING")) {
			// occupant giving a query through ava
			// it is assumed that these queries a locations of other occupants
			String[] parts = command.split("'");
			String[] p = parts[1].split(" ");
			String s = p[2].trim().substring(0, p[2].length() - 1)
					+ " has_location ?";
			triples = new ArrayList<String>();
			triples.add(s);
		} else {
			try {
				triples = translator.convert(command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage() + "\n");
				return null;
			}
		}

		List<Triple> t = stringToTriple(triples);

		Set<Triple> result = new HashSet<Triple>();
		for (int i = 0; i < t.size(); i++) {
			Set<Triple> x = kg.executeQuery(t.get(i));
			if (x != null) {
				result.addAll(x);
			}
		}

		/* extra for configuration */
		if ((!triples.get(0).equalsIgnoreCase("? ? ?."))
				&& (command.contains("configuration"))) {
			Iterator<Triple> list = result.iterator();
			List<String> newStringTriples = new ArrayList<String>();
			while (list.hasNext()) {
				Triple next = list.next();
				if (next.getObject().getIdentifier().contains(":")) {
					newStringTriples.add(next.getObject().getIdentifier()
							+ " ? ?.");
				}
			}

			List<Triple> newTriples = stringToTriple(newStringTriples);

			for (int i = 0; i < newTriples.size(); i++) {
				Set<Triple> x = kg.executeQuery(newTriples.get(i));
				if (x != null) {
					result.addAll(x);
				}
			}
		}

		return formatter.deconflict(result, triples);
	}

	/**
	 * Converts the string triples into Triple instances.
	 * 
	 * @return
	 */
	private List<Triple> stringToTriple(List<String> triples) {
		List<Triple> t = new ArrayList<Triple>();

		for (int i = 0; i < triples.size(); i++) {
			String s = triples.get(i);

			int index1 = s.indexOf(" "); // first whitespace
			int index2 = s.lastIndexOf(" "); // second whitespace
			int index3 = s.indexOf("."); // period

			String subjectID = s.substring(0, index1);
			String predicateID = s.substring(index1 + 1, index2);
			String objectID;

			// keep period out
			if (index3 == -1) {
				objectID = s.substring(index2 + 1);
			} else {
				objectID = s.substring(index2 + 1, index3);
			}

			/* Trims here */
			subjectID = subjectID.trim();
			predicateID = predicateID.trim();
			objectID = objectID.trim();

			Node subject = kg.getNode(subjectID);
			Predicate predicate = kg.getPredicate(predicateID);
			Node object = kg.getNode(objectID);

			t.add(new Triple(s, subject, object, predicate));
		}

		return t;
	}

	/**
	 * There was a status change in a sensor/appliance. Creating a StatusUpdate
	 * instance with the details and sending it to the ControlerHandler
	 * 
	 * @param command
	 */
	private void generateStatusUpdate(AccessToken authToken, String command) {
		StatusUpdate su;

		String[] parts = command.split(" ");
		String[] p = parts[2].split(":");
		String address = p[0].trim() + ":" + p[1].trim();

		String deviceType;
		String deviceID = null;
		String stimulus;
		String val = null;

		if (command.contains("LISTENING")) {
			String[] l = command.split("'");
			String[] m = l[1].split(" ");

			if (m.length == 2) {
				if (m[0].equalsIgnoreCase("lights")) {
					m[0] = "light";
					deviceType = m[0].trim();
					stimulus = m[1].trim();
				} else {
					deviceType = m[1].trim();
					if (m[0].equalsIgnoreCase("close")) {
						stimulus = "CLOSED";
					} else if (m[0].equalsIgnoreCase("lock")) {
						stimulus = "LOCKED";
					} else {
						stimulus = "OPEN";
					}
				}
			} else {
				deviceType = m[0].trim();
				stimulus = m[1].trim() + " change";
				val = m[2].trim();
			}
		} else {
			deviceType = p[2].trim().substring(0, p[2].length() - 1);
			deviceID = parts[2].trim();

			if (parts.length == 6) {
				stimulus = parts[5].trim();
			} else if (parts[4].trim().contains("OCCUPANT")) {
				stimulus = parts[4].trim();
				val = parts[6].trim();
			} else {
				if (parts[4].trim().equalsIgnoreCase("volume")
						|| parts[4].trim().equalsIgnoreCase("channel")
						|| parts[4].trim().equalsIgnoreCase("temperature")
						|| parts[4].trim().equalsIgnoreCase("intensity")
						|| parts[4].trim().equalsIgnoreCase("TimeToCook")) {
					val = parts[6].trim();
					if ((val.equalsIgnoreCase("0"))
							&& (parts[4].trim().equalsIgnoreCase("TimeToCook"))) {
						stimulus = "TimeToCook 0";
					} else {
						stimulus = parts[4].trim() + " change";
					}
				} else {
					stimulus = parts[4].trim() + " " + parts[6].trim();
				}
			}
		}

		su = new StatusUpdate(deviceType, address, deviceID, stimulus, val);

		ch.statusUpdate(authToken, su);
	}
}
