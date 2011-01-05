/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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
