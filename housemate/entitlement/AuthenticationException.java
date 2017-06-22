/**
 * Created: Sep 16, 2015
 */

package cscie97.asn4.housemate.entitlement;

/**
 * Extends Exception. Used when someone attempts to login with invalid information.
 * 
 * @author anna
 *
 */
public class AuthenticationException extends Exception {

	public AuthenticationException() {
	}

	public AuthenticationException(String message) {
		super(message);
	}

}
