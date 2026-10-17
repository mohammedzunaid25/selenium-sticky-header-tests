
package tests;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.Alert;

import java.time.Duration;

public class StickyHeaderTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1365, 900));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        Path website = Paths.get(
            "C:", "SoftwareTestingProject", "website", "index.html"
        );

        driver.get(website.toUri().toString());
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("stickyHeader")
        ));
    }


@Test
public void stickyHeaderRemainsAtTopAfterScrolling() {
    WebElement header = driver.findElement(By.id("stickyHeader"));
    JavascriptExecutor js = (JavascriptExecutor) driver;

    js.executeScript("window.scrollTo(0, 600);");

    wait.until(d -> ((Number) js.executeScript(
        "return window.scrollY"
    )).doubleValue() >= 500);

    double top = ((Number) js.executeScript(
        "return arguments[0].getBoundingClientRect().top;",
        header
    )).doubleValue();

    Assert.assertEquals(
        top, 0.0, 1.0,
        "Sticky header should remain at the top after scrolling."
    );
}



@Test
public void fixedHelpButtonMaintainsScreenPosition() {
    WebElement helpButton = driver.findElement(By.id("helpButton"));
    JavascriptExecutor js = (JavascriptExecutor) driver;

    double beforeX = ((Number) js.executeScript(
        "return arguments[0].getBoundingClientRect().left;",
        helpButton
    )).doubleValue();

    double beforeY = ((Number) js.executeScript(
        "return arguments[0].getBoundingClientRect().top;",
        helpButton
    )).doubleValue();

    js.executeScript("window.scrollTo(0, 600);");

    wait.until(d -> ((Number) js.executeScript(
        "return window.scrollY"
    )).doubleValue() >= 500);

    double afterX = ((Number) js.executeScript(
        "return arguments[0].getBoundingClientRect().left;",
        helpButton
    )).doubleValue();

    double afterY = ((Number) js.executeScript(
        "return arguments[0].getBoundingClientRect().top;",
        helpButton
    )).doubleValue();

    Assert.assertEquals(
        afterX, beforeX, 1.0,
        "Fixed Help button should retain its horizontal position."
    );

    Assert.assertEquals(
        afterY, beforeY, 1.0,
        "Fixed Help button should retain its vertical position."
    );
}


    @Test
    public void backToTopButtonAppearsAfterScrolling() {
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, 600);"
        );

        wait.until(d -> ((Number) ((JavascriptExecutor) d)
            .executeScript("return window.scrollY")).doubleValue() >= 500);

        WebElement backToTop = driver.findElement(By.id("backToTop"));

        wait.until(ExpectedConditions.visibilityOf(backToTop));

        Assert.assertTrue(
            backToTop.isDisplayed(),
            "Back to Top button should appear after scrolling."
        );
    }

    @Test
    public void backToTopButtonReturnsPageToTop() {
        ((JavascriptExecutor) driver).executeScript(
            "window.scrollTo(0, 600);"
        );

        wait.until(d -> ((Number) ((JavascriptExecutor) d)
            .executeScript("return window.scrollY")).doubleValue() >= 500);

        WebElement backToTop = driver.findElement(By.id("backToTop"));

        wait.until(ExpectedConditions.elementToBeClickable(backToTop)).click();

        wait.until(d -> ((Number) ((JavascriptExecutor) d)
            .executeScript("return window.scrollY")).doubleValue() == 0);

        long scrollPosition = Math.round(((Number)
            ((JavascriptExecutor) driver).executeScript(
                "return window.scrollY"
            )).doubleValue());

        Assert.assertEquals(
            scrollPosition, 0,
            "Page should return to the top after clicking the button."
        );
    }
    
    // TC-01: Verify sticky header is visible on page load
    @Test
    public void stickyHeaderIsVisibleOnPageLoad() {
        WebElement header = driver.findElement(By.id("stickyHeader"));

        Assert.assertTrue(
            header.isDisplayed(),
            "Sticky header should be visible on page load."
        );
    }

    // TC-03: Verify sticky header remains at the top during deep scrolling
    @Test
    public void stickyHeaderRemainsAtTopDuringDeepScrolling() {
        WebElement header = driver.findElement(By.id("stickyHeader"));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, 1200);");

        wait.until(d -> ((Number) js.executeScript(
            "return window.scrollY"
        )).doubleValue() >= 1000);

        double top = ((Number) js.executeScript(
            "return arguments[0].getBoundingClientRect().top;",
            header
        )).doubleValue();

        Assert.assertEquals(
            top, 0.0, 1.0,
            "Header should remain at the top during deep scrolling."
        );
    }

    // TC-04: Verify Help button is visible
    @Test
    public void helpButtonIsVisible() {
        WebElement helpButton = driver.findElement(By.id("helpButton"));

        Assert.assertTrue(
            helpButton.isDisplayed(),
            "Help button should be visible."
        );
    }

    // TC-06: Verify Help button displays an alert when clicked
    @Test
    public void helpButtonDisplaysAlert() {
        WebElement helpButton = driver.findElement(By.id("helpButton"));

        helpButton.click();

        org.openqa.selenium.Alert alert = wait.until(
            ExpectedConditions.alertIsPresent()
        );

        Assert.assertEquals(
            alert.getText(),
            "Help button works!",
            "Help button should display the expected alert."
        );

        alert.accept();
    }

    // TC-07: Verify Back to Top is initially hidden
    @Test
    public void backToTopIsInitiallyHidden() {
        WebElement backToTop = driver.findElement(By.id("backToTop"));

        Assert.assertFalse(
            backToTop.isDisplayed(),
            "Back to Top button should initially be hidden."
        );
    }

    // TC-10: Verify navigation link takes the user to Features
    @Test
    public void featuresNavigationLinkWorks() {
        driver.findElement(By.cssSelector("a[href='#features']")).click();

        wait.until(ExpectedConditions.urlContains("#features"));

        WebElement features = driver.findElement(By.id("features"));

        Assert.assertTrue(
            features.isDisplayed(),
            "Features section should be displayed after navigation."
        );

        Assert.assertTrue(
            driver.getCurrentUrl().endsWith("#features"),
            "URL should contain the Features section fragment."
        );
    }

    // TC-11: Verify sticky header has the expected stacking order
    @Test
    public void stickyHeaderHasExpectedStackingOrder() {
        WebElement header = driver.findElement(By.id("stickyHeader"));

        String zIndex = header.getCssValue("z-index");

        Assert.assertEquals(
            zIndex,
            "1000",
            "Sticky header should have z-index 1000."
        );
    }

    // TC-12: Verify fixed buttons remain inside the viewport
    @Test
    public void fixedButtonsRemainInsideViewport() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, 600);");

        wait.until(d -> ((Number) js.executeScript(
            "return window.scrollY"
        )).doubleValue() >= 500);

        WebElement helpButton = driver.findElement(By.id("helpButton"));
        WebElement backToTop = driver.findElement(By.id("backToTop"));

        wait.until(ExpectedConditions.visibilityOf(backToTop));

        for (WebElement element :
                new WebElement[] {helpButton, backToTop}) {

            double left = ((Number) js.executeScript(
                "return arguments[0].getBoundingClientRect().left;",
                element
            )).doubleValue();

            double top = ((Number) js.executeScript(
                "return arguments[0].getBoundingClientRect().top;",
                element
            )).doubleValue();

            double right = ((Number) js.executeScript(
                "return arguments[0].getBoundingClientRect().right;",
                element
            )).doubleValue();

            double bottom = ((Number) js.executeScript(
                "return arguments[0].getBoundingClientRect().bottom;",
                element
            )).doubleValue();

            double viewportWidth = ((Number) js.executeScript(
                "return window.innerWidth"
            )).doubleValue();

            double viewportHeight = ((Number) js.executeScript(
                "return window.innerHeight"
            )).doubleValue();

            Assert.assertTrue(
                left >= 0 && top >= 0 &&
                right <= viewportWidth && bottom <= viewportHeight,
                element.getAttribute("id") +
                " should remain inside the visible viewport."
            );
        }
    }


    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
