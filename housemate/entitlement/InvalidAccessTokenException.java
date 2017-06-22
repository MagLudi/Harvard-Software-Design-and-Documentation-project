/**
 * Created: Sep 16, 2015
 */

package cscie97.asn4.housemate.entitlement;

/**
 * Extends Exception. Used when someone passes in an invalid Access Token
 * 
 * @author anna
 *
 */
public class InvalidAccessTokenException extends Exception {

	public InvalidAccessTokenException() {
	}

	public InvalidAccessTokenException(String message) {
		super(message);
	}

}
