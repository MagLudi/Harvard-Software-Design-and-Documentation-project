package cscie97.asn4.housemate.entitlement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Takes in a file name and reads and converts it into a format that can be
 * processed. There is a load file method for each type of file that could be
 * read in. At the moment there are only three such methods.
 */
public class Importer {

	/**
	 * Takes in the file name, opens it, reads in each line, and stores each
	 * line in an ArrayList of strings. When it finishes reading the file, the
	 * ArrayList gets returned.
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> loadFile(String fileName)
			throws IOException {

		ArrayList<String> commandList = new ArrayList<String>();

		Path path = Paths.get(fileName);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for (String line : lines) {
			String command = line.trim();

			if (!command.isEmpty()) {

				if (!command.contains("#")) {
					/*
					 * Check to see if there are any unusual characters hidden
					 * at the front of the string. These can occur if you obtain
					 * a file from another source and it's not compatible with
					 * the operating system
					 */
					if ((command.length() > 0) && (command.charAt(0) == 0xFEFF)) {
						command = command.replaceFirst("^\\W+", "");
					}

					/*
					 * Check for whitespace blocks. If any are found they are
					 * replaced with a single whitespace.
					 */
					command = command.replaceAll("\\s+", " ");

					commandList.add(command);
				}

			}
		}

		return commandList;
	}

	/**
	 * Read in a rule file and parses it so that rules can be generated. File
	 * must contain one stimulus, one or more clauses, and one or more actions.
	 * Returns an ArrayList<ArrayList<String>> where each ArrayList<String>
	 * represents a rule
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<ArrayList<String>> loadRuleFile(String fileName)
			throws Exception {

		ArrayList<ArrayList<String>> fullList = new ArrayList<ArrayList<String>>();

		Path path = Paths.get(fileName);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String l = line.trim();

			if (!l.isEmpty()) {
				/*
				 * Check to see if there are any unusual characters hidden at
				 * the front of the string. These can occur if you obtain a file
				 * from another source and it's not compatible with the
				 * operating system
				 */
				if ((l.length() > 0) && (l.charAt(0) == 0xFEFF)) {
					l = l.replaceFirst("^\\W+", "");
				}

				if (l.contains("Rule")) {
					ArrayList<String> r = new ArrayList<String>();

					// pulling out stimulus and marking it
					i = i + 2;
					String stim = "stim: " + lines.get(i).trim();

					// pulling out clauses and marking them
					i = i + 3;
					ArrayList<String> clause = new ArrayList<String>();
					while (!lines.get(i).contains("}")) {
						clause.add("clause: " + lines.get(i).trim());
						i++;
					}

					// pulling out actions and marking them
					i = i + 2;
					ArrayList<String> action = new ArrayList<String>();
					while (!lines.get(i).contains("}")) {
						clause.add("action: " + lines.get(i).trim());
						i++;
					}

					r.add(stim);
					r.addAll(clause);
					r.addAll(action);

					fullList.add(r);
				}

			}
		}

		return fullList;
	}

	/**
	 * Read in a rule file and parses it so that rules can be generated. File
	 * must contain a name, user name (id), password, and type (e.g. admin,
	 * adult, child). Returns an ArrayList<ArrayList<String>> where each
	 * ArrayList<String> represents a user
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<String>> loadAuthenFile(String fileName)
			throws IOException {
		ArrayList<ArrayList<String>> fullList = new ArrayList<ArrayList<String>>();

		Path path = Paths.get(fileName);
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String l = line.trim();

			if (!l.isEmpty()) {
				/*
				 * Check to see if there are any unusual characters hidden at
				 * the front of the string. These can occur if you obtain a file
				 * from another source and it's not compatible with the
				 * operating system
				 */
				if ((l.length() > 0) && (l.charAt(0) == 0xFEFF)) {
					l = l.replaceFirst("^\\W+", "");
				}

				if (l.contains("User")) {
					ArrayList<String> r = new ArrayList<String>();

					// pulling out name and marking it
					i = i + 2;
					String stim = "name: " + lines.get(i).trim();

					// pulling out id and marking it
					i = i + 3;
					String id = "id: " + lines.get(i).trim();

					// pulling out password and marking it
					i = i + 3;
					String password = "password: " + lines.get(i).trim();

					// pulling out type and marking it
					i = i + 3;
					String type = "type: " + lines.get(i).trim();

					r.add(stim);
					r.add(id);
					r.add(password);
					r.add(type);

					fullList.add(r);
				}

			}
		}

		return fullList;
	}

}