package org.motechproject.server.model.rct;

import org.motechproject.server.model.ghana.Facility;

public class RCTFacility {

    Long id ;
    Facility facility;
    Boolean active ;

    public Facility getFacility(){
        return facility;
    }
    
    public Integer getFacilityId(){
        return facility.getFacilityId();
    }
}
