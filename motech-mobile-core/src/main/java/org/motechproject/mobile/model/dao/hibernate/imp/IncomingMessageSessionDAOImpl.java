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
import org.motechproject.mobile.model.dao.imp.IncomingMessageSessionDAO;
import org.motechproject.mobile.core.model.IncomingMessageSession;
import org.motechproject.mobile.core.model.IncomingMessageSessionImpl;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/*
 * IncomingMessageSessionDAOImpl is the implementation class of the  interface
 * This Class implements only IncomingMessageSessionDAO specific persistent operation to the IncomingMessageSession model.
 *
 * Date: Dec 03, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageSessionDAOImpl extends HibernateGenericDAOImpl<IncomingMessageSessionImpl> implements IncomingMessageSessionDAO<IncomingMessageSessionImpl> {

    private static Logger logger = Logger.getLogger(IncomingMessageSessionImpl.class);

    public List<IncomingMessageSession> getIncomingMsgSessionByRequestedPhone(String requesterPhone) {
        
         logger.debug("varaible passed to getIncomingMsgSessionByRequestedPhone " + requesterPhone);
        try {

            List<IncomingMessageSession> allMsgSession;
            allMsgSession = (List<IncomingMessageSession>) this.getSessionFactory().getCurrentSession().createCriteria(getPersistentClass())
                    .add(Restrictions.eq("requesterPhone", requesterPhone))
                    .list();

            logger.debug(allMsgSession);
            return allMsgSession;
        } catch (HibernateException he) {

            logger.error("Persistence or JDBC Exception in Method getIncomingMsgSessionByRequestedPhone passed with the variable: " + requesterPhone , he);
            return null;
        } catch (Exception ex) {

            logger.error("Exception in Method getIncomingMsgSessionByRequestedPhone", ex);
            return null;
        }
    }
}
