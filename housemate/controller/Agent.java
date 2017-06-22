/**
 * Created: Oct 25, 2015
 */

package cscie97.asn4.housemate.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * A stand in class that implements IOTAgent. Simply takes in a list of
 * ExecCommands and then executes them all. In reality it would keep track of
 * when an ExecCommand is supposed to be executed by using the executionTime
 * parameter in ExecCommand and will only complete execution when the conditions
 * of that parameter is fulfilled
 * 
 * @author anna
 *
 */
public class Agent implements IOTAgent {
	List<ExecCommand> queue;

	public Agent() {
		this.queue = new ArrayList<ExecCommand>();
	}

	@Override
	public void addToQueue(List<ExecCommand> commands) {
		queue.addAll(commands);

		while (queue.size() > 0) {
			ExecCommand ec = queue.remove(0);

			try {
				ec.executeCommand();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
