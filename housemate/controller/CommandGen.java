package cscie97.asn4.housemate.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cscie97.asn4.housemate.entitlement.AccessToken;
import cscie97.asn4.housemate.model.KnowledgeGraph;
import cscie97.asn4.housemate.model.Triple;

/**
 * Generates ExecCommands with certain information of a certain format.
 */
public class CommandGen {

	/**
	 * Using the StatusUpdate and the KnowledgeGraph, it generates a set of
	 * ExecCommands for the actions that need to be taken once executing the
	 * original command.
	 * 
	 * @param data
	 * @param actions
	 */
	public static List<ExecCommand> convert(AccessToken authToken, StatusUpdate data,
			List<Action> actions) {
		KnowledgeGraph kg = KnowledgeGraph.getInstance();
		List<ExecCommand> ecs = new ArrayList<ExecCommand>();

		for (int i = 0; i < actions.size(); i++) {
			String a = actions.get(i).getAction();

			// for actions that affect devices that are not the initial device
			if ((!a.contains("message"))
					&& ((data.getDeviceID() == null) || (data.getStimulus()
							.contains("OCCUPANT")))) {
				// retrieving all possible valid appliances
				Triple query;
				if (data.getStimulus().contains("OCCUPANT")) {
					query = new Triple("? is_a light.", kg.getNode("?"),
							kg.getNode("light"), kg.getPredicate("is_a"));
				} else {
					String type = data.getDeviceType();
					String q = "? is_a " + type + ".";
					query = new Triple(q, kg.getNode("?"), kg.getNode(type),
							kg.getPredicate("is_a"));
				}
				Set<Triple> result = kg.executeQuery(query);
				Iterator<Triple> it = result.iterator();

				// extracting the appliances that we want
				List<Triple> finalList = new ArrayList<Triple>();

				while (it.hasNext()) {
					Triple t = it.next();
					String s = t.getSubject().getIdentifier();

					if (s.contains(data.getAddress())) {
						finalList.add(t);
					}

				}

				// valid device(s) found
				if (!finalList.isEmpty()) {
					String[] b = a.split(" ");
					String statusType;
					String newStatus;
					String executionTime = "NA";
					String targetDevice;
					String command;
					
					if(b.length == 2){
						statusType = "mode";
						newStatus = b[1].trim();
					} else{
						statusType = b[1].trim();
						newStatus = b[2].trim();
					}

					// generating ExecCommand(s)
					for (int j = 0; j < finalList.size(); j++) {
						targetDevice = finalList.get(j).getSubject()
								.getIdentifier();

						if (statusType.equalsIgnoreCase("mode")) {
							command = "set appliance " + targetDevice
									+ " status mode " + newStatus;
						} else {
							command = "set appliance " + targetDevice
									+ " status " + statusType + " value "
									+ newStatus;
						}

						ExecCommand ec = new ExecCommand(targetDevice,
								statusType, newStatus, executionTime, command, authToken);
						ecs.add(ec);
					}
				}

			} else if ((!a.contains("message")) && (data.getDeviceID() != null)) {
				String[] b = a.split(" ");
				String statusType = b[1].trim();
				String newStatus = b[2].trim();
				String executionTime = "NA";
				String targetDevice = data.getDeviceID();
				String command;

				if (statusType.equalsIgnoreCase("mode")) {
					command = "set appliance " + targetDevice + " status mode "
							+ newStatus;
				} else {
					command = "set appliance " + targetDevice + " status "
							+ statusType + " value " + newStatus;
				}

				// generating ExecCommand
				ExecCommand ec = new ExecCommand(targetDevice, statusType,
						newStatus, executionTime, command, authToken);
				ecs.add(ec);

			} else {
				String statusType = "NA";
				String newStatus = "NA";
				String executionTime = "NA";
				String command = a;
				String targetDevice;
				if (a.contains("Store")) {
					targetDevice = "Store";
				} else if (a.contains("Emergency")) {
					targetDevice = "911";
				} else {
					targetDevice = "All Avas";
				}

				// generating ExecCommand
				ExecCommand ec = new ExecCommand(targetDevice, statusType,
						newStatus, executionTime, command, authToken);
				ecs.add(ec);
			}
		}

		System.out.print("\n"); // sticking this here so that there is a gap
								// following the new generation of commands.
								// Makes things easier to read especially if
								// normal output will be following after
		return ecs;
	}

}