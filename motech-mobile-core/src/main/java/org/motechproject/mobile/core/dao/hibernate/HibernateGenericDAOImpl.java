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


package org.motechproject.mobile.core.dao.hibernate;

import org.motechproject.mobile.core.dao.GenericDAO;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;


/*
 * HibernateGenericDAOImpl is the hibernate implementation of the genericDAO interface
 * that defines the contract of common persistence methods.
 * This class should be extended by  implementation of every domain DAO
 *
 *  Date : Jul 31, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
public abstract class HibernateGenericDAOImpl<T> implements GenericDAO<T> {

    private static Logger logger = Logger.getLogger(HibernateGenericDAOImpl.class);
    private Class<T> persistentClass;
    private SessionFactory sessionFactory;

    public HibernateGenericDAOImpl() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

        /**
     * @return the session
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @param session the session to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @return the persistentClass
     */
    public Class<T> getPersistentClass() {
        logger.info("Calling getPersistentClass");
        return persistentClass;
    }



    /**
     * Gets all objects of type T
     * @return List of entity Type T
     */
    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        logger.info("calling getAll");
        return findByCriteria();
    }

    /**
     * Perfoms a search based on a criterion or no criterion
     * @param criterion array of criterion or no criterion
     * @return List of entity of type T
     */
    protected List<T> findByCriteria(Criterion... criterion) {
        logger.info("Callint FindByCriteria");
//        Criteria crit = getDBSession().getSession().createCriteria(getPersistentClass());
        Criteria crit = this.getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * Gets a particular entity of type T by id
     * @param id id of type ID of the entity instance to be retrieved
     * @return entity of type T
     */
    @SuppressWarnings("unchecked")
    public T getById(Serializable id) {
        logger.info("Calling getById");
        T entity;
        entity = (T) this.getSessionFactory().getCurrentSession().get(getPersistentClass(), id);
        return entity;
    }

    /**
     * Saves the entity of type T
     * @param entity
     * @return entity of type T
     */
    @SuppressWarnings("unchecked")
    public T save(T entity) {
        this.getSessionFactory().getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public T merge(T entity){
        return (T)this.getSessionFactory().getCurrentSession().merge(entity);
    }

    /**
     *  Deletes entity of type T
     * @param entity
     */
    public void delete(T entity) {
        logger.info("Calling delete");
        this.getSessionFactory().getCurrentSession().delete(entity);
    }

    public void flush() {
        logger.info("Calling session fush");
         this.getSessionFactory().getCurrentSession().flush();
    }

    public void clear() {
        logger.info("Calling session clear");
         this.getSessionFactory().getCurrentSession().clear();
    }

    /**
     *  @see {@link org.motechproject.mobile.core.dao.GenericDAO#findByExample(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        logger.info("Calling findByExample");
        Criteria crit =  this.getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        if (excludeProperty.length != 0) {
            for (String exclude : excludeProperty) {
                example.excludeProperty(exclude);
            }
        }
        crit.add(example);
        return crit.list();
    }


}
