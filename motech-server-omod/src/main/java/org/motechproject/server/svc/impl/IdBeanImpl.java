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

package org.motechproject.server.svc.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechIdVerhoeffValidator;
import org.motechproject.server.omod.VerhoeffValidator;
import org.motechproject.server.svc.IdBean;
import org.motechproject.server.svc.OpenmrsBean;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;

/**
 * An implementation of the IdBean interface using services of the Idgen module.
 */
public class IdBeanImpl implements IdBean {

	private static Log log = LogFactory.getLog(IdBeanImpl.class);

	private ContextService contextService;
	private OpenmrsBean openmrsBean;

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setOpenmrsBean(OpenmrsBean openmrsBean) {
		this.openmrsBean = openmrsBean;
	}

	public String generateMotechId() {
		PatientIdentifierType motechIdType = openmrsBean
				.getMotechPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID,
				motechIdType);
	}

	public String generateStaffId() {
		PatientIdentifierType staffIdType = openmrsBean.getStaffPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_STAFF_ID,
				staffIdType);
	}

	public String generateCommunityId() {
		PatientIdentifierType communityIdType = openmrsBean
				.getCommunityPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_COMMUNITY_ID,
				communityIdType);
	}

	public String generateFacilityId() {
		PatientIdentifierType facilityIdType = openmrsBean
				.getFacilityPatientIdType();
		return generateId(MotechConstants.IDGEN_SEQ_ID_GEN_FACILITY_ID,
				facilityIdType);
	}

	protected String generateId(String generatorName,
			PatientIdentifierType identifierType) {
		String id = null;
		if (generatorName == null || identifierType == null) {
			log.error("Unable to generate ID using " + generatorName + " for "
					+ identifierType);
			return null;
		}
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
					generatorName, identifierType);
			id = idSourceService.generateIdentifier(idGenerator,
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT);

		} catch (Exception e) {
			log.error("Error generating " + identifierType + " using "
					+ generatorName + " in Idgen module", e);
		}
		return id;
	}

	public void excludeMotechId(User staff, String motechId) {
		PatientIdentifierType motechIdType = openmrsBean
				.getMotechPatientIdType();
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
					MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID, motechIdType);

			// Persisted only if match for source and id doesn't already exist
			LogEntry newLog = new LogEntry();
			newLog.setSource(idGenerator);
			newLog.setIdentifier(motechId);
			newLog.setDateGenerated(new Date());
			newLog.setGeneratedBy(staff);
			newLog
					.setComment(MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_MANUAL_COMMENT);
			idSourceService.saveLogEntry(newLog);

		} catch (Exception e) {
			log.error("Error verifying Motech Id in Log of Idgen module", e);
		}
	}

	protected SequentialIdentifierGenerator getSeqIdGenerator(String name,
			PatientIdentifierType identifierType) {

		SequentialIdentifierGenerator idGenerator = null;
		try {
			IdentifierSourceService idSourceService = contextService
					.getIdentifierSourceService();

			List<IdentifierSource> idSources = idSourceService
					.getAllIdentifierSources(false);

			for (IdentifierSource idSource : idSources) {
				if (idSource instanceof SequentialIdentifierGenerator
						&& idSource.getName().equals(name)
						&& idSource.getIdentifierType().equals(identifierType)) {
					idGenerator = (SequentialIdentifierGenerator) idSource;
					break;
				}
			}
		} catch (Exception e) {
			log.error("Error retrieving " + name + " for " + identifierType
					+ " in Idgen module", e);
		}
		return idGenerator;
	}

	public boolean isValidMotechIdCheckDigit(Integer motechId) {
		if (motechId == null) {
			return false;
		}
		String motechIdString = motechId.toString();
		MotechIdVerhoeffValidator validator = new MotechIdVerhoeffValidator();
		boolean isValid = false;
		try {
			isValid = validator.isValid(motechIdString);
		} catch (Exception e) {
		}
		return isValid;
	}

	public boolean isValidIdCheckDigit(Integer idWithCheckDigit) {
		if (idWithCheckDigit == null) {
			return false;
		}
		String idWithCheckDigitString = idWithCheckDigit.toString();
		VerhoeffValidator validator = new VerhoeffValidator();
		boolean isValid = false;
		try {
			isValid = validator.isValid(idWithCheckDigitString);
		} catch (Exception e) {
		}
		return isValid;
	}
}
