/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openmrs.Location;

import com.thoughtworks.xstream.XStream;

/*
 * Configured XStream to generate XML for JQuery JSTree plugin. 
 * Added method to converted Locations to XML String.
 */
public class LocationXStream extends XStream {

	private String locationIdPrefix;
	private String rootLocationParentId;

	public LocationXStream() {
		super(
				new LocationStaxDriver(new HashSet<String>(Arrays
						.asList("name"))));

		this.alias("root", LocationRoot.class);
		this.addImplicitCollection(LocationRoot.class, "items", "item",
				LocationItem.class);
		this.useAttributeFor(LocationItem.class, "parentId");
		this.aliasAttribute(LocationItem.class, "parentId", "parent_id");
		this.useAttributeFor(LocationItem.class, "id");

		locationIdPrefix = "location_";
		rootLocationParentId = "0";
	}

	public String getLocationIdPrefix() {
		return locationIdPrefix;
	}

	public void setLocationIdPrefix(String locationIdPrefix) {
		this.locationIdPrefix = locationIdPrefix;
	}

	public String getRootLocationParentId() {
		return rootLocationParentId;
	}

	public void setRootLocationParentId(String rootLocationParentId) {
		this.rootLocationParentId = rootLocationParentId;
	}

	/*
	 * Create xml root element with Location items for jquery jstree from
	 * Collection of Locations.
	 */
	public String toLocationHierarchyXML(Collection<Location> locations) {
		LocationRoot root = new LocationRoot();

		List<LocationItem> locationItems = new ArrayList<LocationItem>();
		for (Location location : locations) {
			locationItems.add(toXmlItem(location));
		}
		root.setItems(locationItems);

		return this.toXML(root);
	}

	/*
	 * Convert a Location into an xml item for jquery jstree. <item
	 * parent_id="0" id="location_1"><content><name><![CDATA[Location
	 * 1]]></name></content></item>
	 */
	private LocationItem toXmlItem(Location location) {
		LocationItem item = new LocationItem();
		Location parent = location.getParentLocation();
		String parentId = null;
		if (parent != null) {
			parentId = locationIdPrefix + parent.getLocationId();
		} else {
			parentId = rootLocationParentId;
		}
		item.setParentId(parentId);
		item.setId(locationIdPrefix + location.getLocationId());
		item.setContent(new LocationContent(location.getName()));
		return item;
	}

}
