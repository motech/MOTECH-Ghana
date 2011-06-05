package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedEncounter;

import java.util.List;

public class ExpectedEncounterFilterChain implements FilterChain<ExpectedEncounter>{

    List<Filter<ExpectedEncounter>> filters;

    public List<ExpectedEncounter> doFilter(List<ExpectedEncounter> collection) {
        for (Filter<ExpectedEncounter> filter : filters) {
            filter.filter(collection);
        }
        return collection;
    }
}
