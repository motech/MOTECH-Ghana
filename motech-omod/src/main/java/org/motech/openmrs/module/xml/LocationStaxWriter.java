package org.motech.openmrs.module.xml;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/*
 * Overridden startNode and setValue in StaxWriter to add CDATA to nodes names in cdataNodeNames Set.
 */
public class LocationStaxWriter extends StaxWriter {

	private boolean cdataNode;
	private Set<String> cdataNodeNames;

	public LocationStaxWriter(QNameMap qnameMap, XMLStreamWriter out,
			boolean writeEnclosingDocument, boolean namespaceRepairingMode,
			XmlFriendlyReplacer replacer, Set<String> cdataNodeNames)
			throws XMLStreamException {
		super(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode,
				replacer);
		setCdataNodeNames(cdataNodeNames);
		cdataNode = false;
	}

	public LocationStaxWriter(QNameMap qnameMap, XMLStreamWriter out,
			boolean writeEnclosingDocument, boolean namespaceRepairingMode,
			XmlFriendlyReplacer replacer) throws XMLStreamException {
		this(qnameMap, out, writeEnclosingDocument, namespaceRepairingMode,
				replacer, new HashSet<String>());
	}

	public void setCdataNodeNames(Set<String> cdataNodeNames) {
		this.cdataNodeNames = cdataNodeNames;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void startNode(String name, Class clazz) {
		super.startNode(name, clazz);
		cdataNode = cdataNodeNames.contains(name);
	}

	@Override
	public void setValue(String text) {
		if (cdataNode) {
			try {
				getXMLStreamWriter().writeCData(text);
			} catch (XMLStreamException e) {
				throw new StreamException(e);
			}
		} else {
			super.setValue(text);
		}
	}
}
