/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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
