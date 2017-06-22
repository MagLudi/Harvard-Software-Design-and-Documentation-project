/**
 * Created: Sep 16, 2015
 */

package cscie97.asn4.housemate.entitlement;

/**
 * Extends Exception. Used in when someone passes in a command that they don't
 * have permission to give.
 * 
 * @author anna
 *
 */
public class AccessDeniedException extends Exception {

	public AccessDeniedException() {
	}

	public AccessDeniedException(String message) {
		super(message);
	}

}
