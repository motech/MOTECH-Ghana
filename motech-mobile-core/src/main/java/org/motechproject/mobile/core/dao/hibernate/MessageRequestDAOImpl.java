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

import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import org.motechproject.mobile.core.model.MessageRequestImpl;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

/**
 *  MessageRequestDAOImpl is the implementation class of the MessageRequestDAO interface
 * This Class implements only MessageRequestDAO specific persistent operation to the MessageRequest F model.
 *  Date : Sep 25, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
public class MessageRequestDAOImpl extends HibernateGenericDAOImpl<MessageRequestImpl> implements MessageRequestDAO<MessageRequestImpl> {

    private static Logger logger = Logger.getLogger(MessageRequestDAOImpl.class);

    public MessageRequestDAOImpl() {
    }

    /**
     * @see  {@link org.motechproject.mobile.core.dao.MessageRequestDAO#getMsgRequestByStatusAndSchedule(org.motechproject.mobile.core.model.MStatus, java.util.Date)  }
     */
    public List getMsgRequestByStatusAndSchedule(MStatus status, Date schedule) {
        logger.debug("variables passed to getMsgRequestByStatusAndSchedule. status: " + status + "And schedule: " + schedule);


        try {
           
            List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
                    .add(Restrictions.eq("status", status))
                    .add(Restrictions.lt("dateFrom", schedule))
                    .add(Restrictions.gt("dateTo", schedule))
                    .list();
            logger.debug(msgRequest);
            return msgRequest;

        } catch (HibernateException he) {
            logger.error("Persistence or JDBC Exception in Method getMsgRequestByStatusAndSchedule", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getMsgRequestByStatusAndSchedule", ex);
            return null;
        }

    }

    /**
     * @see  {@link org.motechproject.mobile.core.dao.MessageRequestDAO#getMsgRequestByStatusAndTryNumber(org.motechproject.mobile.core.model.MStatus, int)   }
     */
    public List getMsgRequestByStatusAndTryNumber(MStatus status, int tryNumber) {
   
        logger.debug("variables passed to getMsgRequestByStatusAndTryNumber.status " + status + "And tryNumber: " + tryNumber);

        try {

           
            Criterion eqStatus = Restrictions.eq("status", status);
            Criterion leTrynumb = Restrictions.le("tryNumber", tryNumber);
            LogicalExpression exp = Restrictions.and(eqStatus, leTrynumb);
            List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass()).add(exp).list();

            logger.debug(msgRequest);
            return msgRequest;
        } catch (HibernateException he) {
            logger.error("Persistence or JDBC Exception in Method getMsgRequestByStatusAndTryNumber", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getMsgRequestByStatusAndTryNumber", ex);
            return null;
        }

    }

    public List<MessageRequest> getMsgByStatus(MStatus status) {
   
        logger.debug("variable passed to getMsgRequestByStatusAndTryNumber. status: " + status);

        try {

            
            Criterion eqStatus = Restrictions.eq("status", status);
            List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
                    .add(eqStatus)
                    .list();

            logger.debug(msgRequest);
            return msgRequest;
        } catch (HibernateException he) {
            logger.error("Persistence or JDBC Exception in Method getMsgRequestByStatusAndTryNumber", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getMsgRequestByStatusAndTryNumber", ex);
            return null;
        }

    }

    public List<MessageRequest> getMsgRequestByRecipientAndStatus(
    		String recipientID, MStatus status) {

    	logger.debug("variable passed to getMsgRequestByRecipientAndStatus.  recipientID: " + recipientID + " status: " + status);

    	try {

    		
    		Criterion eqStatus = Restrictions.eq("status", status);
    		Criterion eqRecipient = Restrictions.eq("recipientId", recipientID);
    		List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
    				.add(eqRecipient)
    				.add(eqStatus)
    				.list();

    		logger.debug(msgRequest);
    		return msgRequest;
    	} catch (HibernateException he) {
    		logger.error("Persistence or JDBC Exception in Method getMsgRequestByRecipientAndStatus", he);
    		return null;
    	} catch (Exception ex) {
    		logger.error("Exception in Method getMsgRequestByStatusAndTryNumber", ex);
    		return null;

    	}
    }

	public List<MessageRequest> getMsgRequestByRecipientAndSchedule(
			String recipientID, Date schedule) {
        logger.debug("variables passed to getMsgRequestByRecipientAndSchedule. recipientID: " + recipientID + "And schedule: " + schedule);


        try {
           
            List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
                    .add(Restrictions.eq("recipientId", recipientID))
                    .add(Restrictions.lt("dateFrom", schedule))
                    .add(Restrictions.gt("dateTo", schedule))
                    .list();
            logger.debug(msgRequest);
            return msgRequest;

        } catch (HibernateException he) {
            logger.error("Persistence or JDBC Exception in Method getMsgRequestByStatusAndSchedule", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getMsgRequestByStatusAndSchedule", ex);
            return null;
        }
	}

	public List<MessageRequest> getMsgRequestByRecipientDateFromBetweenDates(
			String recipientID, Date startDate, Date endDate) {
        logger.debug("variables passed to getMsgRequestByRecipientDateFromBetweenDates. recipientID: " + recipientID + " dateFrom after " + startDate + " before " + endDate);


        try {
           
            List msgRequest = this.getSessionFactory().getCurrentSession().createCriteria(this.getPersistentClass())
                    .add(Restrictions.eq("recipientId", recipientID))
                    .add(Restrictions.gt("dateFrom", startDate))
                    .add(Restrictions.lt("dateFrom", endDate))
                    .list();
            logger.debug(msgRequest);
            return msgRequest;

        } catch (HibernateException he) {
            logger.error("Persistence or JDBC Exception in Method getMsgRequestByStatusAndSchedule", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getMsgRequestByStatusAndSchedule", ex);
            return null;
        }
	}
}
