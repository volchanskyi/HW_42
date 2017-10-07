package core;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.math.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
import java.util.regex.*;

public class Chrome {

    public static void main(String[] args) throws InterruptedException {
	Logger.getLogger("").setLevel(Level.OFF);
	String urls[] = { "http://alex.academy/exe/payment/index.html", "http://alex.academy/exe/payment/index2.html",
		"http://alex.academy/exe/payment/index3.html", "http://alex.academy/exe/payment/index4.html",
		"http://alex.academy/exe/payment/indexE.html" };
	String driverPath = "";
	if (System.getProperty("os.name").toUpperCase().contains("MAC"))
	    driverPath = "./resources/webdrivers/mac/chromedriver";
	else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
	    driverPath = "./resources/webdrivers/pc/chromedriver.exe";
	else
	    throw new IllegalArgumentException("Unknown OS");

	System.setProperty("webdriver.chrome.driver", driverPath);
	System.setProperty("webdriver.chrome.silentOutput", "true");
	ChromeOptions option = new ChromeOptions();
	option.addArguments("disable-infobars");
	option.addArguments("--disable-notifications");
	if (System.getProperty("os.name").toUpperCase().contains("MAC"))
	    option.addArguments("-start-fullscreen");
	else if (System.getProperty("os.name").toUpperCase().contains("WINDOWS"))
	    option.addArguments("--start-maximized");
	else
	    throw new IllegalArgumentException("Unknown OS");
	WebDriver driver = new ChromeDriver(option);
	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	System.out.println("Browser: Chrome");
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
