package cscie97.asn4.housemate.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cscie97.asn4.housemate.model.Triple;

/**
 * This class is a knowledge graph stores all the Node, Predicate, and Triple
 * instances, each in their own Map. It also contains a map of all possible
 * queries and their corresponding Triples. When a new Triple gets passed in,
 * all four of these Map instances are updated accordingly.
 * 
 * Created: Sep 6, 2015
 */

public class KnowledgeGraph {
	private static volatile KnowledgeGraph instance;

	private Map<String, Node> nodeMap;
	private Map<String, Predicate> predicateMap;
	private Map<String, Triple> tripleMap;
	private Map<String, Set<Triple>> queryMapSet;

	private KnowledgeGraph() {
		this.nodeMap = new HashMap<String, Node>();
		this.predicateMap = new HashMap<String, Predicate>();
		this.tripleMap = new HashMap<String, Triple>();
		this.queryMapSet = new HashMap<String, Set<Triple>>();
	}

	/**
	 * Uses the Lazy initialization of the singleton pattern by double checking
	 * to make sure that an instance of KnowledgeGraph doesn't already exist. If
	 * it does exist it will simply return the instance, otherwise it will
	 * create one and return that.
	 * 
	 * @return
	 */
	public static synchronized KnowledgeGraph getInstance() {
		if (instance == null) {
			instance = new KnowledgeGraph();
		}

		return instance;
	}

	/**
	 * Adds a list of Triples to the KnowledgeGraph. The following associations
	 * are updated with each new Triple: nodeMap, tripleMap, queryMapSet, and
	 * predicateMap. Measures are taken to make sure that there is only one
	 * Triple instance per unique Subject, Predicate, Object combination.
	 * 
	 * @param tripleList
	 */
	public void importTriples(List<Triple> tripleList) {
		for (int i = 0; i < tripleList.size(); i++) {
			Triple t = tripleList.get(i);
			String tripleID = t.getIdentifier();
			String subjectID = t.getSubject().getIdentifier();
			String objectID = t.getObject().getIdentifier();
			String predicateID = t.getPredicate().getIdentifier();

			/*
			 * Checks to see if Triple is already stored in tripleMap. If it
			 * receives a null then it will add it to the Map before going on to
			 * update queryMap
			 */
			Triple check = getTriple(t.getSubject(), t.getPredicate(),
					t.getObject());
			if (check == null) {
				tripleMap.put(tripleID.toLowerCase(), t);

				/*
				 * Update queryMap. Will add the Triple to all possible queries.
				 * If a query doesn't exist in the Map then it will add the new
				 * query to queryMapSet with the Triple being the first element
				 * of the new set.
				 */

				String query1 = subjectID + " ? ?.";
				String query2 = "? " + predicateID + " ?.";
				String query3 = "? ? " + objectID + ".";
				String query4 = subjectID + " " + predicateID + " ?.";
				String query5 = subjectID + " ? " + objectID + ".";
				String query6 = "? " + predicateID + " " + objectID + ".";

				updateQuery("? ? ?.", t);
				updateQuery(t.getIdentifier(), t);
				updateQuery(query1, t);
				updateQuery(query2, t);
				updateQuery(query3, t);
				updateQuery(query4, t);
				updateQuery(query5, t);
				updateQuery(query6, t);
			}
		}
	}

	/**
	 * Returns a Set of Triples from the queryMapSet based on the query. If such
	 * a query isn't in queryMapSet, then it will return a null.
	 * 
	 * @param query
	 * @return
	 */
	public Set<Triple> executeQuery(Triple query) {
		Set<Triple> result = queryMapSet.get(query.getIdentifier()
				.toLowerCase());
		return result;
	}

	/**
	 * Returns a Node instance for the given node identifier. Uses the nodeMap
	 * to look up the Node. If it does not exist, it is created and added to the
	 * nodeMap. Node names are case insensitive.
	 * 
	 * @param identifier
	 * @return
	 */
	public Node getNode(String identifier) {
		Node n = nodeMap.get(identifier.toLowerCase());
		if (n == null) {
			n = new Node(identifier);
			nodeMap.put(identifier.toLowerCase(), n);
		}
		return n;
	}

