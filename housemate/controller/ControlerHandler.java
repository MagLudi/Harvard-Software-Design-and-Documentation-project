package cscie97.asn4.housemate.controller;

import java.util.ArrayList;
import java.util.List;

import cscie97.asn4.housemate.controller.StatusUpdate;
import cscie97.asn4.housemate.entitlement.AccessToken;

/**
 * The main interface for the Controller. All data/commands must be passed
 * through here whether it comes from a sensor, the command line, or a data
 * file. Is designed with the command pattern in mind.
 */
public class ControlerHandler {
	private static volatile ControlerHandler instance;

	private RuleEngine re;
	private IOTAgent agent;

	private ControlerHandler() throws Exception {
		this.re = RuleEngine.getInstance();
		this.agent = new Agent();
	}

	private ControlerHandler(String fileName) throws Exception {
		this.re = RuleEngine.getInstance(fileName);
		this.agent = new Agent();
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
	public static synchronized ControlerHandler getInstance() throws Exception {
		if (instance == null) {
			instance = new ControlerHandler();
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
	public static synchronized ControlerHandler getInstance(String fileName)
			throws Exception {
		if (instance == null) {
			instance = new ControlerHandler(fileName);
		}

		return instance;
	}

	/**
	 * Analyzes the StatusUpdate. It goes to the RuleEngine to see if there are
	 * additional steps required. Any actions retrieved are passed to CommandGen
	 * to create new executable commands
	 * 
	 * @param su
	 */
	public void statusUpdate(AccessToken authToken, StatusUpdate su) {
		List<Action> actions;

		if (su.getDeviceID() == null) {
			// occupant giving a command

			actions = new ArrayList<Action>();

			String stim = su.getStimulus();
			String deviceType = su.getDeviceType();

			actions.add(new Action(deviceType + " " + stim.toLowerCase()));
			
		} else {
			actions = re.evaluateStimulus(su);
		}

		// there are valid actions to take
		if ((actions != null)
				&& (!actions.get(0).getAction().equalsIgnoreCase("none"))) {
			List<ExecCommand> commands = CommandGen.convert(authToken, su, actions);

			if (commands.size() > 0) {
				agent.addToQueue(commands);
			}
		}
	}

}