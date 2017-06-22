package cscie97.asn4.housemate.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cscie97.asn4.housemate.controller.FileLogger;
import cscie97.asn4.housemate.entitlement.AccessToken;
import cscie97.asn4.housemate.entitlement.Gatekeeper;
import cscie97.asn4.housemate.entitlement.Importer;
import cscie97.asn4.housemate.entitlement.User;
import cscie97.asn4.housemate.entitlement.UserLibrary;
import cscie97.asn4.housemate.model.CommandHandler;
import cscie97.asn4.housemate.model.Triple;

/**
 * The main class where gets the file it needs to initialize KnowledgeGraph and
 * DomainModel and update/retrieve information accordingly. The file containing
 * the commands are passed in when the user executes the code.
 * 
 * Created: Oct 2, 2015
 */

public class TestDriver {
	private static CommandHandler ch;
	private static UserLibrary ul;
	private static Gatekeeper gk;

	/**
	 * Takes in an arguments The argument is the file name containing the
	 * commands.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// creating file log
		FileLogger fileLog = FileLogger.getLogger();
		ul = UserLibrary.getInstance();
		gk = Gatekeeper.getInstance();

		User u = ul.addUser("admin", "Master Controller", "alpha", "admin");

		// gk.addUsersFromFile(u.getAt(), "./src/UserList.usr");

		boolean interactive = false;
		String fileName = null;
		boolean userFile = false;
		ArrayList<String> data = null;

		if (args[0].equalsIgnoreCase("-i")) {
			interactive = true;
		} else {
			data = Importer.loadFile(args[0]);
		}

		if (args.length == 2) {
			if (args[1].contains(".rule")) {
				ch = new CommandHandler(args[1]);
			} else if (args[1].contains(".usr")) {
				fileName = args[1];
				userFile = true;
				ch = new CommandHandler();
			} else {
				data = Importer.loadFile(args[1]);
				ch = new CommandHandler();
			}
		} else if (args.length == 3) {
			if (args[1].contains(".txt")) {
				data = Importer.loadFile(args[1]);
				if (args[2].contains(".rule")) {
					ch = new CommandHandler(args[2]);
				} else {
					fileName = args[2];
					userFile = true;
					ch = new CommandHandler();
				}
			} else {
				ch = new CommandHandler(args[1]);
				fileName = args[2];
				userFile = true;
			}
		} else if (args.length == 4) {
			ch = new CommandHandler(args[2]);
			fileName = args[3];
			userFile = true;
		} else{
			ch = new CommandHandler();
		}

		if (userFile) {
			gk.logIn(u.getId(), u.getPassword());
			gk.addUsersFromFile(u.getAt(), fileName);
			gk.logOut(u.getAt());
		}

		if (data != null) {
			gk.logIn(u.getId(), u.getPassword());
			for (int i = 0; i < data.size(); i++) {
				TestDriver.newCommand(u.getAt(), data.get(i));
			}
			gk.logOut(u.getAt());
		}

		if (interactive) {
			boolean loggedIn = false;
			User user = null;
			Scanner scanner = new Scanner(System.in);

			for (int i = 0; i < 3; i++) {
				System.out.println("Please enter username and password (attempt " + (i+1)+ " of 3)");
				System.out.print("User name: ");
				String id = scanner.nextLine();
				System.out.print("Password: ");
				String password = scanner.nextLine();
				
				user = gk.logIn(id, password);
				
				if(user != null){
					loggedIn = true;
					break;
				}
			}

			if (loggedIn) {
				System.out.print("Input command (q to quit, h for help): ");
				String input = scanner.nextLine();

				while (!input.equalsIgnoreCase("q")) {
					String[] parts = input.split(" ");
					if (input.equalsIgnoreCase("h")) {
						System.out
								.println("See the file syntax.txt for a list of valid commands and their syntax\n");
					} else if ((!parts[0].equalsIgnoreCase("show"))
							&& (!parts[0].equalsIgnoreCase("remove"))
							&& (!parts[0].equalsIgnoreCase("add"))
							&& (!parts[0].equalsIgnoreCase("define"))
							&& (!parts[0].equalsIgnoreCase("set"))) {
						System.out.println("Invalid command");
						System.out
								.println("See the file syntax.txt for a list of valid commands and their syntax\n");
					} else if ((!parts[1].equalsIgnoreCase("occupant"))
							&& (!parts[1].equalsIgnoreCase("sensor"))
							&& (!parts[1].equalsIgnoreCase("house"))
							&& (!parts[1].equalsIgnoreCase("room"))
							&& (!parts[1].equalsIgnoreCase("appliance"))
							&& (!parts[1].equalsIgnoreCase("configuration"))) {
						System.out.println("Invalid syntax");
						System.out
								.println("See the file syntax.txt for a list of valid commands and their syntax\n");
					} else {
						System.out.println("Executing command\n");
						newCommand(user.getAt(), input);
					}

					System.out.print("Input command (q to quit): ");
					input = scanner.nextLine();
				}
				gk.logOut(user.getAt());
			}
		}

		fileLog.close();
		System.out.println("Good bye!");
	}

	public static void newCommand(AccessToken authToken, String command) {
		if (!command.contains("message")) {
			List<Triple> result = null;
			try {
				result = ch.validateCommand(authToken, command);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}

			if (result != null) {
				System.out.println("Data: " + command + "\n");

				if ((result.size() == 0)
						&& (command.contains("LISTENING") || command
								.contains("show"))) {
					System.out.println("Result: Unkown\n\n");
				} else {
					System.out.println("Result: ");
					for (int j = 0; j < result.size(); j++) {
						String s;

						Triple t = (Triple) result.get(j);
						s = t.getIdentifier();
						System.out.println(s);
					}
					System.out.println("\n");
				}

			}
		} else {
			System.out.println(command);
		}
	}

}
