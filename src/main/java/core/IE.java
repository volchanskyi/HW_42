package core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class IE {

    static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {

	Logger logger = Logger.getLogger("");
	logger.setLevel(Level.WARNING);

	String driverPath = "./resources/webdrivers/pc/IEDriverServer32.exe";
	String urls[] = { "http://alex.academy/exe/payment/index.html", "http://alex.academy/exe/payment/index2.html",
		"http://alex.academy/exe/payment/index3.html", "http://alex.academy/exe/payment/index4.html",
		"http://alex.academy/exe/payment/indexE.html" };
	if (!System.getProperty("os.name").contains("Windows")) {
	    throw new IllegalArgumentException("Internet Explorer is available only on Windows");
	}

	DesiredCapabilities IEDesiredCapabilities = DesiredCapabilities.internetExplorer();
	IEDesiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
		true);
	IEDesiredCapabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "");
	IEDesiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
	IEDesiredCapabilities.setJavascriptEnabled(true);
	IEDesiredCapabilities.setCapability("enablePersistentHover", false);

	System.setProperty("webdriver.ie.driver", driverPath);
	driver = new InternetExplorerDriver(IEDesiredCapabilities);
	driver.manage().window().maximize();
	System.out.println("Browser: IE");
	for (String i : urls) {
	    driver.get(i);
	   

	    String string_monthly_payment = driver.findElement(By.id("id_monthly_payment")).getText();

	    String regex = "^" + "(?:\\$)?" + "(?:\\s*)?" + "((?:\\d{1,3})(?:\\,)?(?:\\d{3})?(?:\\.)?(\\d{0,2})?)"
		    + "$";

	    Pattern p = Pattern.compile(regex);
	    Matcher m = p.matcher(string_monthly_payment);
	    m.find();

	    double monthly_payment = Double.parseDouble(m.group(1).replaceAll(",", ""));
	    double annual_payment = new BigDecimal(monthly_payment * 12).setScale(2, RoundingMode.HALF_UP)
		    .doubleValue();
	    DecimalFormat df = new DecimalFormat("0.00");
	    String f_annual_payment = df.format(annual_payment);
	    driver.findElement(By.id("id_annual_payment")).sendKeys(String.valueOf(f_annual_payment));
	    driver.findElement(By.id("id_validate_button")).submit();
	    String actual_result = driver.findElement(By.id("id_result")).getText();
	    System.out.println("+------------------------------------+");
	    System.out.println("URL: " + i);
	    System.out.println("String: \t" + string_monthly_payment);
	    System.out.println("Annual Payment: " + f_annual_payment);
	    System.out.println("Result: \t" + actual_result);

	}
	driver.quit();
    }

}
