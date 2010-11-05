/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.mobile.core.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 * GenericDAO interface provides common persistence methods contracts.
 * It's implemented by an abstract class that all the implementations of
 * various domain should extend.
 * It's should be also extended by domains DAO interfaces.
 *
 * Date : Jul 31, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 * @author Henry Samspon (henry@dreamoval.com)
 * 
 */
public interface GenericDAO<T> {

    /**
     * Gets all objects of type T
     *
     * @return List of entity Type T
     */
    List<T> getAll();

    /**
     * Get all object by Example
     *
     * @param exampleInstance
     * @return List of entity type T
     */
    List<T> findByExample(T exampleInstance, String... excludeProperty);

    /**
     * Gets a particular entity of type T by id
     *
     * @param id Serializable id of the entity instance to be retrieved
     * @param lock specifies if transaction lock mode should be applied
     * @return entity of type T
     */
    T getById(Serializable id);

    /**
     * Saves the entity of type T
     *
     * @param entity
     * @return entity of type T
     */
    T save(T entity);

    /**
     * Deletes entity of type T
     * 
     * @param entity
     */
    void delete(T entity);

    /**
     * @return the session
     */
    public SessionFactory getSessionFactory();

    /**
     * @param session the session to set
     */
    public void setSessionFactory(SessionFactory sessionFactory);

    T merge(T entity);
}
