package org.motechproject.server.ws;

import java.util.Comparator;

import org.motechproject.ws.Care;

public class CareDateComparator implements Comparator<Care> {

	public int compare(Care care1, Care care2) {
		return care1.getDate().compareTo(care2.getDate());
	}

}
