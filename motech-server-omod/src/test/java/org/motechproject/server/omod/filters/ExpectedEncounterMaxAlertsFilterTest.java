package org.motechproject.server.omod.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.CareConfiguration;
import org.motechproject.server.model.DefaultedExpectedEncounterAlert;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class ExpectedEncounterMaxAlertsFilterTest {
    private MotechService motechService;


    @Before
    public void setUp(){
        motechService = createMock(MotechService.class);
    }

    @Test
    public void filterOutExpectedEncountersForWhichMaximumPermissibleAlertsHaveBeenSent() {

        ExpectedEncounter expEnc1 = new ExpectedEncounter();
        expEnc1.setId(1L);
        ExpectedEncounter expEnc2 = new ExpectedEncounter();
        expEnc2.setId(2L);
        ExpectedEncounter expEnc3 = new ExpectedEncounter();
        expEnc3.setId(3L);

        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(expEnc1);
        expectedEncounters.add(expEnc2);
        expectedEncounters.add(expEnc3);

        CareConfiguration care1 = new CareConfiguration(1L,"ANC",3);
        DefaultedExpectedEncounterAlert defaulterAlert1 = new DefaultedExpectedEncounterAlert(1L,expEnc1, care1,2);
        DefaultedExpectedEncounterAlert defaulterAlert2 = new DefaultedExpectedEncounterAlert(2L,expEnc2,care1,3);
        DefaultedExpectedEncounterAlert defaulterAlert3 = new DefaultedExpectedEncounterAlert(3L,expEnc3,care1,4);

        expect(motechService.getDefaultedEncounterAlertFor(expEnc1)).andReturn(defaulterAlert1);
        expect(motechService.getDefaultedEncounterAlertFor(expEnc2)).andReturn(defaulterAlert2);
        expect(motechService.getDefaultedEncounterAlertFor(expEnc3)).andReturn(defaulterAlert3);

        replay(motechService);

        ExpectedEncounterMaxAlertsFilter filter = new ExpectedEncounterMaxAlertsFilter();
        filter.setMotechService(motechService);
        List<ExpectedEncounter> result = filter.filter(expectedEncounters);

        verify(motechService);

        assertTrue(result.size() == 1);
        assertTrue(result.contains(expEnc1));
    }
}
