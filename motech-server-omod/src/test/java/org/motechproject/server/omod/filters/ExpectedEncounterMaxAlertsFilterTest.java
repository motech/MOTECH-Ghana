package org.motechproject.server.omod.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.CareConfiguration;
import org.motechproject.server.model.DefaultedExpectedEncounterAlert;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class ExpectedEncounterMaxAlertsFilterTest {

    private ContextService contextService;
    private MotechService motechService;

    @Before
    public void setUp() {
        contextService = createMock(ContextService.class);
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

        CareConfiguration care1 = new CareConfiguration(1L, "ANC", 3);
        DefaultedExpectedEncounterAlert defaulterAlert1 = new DefaultedExpectedEncounterAlert(1L, expEnc1, care1, 2, 1);
        DefaultedExpectedEncounterAlert defaulterAlert2 = new DefaultedExpectedEncounterAlert(2L, expEnc2, care1, 3, 1);
        DefaultedExpectedEncounterAlert defaulterAlert3 = new DefaultedExpectedEncounterAlert(3L, expEnc3, care1, 4, 1);

        expect(contextService.getMotechService()).andReturn(motechService).times(3);
        expect(motechService.getDefaultedEncounterAlertFor(expEnc1)).andReturn(defaulterAlert1);
        expect(motechService.getDefaultedEncounterAlertFor(expEnc2)).andReturn(defaulterAlert2);
        expect(motechService.getDefaultedEncounterAlertFor(expEnc3)).andReturn(defaulterAlert3);

        replay(contextService, motechService);

        ExpectedEncounterMaxAlertsFilter filter = new ExpectedEncounterMaxAlertsFilter();
        filter.setContextService(contextService);
        List<ExpectedEncounter> result = filter.on(expectedEncounters);

        verify(contextService, motechService);

        assertTrue(result.size() == 1);
        assertTrue(result.contains(expEnc1));
    }
}
