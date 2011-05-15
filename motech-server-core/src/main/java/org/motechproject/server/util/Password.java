package org.motechproject.server.util;

public class Password {
    private Integer length;

    public Password(Integer length){
         this.length = length ;
    }

    private static final char[] PASSCHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
			'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', '0' };

	public String create(){
        StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length ; i++) {
			int charIndex = (int) (Math.random() * PASSCHARS.length);
			sb.append(PASSCHARS[charIndex]);
		}
		return sb.toString();
    }
}
