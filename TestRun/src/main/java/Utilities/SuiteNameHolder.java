package Utilities;

public class SuiteNameHolder {

    private static String suiteName;

    public static void setSuiteName(String name) {
        suiteName = name;
    }

    public static String getSuiteName() {
        return suiteName != null ? suiteName : "DEFAULT";
    }
}
