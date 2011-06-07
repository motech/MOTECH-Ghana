package org.motechproject.server.omod.filters;

import org.motechproject.server.model.ExpectedEvent;

import java.util.List;

public interface FilterChain<T> {

    public List<T> doFilter(List<T> collection) ;
}
