import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class testSwoop {
    WebDriver driver;
    WebDriverWait wait;
    JavascriptExecutor js;

    @BeforeTest
    @Parameters("browser")
    public void setup(String browserName)
    {
        if(browserName.equalsIgnoreCase("chrome"))
        {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browserName.equalsIgnoreCase("IE")) {
            WebDriverManager.edgedriver().setup();
            driver= new EdgeDriver();
        }

        wait = new WebDriverWait(driver, 10);
        js = (JavascriptExecutor) driver;

        driver.manage().window().maximize();
    }

    @Test
    public void mainTest(){
        driver.get("http://swoop.ge");
        chooseMovie();
        selectPlaceAndTime();
        createAccount();
        checkForm();
    }

    public void chooseMovie()
    {
        //ფილმების სექციაში გადასვლა
        driver.findElement(By.linkText("კინო")).click();
        List<WebElement> movies = driver.findElements(By.cssSelector("div.movies-deal"));

        //პირველივე ფილმზე გადასვლა
        Actions actions = new Actions(driver);
        actions.moveToElement(movies.get(0)).perform();
        driver.findElement(By.linkText("ყიდვა")).click();
    }

    public void selectPlaceAndTime(){
        //სასურველი კინოთეატრის არჩევა/შემოწმება
        WebElement container = driver.findElement(By.xpath("//div[@class='terms-of-use hide-after-7line']"));
        js.executeScript("arguments[0].scrollIntoView();", container);

        WebElement caveaEastPoint = driver.findElement(By.linkText("კავეა ისთ ფოინთი"));
        caveaEastPoint.click();
        String selectedCinema = caveaEastPoint.getText();

        List<WebElement> dates = driver.findElements(By.xpath("//ul[@class='tabs ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all']"));
        List<WebElement> lis = dates.get(0).findElements(By.tagName("li"));

        //აქ მიბეჭდავს სწორ ელემენტს მაგრამ არ ეჭირება და მაგიტო არ ვირჩევ ბოლო ელემენტს ყველანაირი სელექტორი ვცადე მარა მემგონი სხვა პრობლემაა რაღაცა
        System.out.println(lis.get(lis.size()-1).findElement(By.cssSelector("a")).getAttribute("id"));

        String movieName = driver.findElement(By.xpath("//p[@class = 'name']")).getText();
        String xpathCinemaName = "//div[@class ='calendar-tabs ui-tabs ui-widget ui-widget-content ui-corner-all']//div[@aria-hidden='false' and @aria-expanded='true']//a/p[text()='კავეა ისთ ფოინთი']";

        List<WebElement> sessionsList = driver.findElements(By.xpath(xpathCinemaName));
        Assert.assertEquals(selectedCinema, sessionsList.get(sessionsList.size()-1).getText());
        sessionsList.get(sessionsList.size()-1).click();

        //შემოწმება ფილმის სახელით
        WebElement movieTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".right-content .content-header .movie-title")));
        Assert.assertEquals(movieName, movieTitle.getText());

        //პოპაპშუ თავისუფალი ადგილების არჩევა
        List<WebElement> freeSeat = driver.findElements(By.xpath("//div[@class='seat free']"));
        freeSeat.get(2).click();
    }

    public void createAccount()
    {
        //სარეგისტრაციო ფორმაზე გადასვლა
        WebElement regButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@class='authorization-active ui-tabs-anchor']//p[text()='რეგისტრაცია']")));
        regButton.click();
        driver.findElement(By.linkText("იურიდიული პირი")).click();

        List<WebElement> registrationForm = driver.findElements(By.xpath("//div[@class='register-content-2 ui-tabs-panel ui-widget-content ui-corner-bottom']//input"));

        //ყველა ინფუთ ველის შევსება
        for(int i=0; i<registrationForm.size()-3; i++)
        {
            registrationForm.get(i).sendKeys("1231231");
        }

        List<WebElement> dropDowns = driver.findElements(By.tagName("select"));
        List<Select> selectList = new ArrayList<Select>();

        //დროფდაუნების სელექცია
        for (WebElement selectElement : dropDowns) {
            Select select = new Select(selectElement);
            selectList.add(select);
        }
        for (int i = 1; i<selectList.size()-1; i++)
        {
            selectList.get(i).selectByIndex(1);
        }

        //დათანხმება წესებზე და პირობებზე
        driver.findElement(By.xpath("//input[@type='checkbox' and @id='IsLegalAgreedTerms']")).click();
    }

    public void checkForm()
    {
        WebElement fillBlankedText = driver.findElement(By.xpath("//h4[text()='წითლად მონიშნული ველების შევსება სავალდებულოა']"));
        js.executeScript("arguments[0].scrollIntoView()", fillBlankedText);
        Assert.assertEquals("წითლად მონიშნული ველების შევსება სავალდებულოა", fillBlankedText.getText());
    }

    @AfterTest
    public void endTest()
    {
        driver.quit();
    }
}
