package org.motechproject.server.omod.filters.condition;

public interface Condition<E> {

    public Boolean metBy(E e);
}
