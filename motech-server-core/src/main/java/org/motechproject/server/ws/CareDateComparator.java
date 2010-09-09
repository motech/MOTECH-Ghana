package org.motechproject.server.ws;

import java.util.Comparator;

import org.motechproject.ws.Care;

public class CareDateComparator implements Comparator<Care> {

	boolean nameOrdering = false;

	public CareDateComparator() {
	}

	public CareDateComparator(boolean nameOrdering) {
		this.nameOrdering = nameOrdering;
	}

	public int compare(Care care1, Care care2) {
		if (nameOrdering) {
			int nameComparison = care1.getName().compareTo(care2.getName());
			if (nameComparison != 0) {
				return nameComparison;
			}
		}
		return care1.getDate().compareTo(care2.getDate());
	}

}
