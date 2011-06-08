package org.motechproject.server.omod.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.CareConfiguration;
import org.motechproject.server.model.DefaultedExpectedObsAlert;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class ExpectedObsMaxAlertsFilterTest {

    private ContextService contextService;
    private MotechService motechService;


    @Before
    public void setUp() {
        contextService = createMock(ContextService.class);
        motechService = createMock(MotechService.class);
    }

    @Test
    public void filterOutExpectedEncountersForWhichMaximumPermissibleAlertsHaveBeenSent() {

        ExpectedObs expObs1 = new ExpectedObs();
        expObs1.setId(1L);
        ExpectedObs expObs2 = new ExpectedObs();
        expObs2.setId(2L);
        ExpectedObs expObs3 = new ExpectedObs();
        expObs3.setId(3L);
        ExpectedObs expObs4 = new ExpectedObs();
        expObs4.setId(4L);

        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(expObs1);
        expectedObservations.add(expObs2);
        expectedObservations.add(expObs3);
        expectedObservations.add(expObs4);

        CareConfiguration care1 = new CareConfiguration(1L, "ANC", 3);
        DefaultedExpectedObsAlert defaulterAlert1 = new DefaultedExpectedObsAlert(1L, expObs1, care1, 2, 1);
        DefaultedExpectedObsAlert defaulterAlert2 = new DefaultedExpectedObsAlert(2L, expObs2, care1, 3, 1);
        DefaultedExpectedObsAlert defaulterAlert3 = new DefaultedExpectedObsAlert(3L, expObs3, care1, 4, 1);
        DefaultedExpectedObsAlert defaulterAlert4 = new DefaultedExpectedObsAlert(4L, expObs3, care1, 0, 1);

        expect(contextService.getMotechService()).andReturn(motechService).times(4);
        expect(motechService.getDefaultedObsAlertFor(expObs1)).andReturn(defaulterAlert1);
        expect(motechService.getDefaultedObsAlertFor(expObs2)).andReturn(defaulterAlert2);
        expect(motechService.getDefaultedObsAlertFor(expObs3)).andReturn(defaulterAlert3);
        expect(motechService.getDefaultedObsAlertFor(expObs4)).andReturn(defaulterAlert4);

        replay(contextService, motechService);

        ExpectedObsMaxAlertsFilter filter = new ExpectedObsMaxAlertsFilter();
        filter.setContextService(contextService);
        List<ExpectedObs> result = filter.on(expectedObservations);

        verify(contextService, motechService);

        assertTrue(result.size() == 2);
        assertTrue(result.contains(expObs1));
        assertTrue(result.contains(expObs4));
    }

}
