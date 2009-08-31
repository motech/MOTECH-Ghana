/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dreamoval.motech.omi.service;

import java.io.Serializable;

/**
 *
 * @author Kofi A. Asamoah
 * @email yoofi@dreamoval.com
 * @date 30-Apr-2009
 */
public interface Patient extends Serializable {

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the serialNumber
     */
    String getSerialNumber();

    /**
     * @param name the name to set
     */
    void setName(String name);

    /**
     * @param serialNumber the serialNumber to set
     */
    void setSerialNumber(String serialNumber);

}
