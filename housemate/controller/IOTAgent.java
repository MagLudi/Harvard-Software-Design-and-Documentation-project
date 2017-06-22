/**
 * Created: Oct 24, 2015
 */

package cscie97.asn4.housemate.controller;

import java.util.List;

/**
 * The interface for storing and handling ExecCommands.
 * 
 * @author anna
 *
 */
public interface IOTAgent {

	/**
	 * Add new commands to the queue
	 * 
	 * @param commands
	 */
	public void addToQueue(List<ExecCommand> commands);
}
