package org.motechproject.server.model.db;

import org.motechproject.server.model.MessageProcessorURL;

public interface MessageProcessorDAO {

    public MessageProcessorURL urlFor(String keyword);

}
