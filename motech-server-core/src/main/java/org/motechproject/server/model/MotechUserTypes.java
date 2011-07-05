package org.motechproject.server.model;

import java.util.ArrayList;
import java.util.List;

public class MotechUserTypes {

    private List<MotechUserType> userTypes = new ArrayList<MotechUserType>();

    public MotechUserTypes(List<MotechUserType> userTypes) {
        this.userTypes.addAll(userTypes);
    }

    public Boolean hasTypes(Integer count) {
        return count.equals(userTypes.size());
    }

    public Boolean hasType(String type) {
        for (MotechUserType userType : userTypes) {
            if(userType.hasType(type)){
                return true;
            }
        }
        return false;
    }

    public List<String> all() {
        List<String> types = new ArrayList<String>();
        for (MotechUserType userType : userTypes) {
            types.add(userType.toString());
        }
        return types;
    }
}
