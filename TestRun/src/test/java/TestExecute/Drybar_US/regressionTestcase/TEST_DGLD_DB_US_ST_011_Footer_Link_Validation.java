package TestExecute.Drybar_US.regressionTestcase;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import TestComponent.Drybar_US.GoldDrybarusHelper2;
import TestLib.Common;
import TestLib.Login;

public class TEST_DGLD_DB_US_ST_011_Footer_Link_Validation {
	
	String datafile = "Drybar_US//GoldDrybarTestData.xlsx";
	GoldDrybarusHelper2 Drybar = new GoldDrybarusHelper2(datafile,"Footer");

	@Test(retryAnalyzer = Utilities.RetryAnalyzer.class)
	public void Validating_Footer_Links () throws Exception {

		try {
		
			Drybar.Verify_Homepage();
			Drybar.Company("Company");
			Drybar.Support("support");
			Drybar.Information("information");
			Drybar.newtab_FooterLinks("newtab_Footer");

		} catch (Exception e) {

			Assert.fail(e.getMessage(), e);
		}
	}
	
	@AfterTest
	public void clearBrowser() {
		Common.closeAll();
		

	}

	@BeforeTest
	public void startTest() throws Exception {
		System.setProperty("configFile", "Drybar_US\\config.properties");
        Login.signIn();
        Drybar.close_add();
        

	}
}
