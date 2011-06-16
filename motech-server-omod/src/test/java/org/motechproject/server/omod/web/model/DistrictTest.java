package org.motechproject.server.omod.web.model;

import org.junit.Test;
import org.motechproject.server.model.District;
import org.motechproject.server.model.KassenaNankana;
import org.motechproject.server.model.KassenaNankanaWest;
import org.motechproject.ws.CareMessageGroupingStrategy;

import static org.junit.Assert.assertEquals;


public class DistrictTest {
    @Test
    public void shouldReturnNoneForCareMessageGroupingStrategyWhenCalledOnDefaultDistrcit(){
        assertEquals(CareMessageGroupingStrategy.NONE, new District("test").getCareMessageGroupingStrategy());
    }

    @Test
    public void shouldReturnCommunityForCareMessageGroupingStrategyWhenCalledOnKassenaNankana(){
       assertEquals(CareMessageGroupingStrategy.COMMUNITY, new KassenaNankana().getCareMessageGroupingStrategy());
    }

    @Test
    public void shouldReturnCommunityForCareMessageGroupingStrategyWhenCalledOnKassenaNankanaWest(){
       assertEquals(CareMessageGroupingStrategy.COMMUNITY, new KassenaNankanaWest().getCareMessageGroupingStrategy());
    }
}
