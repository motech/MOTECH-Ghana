package org.motechproject.server.model.db;

import org.motechproject.server.model.MessageProcessorURL;
import java.util.List;

public interface MessageProcessorDAO {

    public MessageProcessorURL urlFor(String keyword);

    public List<MessageProcessorURL> list();

    public void update(Object obj);

    public void save(Object obj);
}
