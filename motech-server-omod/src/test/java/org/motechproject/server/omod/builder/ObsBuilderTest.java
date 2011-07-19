package org.motechproject.server.omod.builder;

import org.junit.Test;
import org.motechproject.server.builder.ObsBuilder;
import org.motechproject.server.model.ObsValueType;
import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ObsBuilderTest {

    @Test
    public void createNumericValueObs() {
        Obs obs = new ObsBuilder()
                .withValue(new Double(121)).withValueType(ObsValueType.NUMERIC).done();
        assertNotNull(obs);
        assertEquals(new Double(121), obs.getValueNumeric());
    }

    @Test
    public void createCodedValueObs() {
        Obs obs = new ObsBuilder()
                .withValue(new Concept(1)).withValueType(ObsValueType.CODED).done();
        assertNotNull(obs);
        assertEquals(new Concept(1), obs.getValueCoded());
    }

    @Test
    public void createDateValueObs() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,1999);
        Date date = calendar.getTime();
        Obs obs = new ObsBuilder()
                .withValue(date).withValueType(ObsValueType.DATE).done();
        assertNotNull(obs);
        assertEquals(date, obs.getValueDatetime());
    }

    @Test
    public void createTextValueObs() {
        Obs obs = new ObsBuilder()
                .withValue("Hi There").withValueType(ObsValueType.TEXT).done();
        assertNotNull(obs);
        assertEquals("Hi There", obs.getValueText());
    }

    @Test
    public void createBooleanValueObs() {
        Obs obs = new ObsBuilder()
                .withValue(true).withValueType(ObsValueType.BOOLEAN).done();
        assertNotNull(obs);
        assertEquals(true, obs.getValueAsBoolean());
    }
}
