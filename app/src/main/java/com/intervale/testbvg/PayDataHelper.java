package com.intervale.testbvg;

public class PayDataHelper {

    private static PayDataHelper helper;
    private static String s;

    public static synchronized PayDataHelper getInstance() {
        if (helper == null) {
            helper = new PayDataHelper();
        }
        return helper;
    }

    public static String getS() {
        return s;
    }

    public static void setS(String s) {
        PayDataHelper.s = s;
    }
}
