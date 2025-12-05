package com.guiao5.guiao5;

import org.junit.jupiter.api.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MainPageTest {
    private WebDriver driver;
    private MainPage mainPage;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.jetbrains.com/");

        CookieHelper.closeCookiesIfVisible(driver, wait);
        mainPage = new MainPage(driver);

        PageFactory.initElements(driver, mainPage);




    }

    private void acceptCookiesIfPresent() {
        try {
            if (mainPage.acceptCookiesButton.isDisplayed()) {
                mainPage.acceptCookiesButton.click();
                System.out.println("Cookies aceites com sucesso.");
                wait.until(ExpectedConditions.invisibilityOf(mainPage.acceptCookiesButton));
                sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Info: Cookies já aceites ou banner não apareceu.");
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
        public void search() {
            // open search control
            waitUntilClickableAndClick(mainPage.searchButton);

            // locate the search input using several possible selectors (inspired by Selenide helper)
            WebElement input = SearchHelper.locateSearchInput(driver, wait);
            final String term = "Selenium";
            input.clear();
            input.sendKeys(term);

            // click submit / full search button
            WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[data-test='full-search-button']")));
            waitUntilClickableAndClick(submit);

            // validate navigation: either input retains value on the search results page or URL contains expected param
            // prefer checking URL contains s=full (like Selenide example), fallback to asserting input value
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl != null && currentUrl.contains("s=full")) {
                // good — search route happened
                assertTrue(currentUrl.contains("s=full"));
            } else {
                // fallback: check the search input on results page
                WebElement resultInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[data-test='search-input']")));
                assertEquals(term, resultInput.getAttribute("value"));
            }
        }




    @Test
    public void toolsMenu() {
        // Clicar no menu
        mainPage.toolsMenu.click();

        // CORREÇÃO:
        // Em vez de validar o "contentor" do menu (que falhava),
        // validamos se um item LÁ DENTRO (ex: IntelliJ IDEA) ficou visível.
        // Se o IntelliJ IDEA aparecer, é porque o menu abriu.
        WebElement menuItem = wait.until(ExpectedConditions.visibilityOf(mainPage.menuIntellijLink));

        assertTrue(menuItem.isDisplayed(), "O menu devia ter aberto e mostrado 'IntelliJ IDEA'");
    }

    @Test
    public void navigationToAllTools() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("arguments[0].click();", mainPage.findYourToolsButton);

        WebElement productsList = driver.findElement(By.id("products-page"));
        assertTrue(productsList.isDisplayed());
        assertTrue(driver.getTitle().contains("Tools") || driver.getTitle().contains("Products"));
    }

    private void waitUntilClickableAndClick(WebElement el) {
        wait.until(ExpectedConditions.visibilityOf(el));
        wait.until(ExpectedConditions.elementToBeClickable(el));
        try {
            el.click();
        } catch (ElementClickInterceptedException e) {
            // try JS click if intercepted
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        } catch (WebDriverException wde) {
            // as a last resort try Actions click
            try {
                new Actions(driver).moveToElement(el).click().perform();
            } catch (WebDriverException ignored) {
            }
        }
    }
    static class SearchHelper {
        private static final String[] POSSIBLE = {
                "[data-test='search-input']",
                "[data-test='site-header-search-input']",
                "#header-search",
                "input[type='search']",
                "input[placeholder*='Search']",
                "input[placeholder*='search']"
        };

        static WebElement locateSearchInput(WebDriver driver, WebDriverWait wait) {
            for (String sel : POSSIBLE) {
                try {
                    List<WebElement> els = driver.findElements(By.cssSelector(sel));
                    if (!els.isEmpty()) {
                        WebElement e = els.get(0);
                        // wait briefly for visibility
                        wait.until(ExpectedConditions.visibilityOf(e));
                        return e;
                    }
                } catch (Exception ignored) {}
            }
            throw new IllegalStateException("Search input not found; update selectors after inspecting the page.");
        }
    }
    static class CookieHelper {
        static void closeCookiesIfVisible(WebDriver driver, WebDriverWait wait) {
            // 1) quick try OneTrust accept button
            try {
                WebElement ot = wait.withTimeout(Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
                ot.click();
                sleep(300);
                System.out.println("Accepted cookies via OneTrust button.");
                return;
            } catch (Exception ignored) {}

            // 2) try the JetBrains-style .ch2-container (Selenide example)
            try {
                WebElement container = driver.findElement(By.cssSelector(".ch2-container"));
                // wait briefly for it to appear (non-fatal)
                try {
                    long end = System.currentTimeMillis() + 1000;
                    while (System.currentTimeMillis() < end) {
                        if (container.isDisplayed()) break;
                        sleep(100);
                    }
                } catch (Exception ignored) {}
                if (container.isDisplayed()) {
                    try {
                        WebElement btn = container.findElement(By.cssSelector("button"));
                        btn.click();
                        sleep(300);
                        System.out.println("Closed cookies via .ch2-container button.");
                        return;
                    } catch (Exception ignored) {}
                }
            } catch (NoSuchElementException ignored) {}

            // 3) fallback: try any visible button or link that contains accept/agree text
            try {
                List<WebElement> candidates = driver.findElements(By.xpath("//button|//a"));
                for (WebElement el : candidates) {
                    try {
                        String txt = el.getText();
                        if (txt == null) continue;
                        String low = txt.trim().toLowerCase();
                        if (low.contains("accept") || low.contains("accept all") || low.contains("agree") || low.contains("got it") || low.contains("i accept")) {
                            try {
                                el.click();
                                sleep(300);
                                System.out.println("Accepted cookies via text-matching element: " + txt);
                                return;
                            } catch (Exception ignored) {}
                        }
                    } catch (StaleElementReferenceException ignored) {}
                }
            } catch (Exception ignored) {}

            // 4) last resort: hide overlay nodes (non-invasive to server)
            try {
                ((JavascriptExecutor) driver).executeScript(
                        "['.ch2-container','.cookie-banner','.cookie-consent','#onetrust-consent-sdk','.onetrust-banner','.modal','.overlay']" +
                                ".forEach(function(s){ document.querySelectorAll(s).forEach(function(e){ e.style.display='none'; e.style.visibility='hidden'; });});");
                sleep(150);
                System.out.println("Hid common overlay selectors as fallback.");
            } catch (Exception ignored) {}
        }
    }
}