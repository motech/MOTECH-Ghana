/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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
package org.motechproject.server.util;


import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateUtilTest {

    private DateUtil dateUtil;

    @Before
    public void setUp() {
        dateUtil = new DateUtil();
    }

    @Test
    public void isSameMonth() {
        assertTrue(dateUtil.isSameMonth(new Date(), new Date()));
    }

    @Test
    public void isNotSameMonth() {
        assertFalse(dateUtil.isSameMonth(new Date(), DateUtils.addMonths(new Date(), -1)));
    }

    @Test
    public void isSameYear() {
        assertTrue(dateUtil.isSameYear(new Date(), new Date()));
    }

    @Test
    public void isNotSameYear() {
        assertFalse(dateUtil.isSameYear(new Date(), DateUtils.addYears(new Date(), -1)));
    }

    @Test
    public void shouldReturnCalendarWithGivenTime() {
        Calendar calendar = dateUtil.getCalendarWithTime(10, 50, 20);
        assertNotNull(calendar);
        assertEquals(10, calendar.get(Calendar.HOUR_OF_DAY));
        assertEquals(50, calendar.get(Calendar.MINUTE));
        assertEquals(20, calendar.get(Calendar.SECOND));
    }

    @Test
    public void shouldCheckForYearOldDates() {
        assertFalse(dateUtil.isYearOld(new Date()));
        assertTrue(dateUtil.isYearOld(DateUtils.addYears(new Date(), -1)));
    }

}
