package org.motechproject.server.filters;

import java.util.List;

public interface FilterChain<T> {

    public List<T> doFilter(List<T> collection) ;
}
