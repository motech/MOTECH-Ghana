package org.motechproject.server.openmrs.module.sdsched;

import java.util.HashSet;
import java.util.Set;

/**
 * An object intended to be bound as a transaction synchronization resource.
 * This is where we do the bookkeeping on what patients need to be recomputed at
 * the end of the transaction. The synchronization will use this to determine
 * which patient schedules to compute.
 * 
 * @author batkinson
 * 
 */
public class AffectedPatients {

	final Set<Integer> affectedIds;

	public AffectedPatients() {
		affectedIds = new HashSet<Integer>();
	}

	public Set<Integer> getAffectedIds() {
		return affectedIds;
	}
}
