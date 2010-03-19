package org.motechproject.server.openmrs.module.xml;

import java.io.Writer;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/*
 * Custom XmlFrieldlyReplace for StaxDriver to prevent underscores replaced with double underscores.
 * Using overridden createWriter in StaxDriver to return LocationXmlStaxWriter 
 * which allows CDATA for names in cdataNodeNames.
 */
public class LocationStaxDriver extends StaxDriver {

	Set<String> cdataNodeNames;

	public LocationStaxDriver(Set<String> cdataNodeNames) {
		super(new XmlFriendlyReplacer("ddd", "_"));
		setCdataNodeNames(cdataNodeNames);
	}

	public Set<String> getCdataNodeNames() {
		return cdataNodeNames;
	}

	public void setCdataNodeNames(Set<String> cdataNodeNames) {
		this.cdataNodeNames = cdataNodeNames;
	}

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		try {
			XMLStreamWriter xmlWriter = getOutputFactory()
					.createXMLStreamWriter(out);
			boolean writeEnclosingDocument = false;

			return new LocationStaxWriter(getQnameMap(), xmlWriter,
					writeEnclosingDocument, isRepairingNamespace(),
					xmlFriendlyReplacer(), cdataNodeNames);

		} catch (XMLStreamException e) {
			throw new StreamException(e);
		}
	}

}
