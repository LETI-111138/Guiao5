package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FormAuthPage {
    private final WebDriver driver;

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By flashMessage = By.id("flash");
    private final By logoutButton = By.cssSelector("a[href='/logout']");

    public FormAuthPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void login(String username, String password) {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    public String getAlertText() {
        return driver.findElement(flashMessage).getText();
    }

    public boolean isLogoutButtonVisible() {
        return driver.findElement(logoutButton).isDisplayed();
    }
}