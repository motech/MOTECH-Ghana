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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.motechproject.mobile.imp.serivce.oxd;

import java.util.ArrayList;
import java.util.List;

import org.fcitmuk.epihandy.DeserializationListenerAdapter;
import org.fcitmuk.epihandy.FormData;
import org.fcitmuk.epihandy.StudyData;
import org.fcitmuk.epihandy.StudyDataList;

/**
 *
 * @author Henry Sampson (henry@dreamoval.com) and Brent Atkinson
 * Date Created: Mar 3, 2010
 */
public class StudyProcessor extends DeserializationListenerAdapter {

	int numForms = 0;
	StudyDataList model;
	List<List<String>> studyFormXmlList = new ArrayList<List<String>>();

	@Override
	public void processingStudy(StudyData studyData) {
		List<String> formList = new ArrayList<String>();
		studyFormXmlList.add(formList);
	}

	@Override
	public void formProcessed(StudyData studyData, FormData formData, String xml) {
		// Get the last list (should be our study), and add xml to end of it
		int lastFormListIndex = studyFormXmlList.size() - 1;
		List<String> lastFormList = studyFormXmlList.get(lastFormListIndex);
		lastFormList.add(xml);
		numForms++;
	}

	@Override
	public void complete(StudyDataList studyDataList, List<String> xmlForms) {
		model = studyDataList;
	}

	/**
	 * Returns the total number of forms processed so far.
	 *
	 * @return
	 */
	public int getNumForms() {
		return numForms;
	}

	/**
	 * Returns the deserialized object model read off the wire.
	 *
	 * @return
	 */
	public StudyDataList getModel() {
		return model;
	}

	/**
	 * Returns 2-dimensional array indexed by study, then form
	 *
	 * @return
	 */
	public String[][] getConvertedStudies() {
		String[][] studies = new String[studyFormXmlList.size()][];
		for (int i = 0; i < studies.length; i++)
			studies[i] = studyFormXmlList.get(i).toArray(new String[] {});
		return studies;
	}
}
