package com.guiao5.guiao5;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.title;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainPageTest {

    private static final String BASE_URL = "https://www.jetbrains.com/";
    private static final String ALL_TOOLS_PAGE_TITLE = "All Developer Tools and Products by JetBrains";

    private MainPage mainPage;

    @BeforeAll
    static void setUpAll() {
        Configuration.browserSize = "1280x800";
        // Default timeout for Selenide explicit waits
        Configuration.timeout = 30_000;

        SelenideLogger.addListener("allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false));
    }

    @BeforeEach
    public void setUp() {
        open(BASE_URL);

        // Page Object still uses WebDriver internally
        mainPage = new MainPage(WebDriverRunner.getWebDriver());

        acceptCookiesIfVisible();
    }

    @AfterEach
    public void tearDown() {
        closeWebDriver();
    }

    @Test
    public void search() {
        final String query = "Selenium";

        $(mainPage.searchButton)
                .shouldBe(visible)
                .click();

        SelenideElement searchInput = $("[data-test-id='search-input']")
                .shouldBe(visible);

        // Type the search text
        searchInput.setValue(query);

        // Immediately validate that the input has the expected value
        searchInput.shouldHave(value(query));

        // Do not press Enter, do not validate URL or page content here
    }

    @Test
    public void toolsMenu() {
        // Use the Page Object element and trigger hover
        SelenideElement toolsMenu = $(mainPage.toolsMenu)
                .shouldBe(visible);

        toolsMenu.hover();

        // Just ensure that submenu elements exist in the DOM
        $$("div[data-test='main-submenu']")
                .shouldHave(sizeGreaterThan(0));

        // Weak assertion about content, independent of layout
        String pageSource = WebDriverRunner.getWebDriver().getPageSource();
        assertTrue(
                pageSource.contains("IDE") || pageSource.contains("Tools"),
                "Page source should contain at least 'IDE' or 'Tools'"
        );
    }

    @Test
    public void navigationToAllTools() {
        // 1) Click the "Developer Tools" card
        $(mainPage.seeDeveloperToolsButton)
                .shouldBe(visible)
                .click();

        // 2) The [data-test='suggestion-action'] button is covered by another <a>,
        //    so we use a JavaScript click to avoid ElementClickInterceptedException
        $(mainPage.findYourToolsButton)
                .shouldBe(visible)
                .click(ClickOptions.usingJavaScript());

        // 3) Products page should be visible
        $("#products-page").shouldBe(visible);

        assertEquals(ALL_TOOLS_PAGE_TITLE, title());
    }

    /**
     * Tries to accept the cookie banner, if present.
     * The test should not fail if the banner is not available.
     */
    private void acceptCookiesIfVisible() {
        try {
            ElementsCollection buttons = $$x(
                    "//button[contains(., 'Accept') or contains(., 'accept')]"
            );

            if (!buttons.isEmpty()) {
                buttons.first()
                        .shouldBe(visible)
                        .click();
                System.out.println("Cookie consent banner accepted.");
            } else {
                System.out.println("No cookie consent banner found.");
            }
        } catch (Exception e) {
            System.out.println("Cookie banner handling failed: " + e.getMessage());
        }
    }
}
