/**
 * Created: Sep 30, 2015
 */

package cscie97.asn4.housemate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Takes in Triples and formats them into the appropriate output. For the
 * moment, its only function is to remove Triples that are essentially
 * duplicates. However there are plans to expand its features. One such feature
 * is merging query results. e.g. if you are trying to find which people are in
 * a particular room you will have to use the house1:kitchen1 contains ? and ? is
 * a person queries and merge their results.
 * 
 * @author anna
 *
 */
public class FormatOutput {
	/**
	 * Takes in a list of Triples and filters out any duplicates
	 * 
	 * @param results
	 * @param triples
	 * @return
	 */
	public List<Triple> deconflict(Set<Triple> results, List<String> triples) {
		List<Triple> finalVersion = new ArrayList<Triple>(results);

		for (int i = 0; i < finalVersion.size(); i++) {
			Triple t = finalVersion.get(i);

			for (int j = i + 1; j < finalVersion.size(); j++) {
				Triple u = finalVersion.get(j);

				if ((t.getSubject().getIdentifier().equalsIgnoreCase(u
						.getObject().getIdentifier()))
						&& (t.getObject().getIdentifier().equalsIgnoreCase(u
								.getSubject().getIdentifier()))) {
					finalVersion.remove(u);
					j--;
				}
			}
		}

		return finalVersion;
	}
}