	/**
	 * Removes a Node from the KnowledgeGraph. nodeMap, tripleMap, and
	 * queryMapSet are updated since all triples and queries with that Node are
	 * no longer valid.
	 * 
	 * @param identifier
	 */
	public synchronized void removeNode(String identifier) {
		nodeMap.remove(identifier.toLowerCase());

		/*
		 * calls the removeTriple function here for the update of tripleMap and
		 * queryMapSet when it finds a triple containing the node in question
		 */

		Set<String> keySet = tripleMap.keySet();

		Iterator<String> list = keySet.iterator();
		ArrayList<Triple> remove = new ArrayList<Triple>();
		while (list.hasNext()) {
			String s = list.next();
			if (s.contains(identifier.toLowerCase())) {
				remove.add(tripleMap.get(s.toLowerCase()));
			}
		}

		for (int i = 0; i < remove.size(); i++) {
			Triple t = remove.get(i);
			removeTriple(t.getSubject(), t.getPredicate(), t.getObject());
		}

		/*
		 * final cleanup of queryMap. Here we simply remove all queries that
		 * contain the node in their id. tripleMap should by this point be fully
		 * updated.
		 */

		Set<String> querySet = queryMapSet.keySet();

		Iterator<String> list2 = querySet.iterator();
		ArrayList<String> remove2 = new ArrayList<String>();
		while (list2.hasNext()) {
			String s = list2.next();
			if (s.contains(identifier.toLowerCase())) {
				remove2.add(s);
			}
		}

		for (int i = 0; i < remove2.size(); i++) {
			queryMapSet.remove(remove2.get(i).toLowerCase());
		}
	}

	/**
	 * Returns a Predicate instance for the given identifier. Uses the
	 * predicateMap to lookup the Predicate. If it does not exist, it is created
	 * and added the predicateMap. Predicate names are case insensitive.
	 * 
	 * @param identifier
	 * @return
	 */
	public Predicate getPredicate(String identifier) {
		Predicate p = predicateMap.get(identifier.toLowerCase());
		if (p == null) {
			p = new Predicate(identifier);
			predicateMap.put(identifier.toLowerCase(), p);
		}
		return p;
	}

	/**
	 * Returns the Triple instance for the given Object, Predicate and Subject.
	 * Uses the tripleMap to lookup the Triple. If it does not exist a null is
	 * returned.
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	public Triple getTriple(Node subject, Predicate predicate, Node object) {
		String tripleID = subject.getIdentifier() + " "
				+ predicate.getIdentifier() + " " + object.getIdentifier()
				+ ".";

		return tripleMap.get(tripleID.toLowerCase());
	}

	/**
	 * Removes a Triple from the KnowledgeGraph. tripleMap, and queryMapSet are
	 * updated since it's no longer a valid Triple.
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 */
	public synchronized void removeTriple(Node subject, Predicate predicate,
			Node object) {
		String tripleID = subject.getIdentifier() + " "
				+ predicate.getIdentifier() + " " + object.getIdentifier()
				+ ".";
		Triple t = tripleMap.get(tripleID.toLowerCase());

		queryMapSet.remove(tripleID.toLowerCase());

		/* generate all possible query's that will contain the triple */
		String query1 = subject.getIdentifier() + " ? ?.";
		String query2 = "? " + predicate.getIdentifier() + " ?.";
		String query3 = "? ? " + object.getIdentifier() + ".";
		String query4 = subject.getIdentifier() + " "
				+ predicate.getIdentifier() + " ?.";
		String query5 = subject.getIdentifier() + " ? "
				+ object.getIdentifier() + ".";
		String query6 = "? " + predicate.getIdentifier() + " "
				+ object.getIdentifier() + ".";

		/* update the query map */
		removeFromQuery("? ? ?.", t);
		removeFromQuery(query1, t);
		removeFromQuery(query2, t);
		removeFromQuery(query3, t);
		removeFromQuery(query4, t);
		removeFromQuery(query5, t);
		removeFromQuery(query6, t);

		tripleMap.remove(tripleID);
	}

	/**
	 * Removes a triple from a particular query
	 * 
	 * @param key
	 * @param t
	 */
	private synchronized void removeFromQuery(String key, Triple t) {
		Set<Triple> set = queryMapSet.get(key.toLowerCase());
		if (set != null) {
			set.remove(t);
		}
	}

	/**
	 * Takes in a query in string format and checks to see if such a query is
	 * being used as a key in queryMapSet. If not then it will create a new
	 * entry in queryMapSet using that key. The triple is then stored in the Set
	 * the key points to
	 * 
	 * @param query
	 * @param value
	 */
	private synchronized void updateQuery(String query, Triple value) {
		Set<Triple> list = queryMapSet.get(query.toLowerCase());
		if (list == null) {
			list = new HashSet<Triple>();
			list.add(value);
			queryMapSet.put(query.toLowerCase(), list);
		} else {
			list.add(value);
		}
	}
}
