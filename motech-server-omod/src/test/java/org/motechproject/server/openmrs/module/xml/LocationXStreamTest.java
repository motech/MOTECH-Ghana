package org.motechproject.server.openmrs.module.xml;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.openmrs.Location;

public class LocationXStreamTest extends TestCase {

	public void testDefaultToLocationXml() {
		LocationXStream xstream = new LocationXStream();

		Location location1 = new Location(1);
		location1.setName("Location 1");

		Location location2 = new Location(2);
		location2.setName("Location 2");

		location1.addChildLocation(location2);

		List<Location> locations = new ArrayList<Location>();
		locations.add(location1);
		locations.add(location2);

		String expectedXml = "<root>"
				+ "<item parent_id=\"0\" id=\"location_1\"><content><name><![CDATA[Location 1]]></name></content></item>"
				+ "<item parent_id=\"location_1\" id=\"location_2\"><content><name><![CDATA[Location 2]]></name></content></item>"
				+ "</root>";
		String actualXml = xstream.toLocationHierarchyXML(locations);

		assertEquals(expectedXml, actualXml);
	}

}
