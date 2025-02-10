package it.bancomat.pay.consumer.utilities;

public class AppConstants {

    public static final String APP_ID = "APP_CONS";
    public static final String SERVICE_PROVIDER = "1";
    public static final String OS_NAME = "ANDROID";

    public static final int TIMEOUT = 60;
    public static final boolean MESSAGE_SIGN_ENABLED = true;
    public static final String BASE_URL = "https://app-te.vaservices.eu/sit-BcmPay-p2bMobileServer/";
    public static final boolean MESSAGE_VERIFICATION_ENABLED = true;
    public static final boolean MESSAGE_ENCRYPTION_ENABLED = true;
    public static final boolean MESSAGE_DECRYPTION_ENABLED = true;
    public static final boolean PINNING_ENABLED = true;
    public static final int [] TRUSTED_CERTIFICATES = new int[]{
            0x6dbfae00, 0xd37b9cd7, 0x3f8fb47d, 0xe65917af, 0xe0dddf, 0x42dbceac, 0x20c17c02, 0x75ee2095,      // CN=Entrust Root Certification Authority, OU="(c) 2006 Entrust, Inc.", OU=www.entrust.net/CPS is incorporated by reference, O="Entrust, Inc.", C=US
            0x76ee8590, 0x374c7154, 0x37bbca6b, 0xba6028ea, 0xdde2dc6d, 0xbbb8c3f6, 0x10e851f1, 0x1d1ab7f5,      // CN=Entrust Root Certification Authority - G2, OU="(c) 2009 Entrust, Inc. - for authorized use only", OU=See www.entrust.net/legal-terms, O="Entrust, Inc.", C=US
            0x3c5a9b8c, 0x8c8c3b64, 0xaa0a7052, 0x95d5da59, 0x534c0469, 0x8dcfc16, 0x8fa53d65, 0xb3c32d50,      // CN=app-te.vaservices.eu, O=SIA S.p.A, L=Milano, ST=Milano, C=IT
            0xf7cd08a2, 0x7aa9df09, 0x18b4df52, 0x65580cce, 0xe590cc9b, 0x5ad677f1, 0x34fc137a, 0x6d57d2e7,      // CN=Entrust Certification Authority - L1K, OU="(c) 2012 Entrust, Inc. - for authorized use only", OU=See www.entrust.net/legal-terms, O="Entrust, Inc.", C=US
    };
    public static final String SERVER_ENV = "SIT";

    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3O7pUBs1/ljmKOMEZoZy" +
            "gAXTjvdleztUnMcAQmELsf5SfNvjx4Wdac9B1arwtmevUCOnovHO2Ly09vzy1MvH" +
            "RB9okS9Wd9m3Xb1QrPnpLy9new/ivuYjprkTntOYNVwtbpNKynETzMiXwA/45SFi" +
            "tYayug8IBaIU6Yoz5Fn+JCdUfyaYscnDOdj9pJXxPiHNx9HsAeXJLhcYM0AHMH7b" +
            "7uZoeNLR1HJJxX4+QxpF7kir86wT45z/KLuShgndTy27keN+nbFw1gYMOIZmug3X" +
            "ksB7HSmGDgcGRTdlSWsQtzi8dUP3M3rMT+Um5menXDvIDOWVFULD+tigXbojoBkU" +
            "BQIDAQAB";

    public static final String PRIVATE_KEY_DEFAULT = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCxvk5lkC8zVV8/" +
            "Qn9ihYk55a3IBSyeG0DflxFzyL3wj8pjIaLjEBnoZkyEgyH2s1wCX1dhxEbf/aux" +
            "EAuqRPWhuKIhKQ+JmEofZhbo3zkem+Cx2p+qwdqm9g6FNOI2/v4JOFRm2HVLvkGp" +
            "1+8aYeqVkfSW5YalsjdOI2DXSE8MVgeEcbd0y6xn4fGCR6RpR+8WBrLWhVCSRqeh" +
            "7+1IVQO2vo2dg0FSZJfRHflgjd95E+CXMhxp369GR1nuruqrULu23xG9t0n46j5e" +
            "c2iBtBITSGFyeprseQuuKMwvUgP+rupflNKeNgTu7tpudqDXNaV9t0buLa1SnIFt" +
            "aYh+EFTZAgMBAAECggEAZG3pEg3j/ZdRWAcUvvBA10vlY005JqSzhSJwpAFbAR/d" +
            "9SE+Thn86YlT1tPZTvGNHXINh5pFia5lYoh0buLD/3q5cQ8oTlCUISWn8DCgr6DX" +
            "1uZ2rR8XkpsZHT8CSpzNoRwI5IuNyxxXMcZmvXtx9s0fbFuINvt5sFbhEwMyVv8Z" +
            "Wj0GgEZN1blWas9Nm1rng1kG6KEXjXu4sEaN4fl293ZAmvtK6WC/2BEg5K5uqYTX" +
            "VkrLbJSkVpzvXKvwwc0rV/1ohSaWhiBRN4X6mrhY1Pca0HmDE1Noxa1w45D0zTN7" +
            "IChY0kBNJjCEAO0uigiw7xECQcDQJDXZZhXmxkmMAQKBgQD3Ey8URvUHRpUHqwgH" +
            "uxsRt043gVdWDYB8R59US9Ju7k4ox/DYq2IA6suzgCv/OXYDolw+AjmPH0ouyEff" +
            "bn1i6m/GsxG+EbjYesctq/RcE/MS7avB71fJQUahtuBR0CXBn6yDm5xjhMX3k/hH" +
            "GECWC3Dmzqp/tZWt/OC7NKs0GQKBgQC4Kfs+1IpgaVtR3w/9gPHf1FOBDzBwP5c2" +
            "gsplMCu+U1jDgq6WlG3x3AOJmcK1WEXg3IqHiVgN2drvGotJlWnM4QDpYViFRnPd" +
            "/RBjl00ectBLzD939ihvUBhOmzgbByte89gKsEvYl+aH5Ff/y5mhygFYA/LUNA4S" +
            "4/M4MWA+wQKBgQCx4F/Oi18wg8IbKSi42B2e3Yc0Mqv7yfCsiKvOdl/jeTFP7tIY" +
            "SxpdaqyIaEhEkMvDxgn+on9p6K7xi0MjOlqO8hTSymyFmCCpyYrH+LoN6FbhU7aK" +
            "ApQC0jnVhuFsRRxJj3kVrtwiUZ5spVzjYjRtUYNQx4y8MLC3+Aya+kofUQKBgHjb" +
            "zC9qijlFSxHKopLKCXHLxNVAo5g2k0TJxotrdnmb9vevjmXxmMmNiqDF0Jr0CD/O" +
            "W/cgsjG808nwkJJExL8YtmFyagZkuutnBVdmVszxqjLkIo7Wc6jBVoJXqRVLujHB" +
            "Mvmows2sujRaLUoIUpVznTgmtzh9xPKut9IbzT7BAoGAOiBpra1T2CV3/8LogdTn" +
            "oyK74EKf0o384sCaYVjoytxcN4s5Fq54+7NrLWYUcfncQuiya0le+peXi2Kbcsox" +
            "CNU1A/AOapCoQJ1nv2W5Z1Bhgh/AS2QucMQbBZqRs7jVli7WvW2MTPktJk11Ze6m" +
            "mgpzdLss/xI4v4XZn7OtBcE=";

}