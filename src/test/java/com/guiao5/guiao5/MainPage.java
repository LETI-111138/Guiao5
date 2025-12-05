package com.guiao5.guiao5;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.$;

// page_url = https://www.jetbrains.com/
public class MainPage {

    private final WebDriver driver;

    // Public locators to be used in tests with $(...)
    public final By seeDeveloperToolsButton =
            By.cssSelector("[data-test-marker='Developer Tools']");

    public final By findYourToolsButton =
            By.cssSelector("[data-test='suggestion-action']");

    public final By toolsMenu =
            By.cssSelector("[data-test='main-menu-item'][data-test-marker='Developer Tools']");

    public final By searchButton =
            By.cssSelector("[data-test='site-header-search-action']");

    public MainPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    // Convenience methods (if you want to use the Page Object directly in tests)

    public SelenideElement seeDeveloperToolsButton() {
        return $(seeDeveloperToolsButton);
    }

    public SelenideElement findYourToolsButtonElement() {
        return $(findYourToolsButton);
    }

    public SelenideElement toolsMenuElement() {
        return $(toolsMenu);
    }

    public SelenideElement searchButtonElement() {
        return $(searchButton);
    }
}
