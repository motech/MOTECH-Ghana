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

import org.motechproject.mobile.core.dao.MessageTemplateDAO;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MessageTemplate;
import org.motechproject.mobile.core.model.MessageTemplateImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import javax.persistence.NonUniqueResultException;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *  MessageTemplateDAOImpl is the implementation class of the MessageTemplateDAO interface
 * This Class implements only MessageTemplateDAO specific persistent operation to the MessageTemplate model.
 *  Date : Sep 27, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
public class MessageTemplateDAOImpl extends HibernateGenericDAOImpl<MessageTemplateImpl> implements MessageTemplateDAO<MessageTemplateImpl> {

    private static Logger logger = Logger.getLogger(MessageTemplateDAOImpl.class);

    /**
     * @see {@link org.motechproject.mobile.core.dao.MessageTemplateDAO#getTemplateByLangNotifMType(org.motechproject.mobile.core.model.Language, org.motechproject.mobile.core.model.NotificationType, org.motechproject.mobile.core.model.MessageType)  }
     */
    public MessageTemplate getTemplateByLangNotifMType(Language lang, NotificationType notif, MessageType type) {

        logger.debug("variables passed to getTemplateByLangNotifMType. language: " + lang + "And NotificationType: " + notif + "And MessageType: " + type);
        try {
           
            MessageTemplate template = (MessageTemplate) this.getSessionFactory().getCurrentSession().createCriteria(MessageTemplateImpl.class).add(Restrictions.eq("language", lang)).add(Restrictions.eq("notificationType", notif)).add(Restrictions.eq("messageType", type)).uniqueResult();

            logger.debug(template);

            return template;
        } catch (NonUniqueResultException ne) {
            logger.error("Method getTemplateByLangNotifMType returned more than one MessageTemplate object", ne);
            return null;
        } catch (HibernateException he) {
            logger.error(" Persistence or JDBC Exception in Method getTemplateByLangNotifMType", he);
            return null;
        } catch (Exception ex) {
            logger.error("Exception in Method getTemplateByLangNotifMType", ex);
            return null;
        }

    }

    /**
     * @see {@link org.motechproject.mobile.core.dao.MessageTemplateDAO#getTemplateByLangNotifMType(org.motechproject.mobile.core.model.Language, org.motechproject.mobile.core.model.NotificationType, org.motechproject.mobile.core.model.MessageType, org.motechproject.mobile.core.model.Language)   }
     */
    public MessageTemplate getTemplateByLangNotifMType(Language lang, NotificationType notif, MessageType type, Language defaultLang) {
        if (lang != null) {
            return getTemplateByLangNotifMType(lang, notif, type);

        } else {
            return getTemplateByLangNotifMType(defaultLang, notif, type);
        }
    }
}
