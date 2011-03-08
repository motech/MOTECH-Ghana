package org.motechproject.server.model.rct;

import org.motechproject.server.model.Facility;

public class RCTFacility {

    Long id ;
    Facility facility;
    Boolean active ;

    public Facility getFacility(){
        return facility;
    }
}
