package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BasicAuthPage {

    private final WebDriver driver;

    // URL base sem credenciais (isto dispara o popup — Selenium não lida com isto)
    private static final String BASE_URL =
            "https://the-internet.herokuapp.com/basic_auth";

    // URL com credenciais válidas (admin/admin) — evita popup do browser
    private static final String AUTH_URL =
            "https://admin:admin@the-internet.herokuapp.com/basic_auth";

    // Locator da mensagem de sucesso (um <p> dentro do conteúdo)
    private final By successMessage = By.cssSelector("#content div p");

    public BasicAuthPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Abre a página normal (gera popup — NÃO funciona bem com Selenium) */
    public void openWithoutCredentials() {
        driver.get(BASE_URL);
    }

    /** Abre a página já com credenciais */
    public void openWithCredentials() {
        driver.get(AUTH_URL);
    }

    /** Lê o texto da mensagem de sucesso */
    public String getSuccessMessage() {
        WebElement msg = driver.findElement(successMessage);
        return msg.getText().trim();
    }

    /** Verifica se a mensagem de sucesso está visível */
    public boolean isSuccessMessageVisible() {
        return driver.findElement(successMessage).isDisplayed();
    }
}
