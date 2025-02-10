package it.bancomatpay.sdk.manager.utilities;

public class Constants {

    public static final String APP_ID = "SDK_CONS";
    public static final String SERVICE_PROVIDER = "1";
    public static final String OS_NAME = "ANDROID";
    public static final String BASE_URL = "http://hcp2p1.cloud.reply.eu:8080/bancomatpay/";
    public static final int TIMEOUT = 60;
    public static final boolean MESSAGE_SIGN_ENABLED = true;
    public static final boolean MESSAGE_VERIFICATION_ENABLED = true;
    public static final boolean MESSAGE_ENCRYPTION_ENABLED = true;
    public static final boolean MESSAGE_DECRYPTION_ENABLED = true;
    public static final boolean PINNING_ENABLED = false;
    public static final boolean CUSTOMER_JOURNEY_ENABLED = true;
    public static final boolean BITMAP_CACHE_ENABLED = false;
    public static final boolean BANK_SERVICE_TUTORIAL_ENABLED = true;
    public static final String QR_CODE_BASE_URL = "http://p.bcmt.it/?#";
    public static final int MAX_FREQUENTS_NUMBER = 3;
    public static final int MAX_FREQUENTS_CARD = 3;
    public static final int TIMES_TO_SHOW_CASHBACK_DIALOG_DEFAULT = 4;
    public static final int [] TRUSTED_CERTIFICATES = new int[]{
            0x6dbfae00, 0xd37b9cd7, 0x3f8fb47d, 0xe65917af, 0xe0dddf, 0x42dbceac, 0x20c17c02, 0x75ee2095,      // CN=Entrust Root Certification Authority, OU="(c) 2006 Entrust, Inc.", OU=www.entrust.net/CPS is incorporated by reference, O="Entrust, Inc.", C=US
            0x76ee8590, 0x374c7154, 0x37bbca6b, 0xba6028ea, 0xdde2dc6d, 0xbbb8c3f6, 0x10e851f1, 0x1d1ab7f5,      // CN=Entrust Root Certification Authority - G2, OU="(c) 2009 Entrust, Inc. - for authorized use only", OU=See www.entrust.net/legal-terms, O="Entrust, Inc.", C=US
            0x3c5a9b8c, 0x8c8c3b64, 0xaa0a7052, 0x95d5da59, 0x534c0469, 0x8dcfc16, 0x8fa53d65, 0xb3c32d50,      // CN=app-te.vaservices.eu, O=SIA S.p.A, L=Milano, ST=Milano, C=IT
            0xf7cd08a2, 0x7aa9df09, 0x18b4df52, 0x65580cce, 0xe590cc9b, 0x5ad677f1, 0x34fc137a, 0x6d57d2e7,      // CN=Entrust Certification Authority - L1K, OU="(c) 2012 Entrust, Inc. - for authorized use only", OU=See www.entrust.net/legal-terms, O="Entrust, Inc.", C=US
    };
    public static final String SERVER_ENV = "REPLY INT";

    public static final int DISPLACEMENT = 10;

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3O7pUBs1/ljmKOMEZoZy" +
            "gAXTjvdleztUnMcAQmELsf5SfNvjx4Wdac9B1arwtmevUCOnovHO2Ly09vzy1MvH" +
            "RB9okS9Wd9m3Xb1QrPnpLy9new/ivuYjprkTntOYNVwtbpNKynETzMiXwA/45SFi" +
            "tYayug8IBaIU6Yoz5Fn+JCdUfyaYscnDOdj9pJXxPiHNx9HsAeXJLhcYM0AHMH7b" +
            "7uZoeNLR1HJJxX4+QxpF7kir86wT45z/KLuShgndTy27keN+nbFw1gYMOIZmug3X" +
            "ksB7HSmGDgcGRTdlSWsQtzi8dUP3M3rMT+Um5menXDvIDOWVFULD+tigXbojoBkU" +
            "BQIDAQAB";

