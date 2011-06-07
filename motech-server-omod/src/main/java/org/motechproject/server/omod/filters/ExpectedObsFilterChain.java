package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedObs;

import java.util.List;

public class ExpectedObsFilterChain implements FilterChain<ExpectedObs> {
    List<Filter<ExpectedObs>> filters;

    public List<ExpectedObs> doFilter(List<ExpectedObs> collection) {
        for (Filter<ExpectedObs> filter : filters) {
            filter.on(collection);
        }
        return collection;
    }

    public void setFilters(List<Filter<ExpectedObs>> filters) {
        this.filters = filters;
    }
}
