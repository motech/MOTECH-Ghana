package org.motechproject.server.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class MotechConfiguration {
    private static Log log = LogFactory.getLog(MotechConfiguration.class);
    private Long id;
    private String name;
    private String value;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date asDate() {
        DateFormat formatter = new SimpleDateFormat("dd-M-yyyy");
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            log.error("Error while parsing date:" + e.getStackTrace());
        }
        return null;
    }
}
