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

package org.motechproject.mobile.model.dao.hibernate.imp;

import org.motechproject.mobile.core.dao.hibernate.HibernateGenericDAOImpl;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDefinitionDAO;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinitionImpl;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

/*
 * IncomingMessageFormDefinitionDAOImpl is the implementation class of the  interface
 * This Class implements only IncomingMessageFormDefinitionDAO specific persistent operation to the IncomingMessageFormDefinition model.
 *
 * Date: Dec 03, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 * @author Kofi Asamoah (yoofi@dreamoval.com)
 */
public class IncomingMessageFormDefinitionDAOImpl extends HibernateGenericDAOImpl<IncomingMessageFormDefinitionImpl>  implements IncomingMessageFormDefinitionDAO<IncomingMessageFormDefinitionImpl>{
    private static Logger logger = Logger.getLogger(IncomingMessageFormDefinitionDAOImpl.class);

    /**
     * Retrieve the most recent GatewayResponse Object based on the request id and the fact its status is not pending nor processing
     * @param requestId the requestId to pass
     * @return GatewayResponse object
     */
    public IncomingMessageFormDefinition getByCode(String formCode) {
        logger.debug("variable passed to getByCode: " + formCode);

        try {

        
            Criterion code = Restrictions.eq("formCode", formCode);

            IncomingMessageFormDefinition definition = (IncomingMessageFormDefinition)this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
                    .add(code)
                    .setMaxResults(1)
                    .uniqueResult();

            logger.debug(definition);

            return definition;

        } catch (HibernateException he) {

            logger.error("Persistence or JDBC Exception in getByCode", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in getByCode", ex);
            return null;
        }
    }
}
