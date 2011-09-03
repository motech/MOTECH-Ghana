package org.motechproject.server.filters.condition;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.ExpectedObs;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TT1ConditionTest {
    private TT1Condition condition;

    @Before
    public void setUp() {
        condition = new TT1Condition();
    }

    @Test
    public void shouldReturnFalseForNonTT1() {
        ExpectedObs obs = new ExpectedObs();
        obs.setName("TT2");
        assertFalse(condition.metBy(obs));
    }

    @Test
    public void shouldReturnTrueForTT1AndDateGapMoreThanAYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 2, 1);
        ExpectedObs obs = new ExpectedObs();
        obs.setName("TT1");
        obs.setLateObsDatetime(cal.getTime());
        assertTrue(condition.metBy(obs));
    }

    @Test
    public void shouldReturnFalseForTT1AndDateGapWithinAYear() {
        ExpectedObs obs = new ExpectedObs();
        obs.setName("TT1");
        obs.setLateObsDatetime(new Date());
        assertFalse(condition.metBy(obs));
    }

    @Test
    public void shouldReturnTrueForTT1AndNullObsDate() {
        ExpectedObs obs = new ExpectedObs();
        obs.setName("TT1");
        assertTrue(condition.metBy(obs));
    }
}
