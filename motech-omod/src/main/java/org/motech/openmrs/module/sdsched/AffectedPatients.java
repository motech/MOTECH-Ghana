package org.motech.openmrs.module.sdsched;

import java.util.HashSet;
import java.util.Set;

import org.openmrs.PatientIdentifier;

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

	final Set<PatientIdentifier> affectedIds;

	public AffectedPatients() {
		affectedIds = new HashSet<PatientIdentifier>();
	}

	public Set<PatientIdentifier> getAffectedIds() {
		return affectedIds;
	}
}
