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

package org.motechproject.mobile.imp.serivce.oxd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is an adaptation of Daniel Kayiwa's SecurityUtil class from
 * openxdata server. Here, it is used primarily to encode user passwords for the
 * mobile client.
 * 
 * @author batkinson
 * 
 */
public class PasswordEncoderImpl implements PasswordEncoder {

	public Log log = LogFactory.getLog(PasswordEncoderImpl.class);

	public String encodePassword(String clearPass, String salt) {
		StringBuffer buf = new StringBuffer();
		buf.append(clearPass);
		buf.append(salt);
		return rawToHex(sha1Encode(buf.toString()));
	}

	/**
	 * This method will return a raw sha1 digest of the given string.
	 * 
	 * @param stringToHash
	 *            string to encode
	 * @return the SHA-1 encryption of a given string
	 */
	private byte[] sha1Encode(String stringToHash) {
		try {
			String algorithm = "SHA1";
			MessageDigest md = MessageDigest.getInstance(algorithm);
			return md.digest(stringToHash.getBytes());
		} catch (NoSuchAlgorithmException e) {
			String msg = "Unable to generate sha1 digests";
			log.error(msg, e);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * Converts a raw hex in byte[] to an encoded string.
	 * 
	 * @param b
	 *            Byte array to convert to HexString
	 * @return Hexidecimal based string
	 */

	private String rawToHex(byte[] b) {
		if (b == null || b.length < 1)
			return "";
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			s.append(Integer.toHexString(b[i] & 0xFF));
		}
		return s.toString();
	}

	public String generateSalt() {
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		buf.append(Long.toString(System.currentTimeMillis()));
		buf.append(Long.toString(random.nextLong()));
		return rawToHex(sha1Encode(buf.toString()));
	}
}
