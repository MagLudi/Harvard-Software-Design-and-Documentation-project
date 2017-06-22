/**
 * Created: Sep 30, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Takes strings in and translates them from one form to another form.
 * 
 * @author anna
 *
 */
public class Translator {
	/**
	 * Takes in a command and converts it into a list of triples that translates
	 * into that command
	 * 
	 * @param command
	 * @return
	 * @throws Exception
	 */
	public List<String> convert(String command) throws Exception {
		List<String> triples = new ArrayList<String>();

		String[] parts = command.split(" ");

		String s;

		if (command.contains("define")) {
			/* new instance and/or relation being created */
			s = parts[2].trim() + " is_a " + parts[1].trim() + ".";

			if ((!s.contains("room")) && (!s.contains("sensor"))
					&& (!s.contains("appliance"))) {
				triples.add(s);
			}

			if (parts.length == 5) {
				s = parts[2].trim() + " is_a " + parts[4].trim() + ".";
				triples.add(s);
				if(parts[1].trim().equalsIgnoreCase("occupant")){
					s = parts[2].trim() + " is_a occupant.";
					triples.add(s);
				}
			} else if (parts.length == 7) {
				s = parts[6].trim() + ":" + parts[2].trim() + " is_a "
						+ parts[4].trim() + ".";
				triples.add(s);

				s = parts[6].trim() + ":" + parts[2].trim() + " is_located "
						+ parts[6].trim() + ".";
				triples.add(s);

				s = parts[6].trim() + " contains " + parts[6].trim() + ":"
						+ parts[2].trim() + ".";
				triples.add(s);
			} else if (parts.length == 9) {
				/*
				 * only when configuring a room should the command be broken
				 * down to 9 parts
				 */

				s = parts[8].trim() + ":" + parts[2].trim() + " is_a "
						+ parts[6].trim() + ".";
				triples.add(s);

				s = parts[8].trim() + ":" + parts[2].trim()
						+ " is_located_on_floor_" + parts[4].trim() + " "
						+ parts[8].trim() + ".";
				triples.add(s);

				s = parts[8].trim() + " contains " + parts[8].trim() + ":"
						+ parts[2].trim() + ".";
				triples.add(s);
			}
		} else if (command.contains("add")) {
			/* new occupant/house relation being created */
			if (parts.length == 7) {
				s = parts[2].trim() + " is_a_" + parts[6].trim() + " "
						+ parts[4].trim() + ".";
			} else {
				s = parts[2].trim() + " is_a_resident " + parts[4].trim() + ".";
			}
			triples.add(s);
		} else if (command.contains("remove")) {
			if (parts.length == 3) {
				/* only an element is being removed */
				return null;
			} else if (parts[1].equalsIgnoreCase("occupant")) {
				/*
				 * a relation is being removed. The ? helps to make it easier to
				 * find the right triple because query will be used and there
				 * should be only one valid Triple for each Subject ? Object
				 * pairing
				 */
				s = parts[2].trim() + " ? " + parts[4].trim() + ".";
				triples.add(s);

				s = parts[4].trim() + " ? " + parts[2].trim() + ".";
				triples.add(s);
			} else {
				throw new Exception(
						"Invalid use of remove: "
								+ command
								+ ". Only use when removing an element from the model or removing an occupant/house relation.");
			}
		} else if (command.contains("set")) {
			if (command.contains("OCCUPANT_DETECTED")) {
				if(parts[6].contains("\"")){
					parts[6] = parts[6].replaceAll("\"", "");
				}
				String[] n = parts[2].split(":");
				s = parts[6].trim() + " has_location " + n[0].trim() + ":" + n[1].trim()
						+ ".";
			} else if (command.contains("OCCUPANT_LEAVING")) {
				String[] n = parts[2].split(":");
				s = parts[6].trim() + " has_location " + n[0] + ":" + n[1]
						+ ".";
			} else {
				/* changing the status of an appliance/sensor */
				if (parts.length == 6) {
					s = parts[2].trim() + " has_" + parts[4].trim()
							+ "_setting " + parts[5].trim() + ".";
				} else {
					s = parts[2].trim() + " has_" + parts[4].trim()
							+ "_setting " + parts[6].trim() + ".";
				}
			}
			triples.add(s);
		} else if (command.contains("show")) {
			/* retrieving information */
			if (command.equalsIgnoreCase("show configuration")) {
				triples.add("? ? ?.");
			} else {
				if (parts.length == 5) {
					s = parts[2].trim() + " has_" + parts[4].trim()
							+ "_setting ?.";
				} else {
					int i = parts.length;
					s = parts[i - 1].trim() + " ? ?.";
					triples.add(s);
					s = "? ? " + parts[i - 1].trim() + ".";
				}
				triples.add(s);
			}
		}

		return triples;
	}
}
