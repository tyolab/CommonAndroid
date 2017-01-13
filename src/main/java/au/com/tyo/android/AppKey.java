package au.com.tyo.android;


public class AppKey {
    // You must use the public key belonging to your publisher account
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqsWMGcLFeUEXq2rke2q/1rPa0OoJCty+QsHtRvS8LPyKvhloNpXsW3x2XIcy8swUQqSLW0b2lB7zrwDaDg5wQdGdTPRX1bKKebEJskWYx6mBA2NwDE7jxJHiZP8pWggPWNOpj+1WuZ0I6lI+lf550VK+PdatopICQMwJkrwjQTrh/ckUh2H8/Lxqd7oyzBREU0id8Fg0I7ys19gfKHoMAtiHSX/I84h9BAt6eIUXLzLbb0DvCRJ53nNcTOw7VUL2lt0FpnZWe1rsjiZTwKetHA/2ySpe6rGtfNcoE3M6/FAs7UzW9mpKVKIh2WDFhxwXITmbt+LYLU70B37yPXcQIQIDAQAB";
    
    private static String publicKey = BASE64_PUBLIC_KEY;
    
    public static void setPublicKey(String key) {
    	AppKey.publicKey = key;
    }
    
    // You should also modify this salt
    public static final byte[] SALT = new byte[] { 1, 62, -12, 11, 54, -98,
            -10, -112, -43, 2, -83, -6, 9, 5, -16, -17, -13, 45, -87, 4
    };

	public static String getPublicKey() {
		return publicKey;
	}

	public static byte[] getSALT() {
		return SALT;
	}
}
