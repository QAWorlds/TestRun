package Utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import TestLib.BaseDriver;

public class RetryAnalyzer implements IRetryAnalyzer {

    int counter = 0;
    int retryLimit = Integer.parseInt(System.getProperty("RetryCount","2"));

    @Override
    public boolean retry(ITestResult result) {
        if (counter < retryLimit) {
            counter++;
            try {
                System.out.println("Closing browser before retry attempt #" + counter);
                BaseDriver.closeBrowser(); // <- closes current driver
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
