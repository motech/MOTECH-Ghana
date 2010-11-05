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

package org.motechproject.mobile.core.util;

import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * <p>Class for generating IDs </p>
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * Date Created: Aug 10, 2009
 */
public class MotechIDGenerator {

    public static final int DEFUALT_ID_LENGTH = 15;
    private static Logger logger = Logger.getLogger(MotechIDGenerator.class);
    private static final int[] NUMS = {6, 2, 9, 3, 4, 9, 1, 4, 8, 0, 5, 0, 2, 5, 6, 7, 1, 7, 3, 8};

    /**
     * <p>Generates a Long ID of length <code>length</code></p>
     *
     * @param length the length of the id to be generated
     * @return an ID of type Long with length <code>length</code>
     */
    public static Long generateID(int length) {
        logger.info("Calling generateID with specify length");
        Long result = null;

        if (length > 0) {
            StringBuilder id = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                id.append(NUMS[(int) Math.floor(Math.random() * 20)]);
            }
            result = Long.parseLong(id.toString());
        }

        return result;
    }

    /**
     * <p>Generates an ID of type Long of length 15</p>
     *
     * @return an ID of type Long
     */
    public static Long generateID() {
        logger.info("Calling Default generateID");
        return generateID(DEFUALT_ID_LENGTH);
    }


    public static String generateUUID(){
        logger.info("Calling UUID generator");
        UUID id = UUID.randomUUID();
        return id.toString();
    }
}
