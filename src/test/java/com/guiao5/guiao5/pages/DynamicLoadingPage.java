package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class DynamicLoadingPage {
    private final WebDriver driver;

    // Exemplo 1: Elemento escondido que fica visível
    private static final String URL = "https://the-internet.herokuapp.com/dynamic_loading/1";

    private final By startButton = By.cssSelector("#start button");
    private final By finishText = By.cssSelector("#finish h4");
    private final By loadingBar = By.id("loading");

    public DynamicLoadingPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get(URL);
    }

    public void clickStart() {
        driver.findElement(startButton).click();
    }

    /** Espera que a barra de loading desapareça e o texto final apareça */
    public String getFinishText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Esperar que o texto fique visível
        wait.until(ExpectedConditions.visibilityOfElementLocated(finishText));

        return driver.findElement(finishText).getText();
    }
}