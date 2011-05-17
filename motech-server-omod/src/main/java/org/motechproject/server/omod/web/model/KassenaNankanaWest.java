package org.motechproject.server.omod.web.model;

import org.motechproject.ws.CareMessageGroupingStrategy;


public class KassenaNankanaWest extends District{
    private static final String KASSENA_NANKANA_WEST = "Kassena-Nankana West";

    public KassenaNankanaWest() {
        super(KASSENA_NANKANA_WEST);
    }

    @Override
    public CareMessageGroupingStrategy getCareMessageGroupingStrategy() {
        return CareMessageGroupingStrategy.COMMUNITY;
    }

    @Override
    public String toString() {
        return KASSENA_NANKANA_WEST;
    }
}
