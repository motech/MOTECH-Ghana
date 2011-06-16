package org.motechproject.server.omod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.PatientService;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

import static org.motechproject.server.omod.PatientIdentifierTypeEnum.*;

public class IndentifierGeneratorImpl implements IdentifierGenerator{

    private static final String IDGEN_SEQ_ID_GEN_MOTECH_ID_MANUAL_COMMENT = "MANUAL ENTRY";
    private static final String IDGEN_SEQ_ID_GEN_MOTECH_ID = "MoTeCH ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_COMMUNITY_ID = "MoTeCH Community ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_STAFF_ID = "MoTeCH Staff ID Generator";
    private static final String IDGEN_SEQ_ID_GEN_FACILITY_ID = "MoTeCH Facility ID Generator";

    @Autowired
    private ContextService contextService;

    private PatientService patientService;

    private static Log log = LogFactory.getLog(IndentifierGeneratorImpl.class);

    private String generateId(String generatorName, PatientIdentifierType identifierType) {
        String id = null;
        if (generatorName == null || identifierType == null) {
            log.error("Unable to generate ID using " + generatorName + " for " + identifierType);
            return null;
        }
        try {
            IdentifierSourceService idSourceService = contextService.getIdentifierSourceService();

            SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(generatorName, identifierType);
            id = idSourceService.generateIdentifier(idGenerator, MotechConstants.IDGEN_SEQ_ID_GEN_MOTECH_ID_GEN_COMMENT);

        } catch (Exception e) {
            log.error("Error generating " + identifierType + " using " + generatorName + " in Idgen module", e);
        }
        return id;
    }

    public void excludeIdForGenerator(User staff, String motechId) {
        PatientIdentifierType motechIdType = getPatientIdentifierTypeFor(PATIENT_IDENTIFIER_MOTECH_ID);
        try {
            IdentifierSourceService idSourceService = contextService
                    .getIdentifierSourceService();

            SequentialIdentifierGenerator idGenerator = getSeqIdGenerator(
                    IDGEN_SEQ_ID_GEN_MOTECH_ID, motechIdType);

            // Persisted only if match for source and id doesn't already exist
            LogEntry newLog = new LogEntry();
            newLog.setSource(idGenerator);
            newLog.setIdentifier(motechId);
            newLog.setDateGenerated(new Date());
            newLog.setGeneratedBy(staff);
            newLog
                    .setComment(IDGEN_SEQ_ID_GEN_MOTECH_ID_MANUAL_COMMENT);
            idSourceService.saveLogEntry(newLog);

        } catch (Exception e) {
            log.error("Error verifying Motech Id in Log of Idgen module", e);
        }
    }

    private SequentialIdentifierGenerator getSeqIdGenerator(String name,
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

    public Integer generateFacilityId() {
        return Integer.valueOf(generateId(IDGEN_SEQ_ID_GEN_FACILITY_ID,
                getPatientIdentifierTypeFor(PATIENT_IDENTIFIER_FACILITY_ID)));
    }

    public Integer generateCommunityId() {
        return Integer.valueOf(generateId(IDGEN_SEQ_ID_GEN_COMMUNITY_ID,
                getPatientIdentifierTypeFor(PATIENT_IDENTIFIER_COMMUNITY_ID)
));     }

    public String generateStaffId() {
        return generateId(IDGEN_SEQ_ID_GEN_STAFF_ID,
               getPatientIdentifierTypeFor(PATIENT_IDENTIFIER_STAFF_ID));
    }

    public String generateMotechId() {
        return generateId(IDGEN_SEQ_ID_GEN_MOTECH_ID,
                getPatientIdentifierTypeFor(PATIENT_IDENTIFIER_MOTECH_ID));
    }

    private PatientIdentifierType getPatientIdentifierTypeFor(PatientIdentifierTypeEnum type){
        return type.getIdentifierType(patientService);
    }


    public void setPatientService(PatientService patientService) {
        this.patientService = patientService ;
    }
}