    public static final String PRIVATE_KEY_DEFAULT = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCO9TM5eOigOyEM" +
            "M+sHJpMDDsav2XuXvSI0YSPPsQne+J5qA+M+Ak1ANM3F5JS4V72YIYAcfsaqFwVA" +
            "UzdJoDR3svgO7Bpiob7m7CoNShQ6hzE3Oh8F90Heww3scSRGn9yau0+rGXsqWClA" +
            "R5Q2nRajJanvfEQQhY05dNUmohWsGvuBP9d77k6kqynsJjajMZYNCadCeTajkItt" +
            "XN6irktHGKW5/Dt8ey/nemF11olZ5QlUVFbBv+hLQFLnySULHWVXQALvFXc+c9NC" +
            "yK1S3gKZGobL2IPqELbQDkNYGLGsws+BzzgeodtsfYEF1/oFlS4eEL8xYHSTt/tv" +
            "5AzQdOYhAgMBAAECggEAAXu51FbU0xTJ+v5u6dwUmmuE1D5FcgWcXT7EyFrl51Jd" +
            "EmyH5xTPCGOD3xHIGUGPzgxK8ADAvvMOMuSqJjFrWfKZcE4y1+Mlv++jYA/stc7B" +
            "S/lmaQFvxYfwlQt3fOQbvd9DrFrPVFkl1GzptMdcfMTfRoTbj/BSzs3PiF8WKnr5" +
            "N2ugy6wjGYYBjPX44ZzazjE40kUXVBdwAIi5ORxOHlQQNvXLgT9c8y3SgqFvIlxJ" +
            "yf+ZS2avGj00ZJSTfQjVq96p2Zu2itWcm7SrbnwFlvc4WhC9pCtBPahDPvzSW8DL" +
            "Mz9Z2nKf4Vryvnz8np3uE9pR/s2oOJiE2wfPEb+lIQKBgQDJeWjeop0hNMbzirYR" +
            "VjWuz2clg+5e+sk09gya6f7+6oin1mqHX8pYSYK2OOP7x4pkdim+XALjhWAQb6ko" +
            "BZQTiXDmKwXmwdAXv7HkpuSIRfYjOKMI4lrxrHPfd/1ct4j0Key/oeNiCE7NVcgx" +
            "L3qW6iky9uMNH1vUrxrATLOIwwKBgQC1paOw1HLE5/IdK2fsO8QHORtZRtuO7oOJ" +
            "rUCLUDQbLpCKTbrwtTYJ3Cu9I15P6GgkAcj6qAFYwT4jrfjPPSHmnwAotetd1W27" +
            "BB0h1lhSDA+QRH3flOz0++JVGU4Gb4WLhBnGHbGM+bSwIpwq3i3n2WbaHIDIfR3d" +
            "Imskxn+HSwKBgDo4JT/AidtgnuUdfkNAQD8Fm+7RUv6LRb1SJI5Xc+JB779gTGRW" +
            "H+inRWhWxDqrjKg/JQznFcadcgcN3Tm9cu66Vq+MWyY1d2aFdGh4ea2PWfMPmL0m" +
            "e9U+WjWHPOWf+6livBEcPNLFR9A6K6Fa8PfPLpyVj0LpfuXUu8w53d91AoGBALOw" +
            "sthXoV02UiUW7kwb51R54psTZFjli8vjTolCAvmsu6GFOVuq91UTr1UAKukDcBnG" +
            "hkmnbwWczFrBW/Oudt3MBn0/USFW46Dwdd0mS+KGOdMuXhKSIUJL9l7WlH/7THFy" +
            "cYixkRVzY5Il+rTRazoCryMutRsf4i4CQVl03203AoGAMP7QnSnPGLKkwVJcbsmm" +
            "oMHdLPv3k2fuIiLmP+mGSKEIlCEDK//XPiUB97fHjv/FcbK1ZyNSCjpFs1wJi0kp" +
            "DosAZ0TtmrJLxuS4ouVZX0EMtFMYb8O8k6YZBmK6dLrqVd4WbQHbyyxIONsg66PV" +
            "ONXXk7tAYLX8+jw6Kr+5WFE=";

}
