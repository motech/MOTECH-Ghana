package org.motechproject.server.model.ghana;

import org.motechproject.ws.CareMessageGroupingStrategy;


public class KassenaNankana extends District{
    private static final String KASSENA_NANKANA = "Kassena-Nankana";

    public KassenaNankana() {
        super(KASSENA_NANKANA);
    }

    @Override
    public CareMessageGroupingStrategy getCareMessageGroupingStrategy() {
        return CareMessageGroupingStrategy.COMMUNITY;
    }

    @Override
    public String toString() {
        return KASSENA_NANKANA;
    }
}
