package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.BasicAuthPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

public class BasicAuthTest {

    private WebDriver driver;
    private BasicAuthPage basicAuthPage;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        basicAuthPage = new BasicAuthPage(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testBasicAuthWithValidCredentials() {

        // 1. Abrir a página já com credenciais na URL
        basicAuthPage.openWithCredentials();

        // 2. Verificar se a mensagem de sucesso aparece
        assertTrue(
                basicAuthPage.isSuccessMessageVisible(),
                "A mensagem de sucesso devia estar visível após autenticação válida."
        );

        // 3. Verificar conteúdo da mensagem
        String msg = basicAuthPage.getSuccessMessage();
        assertTrue(
                msg.contains("Congratulations"),
                "A mensagem devia conter 'Congratulations'."
        );
        assertTrue(
                msg.contains("proper credentials"),
                "A mensagem devia indicar que as credenciais foram aceites ('proper credentials')."
        );
    }
}
