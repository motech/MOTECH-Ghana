package org.motechproject.ws;

import java.io.Serializable;

/**
 * 
 * @author yoofi
 */
public class NameValuePair implements Serializable{

	private static final long serialVersionUID = 5740132600380851218L;
	
	private String name;
    private String value;

    public NameValuePair(){
        
    }
    
    public NameValuePair(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}