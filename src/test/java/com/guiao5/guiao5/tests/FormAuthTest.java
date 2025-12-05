package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.FormAuthPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormAuthTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginSuccess() {
        FormAuthPage loginPage = new FormAuthPage(driver);
        loginPage.open();

        // Tentar login com credenciais válidas (definidas pelo site The Internet)
        loginPage.login("tomsmith", "SuperSecretPassword!");

        // Validar mensagem de sucesso
        String alertText = loginPage.getAlertText();
        assertTrue(
                alertText.contains("You logged into a secure area!"),
                "O login falhou ou a mensagem de sucesso não apareceu."
        );

        // Validar que o botão de logout aparece
        assertTrue(
                loginPage.isLogoutButtonVisible(),
                "O botão de logout deveria estar visível após o login."
        );
    }
}