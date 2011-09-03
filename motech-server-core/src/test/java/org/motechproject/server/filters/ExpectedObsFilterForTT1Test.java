package org.motechproject.server.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.filters.condition.Condition;
import org.motechproject.server.filters.condition.TT1Condition;
import org.motechproject.server.model.ExpectedObs;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpectedObsFilterForTT1Test {
    private ExpectedObsFilterForTT1 filter;

    @Before
    public void setUp() {
        filter = new ExpectedObsFilterForTT1();
    }

    @Test
    public void shouldRemoveBasedOnCondition() {
        Condition condition = createMock(TT1Condition.class);
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setId(1l);
        ExpectedObs obs2 = new ExpectedObs();
        obs2.setId(2l);
        expect(condition.metBy(obs1)).andReturn(false);
        expect(condition.metBy(obs2)).andReturn(true);
        replay(condition);
        filter.setCondition(condition);

        List<ExpectedObs> inputObs = new ArrayList<ExpectedObs>();
        inputObs.add(obs1);
        inputObs.add(obs2);

        List<ExpectedObs> filteredObs = filter.on(inputObs);
        assertTrue(filteredObs.contains(obs1));
        assertFalse(filteredObs.contains(obs2));
    }
}
