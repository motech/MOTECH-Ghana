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

import org.motechproject.mobile.core.dao.GatewayRequestDAO;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.MStatus;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/*
 * GatewayRequestDAOImpl is the implementation class of the GatewayRequestDAO interface
 * This Class implements only GatewayRequestDAO specific persistent operation to the GatewayRequest model.
 * 
 * Date: Jul 24, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 * @author Henry Sampson (henry@dreamoval.com)
 */
public class GatewayRequestDAOImpl extends HibernateGenericDAOImpl<GatewayRequestImpl> implements GatewayRequestDAO<GatewayRequestImpl> {

    private static Logger logger = Logger.getLogger(GatewayRequestDAOImpl.class);

    public GatewayRequestDAOImpl() {
    }

    /**
     *  @see {@link org.motechproject.mobile.core.dao.GatewayRequestDAO#getByStatus}
     */
    public List<GatewayRequest> getByStatus(MStatus status) {
        logger.debug("varaible passed to getByStatus " + status);
        try {

            List<GatewayRequest> allbyStatus;
            allbyStatus = (List<GatewayRequest>) this.getSessionFactory().getCurrentSession().createCriteria(getPersistentClass()).add(Restrictions.eq("messageStatus", status)).list();

            logger.debug(allbyStatus);
            return allbyStatus;
        } catch (HibernateException he) {

            logger.error("Persistence or JDBC Exception in Method getByStatus", he);
            return null;
        } catch (Exception ex) {

            logger.error("Exception in Method getByStatus", ex);
            return null;
        }
    }

    /**
     *  @see {@link org.motechproject.mobile.core.dao.GatewayRequestDAO#getByStatusAndSchedule}
     */
    public List<GatewayRequest> getByStatusAndSchedule(MStatus status, Date schedule) {
        logger.debug("variables passed to getByStatusAndSchedule. status: " + status + "And schedule: " + schedule);

        try {

            List<GatewayRequest> allbystatandSdule;
            Criteria criteria =  this.getSessionFactory().getCurrentSession().createCriteria(getPersistentClass());
            if (schedule == null) {
                criteria = criteria.add(Restrictions.isNull("dateTo")).add(Restrictions.isNull("dateFrom")).add(Restrictions.eq("messageStatus", status));
            } else {
                criteria = criteria.add(Restrictions.eq("messageStatus", status)).add(Restrictions.or(Restrictions.isNull("dateFrom"),Restrictions.lt("dateFrom", schedule))).add(Restrictions.or(Restrictions.isNull("dateTo"),Restrictions.gt("dateTo", schedule)));
            }

            allbystatandSdule = (List<GatewayRequest>) criteria.add(Restrictions.isNotNull("gatewayRequestDetails")).list();
            logger.debug(allbystatandSdule);

            return allbystatandSdule;

        } catch (HibernateException he) {

            logger.error("Persistence or JDBC Exception in Method getByStatusAndSchedule", he);
            return null;
        } catch (Exception ex) {

            logger.error("Exception in Method getByStatusAndSchedule", ex);
            return null;
        }
    }
}
