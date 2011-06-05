package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedEncounter;

import java.util.List;

public class ExpectedEncounterFilterChain implements FilterChain<ExpectedEncounter>{

    List<Filter<ExpectedEncounter>> filters;

    public List<ExpectedEncounter> doFilter(List<ExpectedEncounter> collection) {
        for (Filter<ExpectedEncounter> filter : filters) {
            filter.on(collection);
        }
        return collection;
    }

    public void setFilters(List<Filter<ExpectedEncounter>> filters) {
        this.filters = filters;
    }
}
