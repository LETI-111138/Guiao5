package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.DynamicLoadingPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingTest {

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
    public void testWaitForHiddenElement() {
        DynamicLoadingPage loadingPage = new DynamicLoadingPage(driver);
        loadingPage.open();

        // Clica no botão Start
        loadingPage.clickStart();

        // A Page Object trata da espera (Wait) internamente no método getFinishText
        String text = loadingPage.getFinishText();

        assertEquals(
                "Hello World!",
                text,
                "O texto final não corresponde ou não apareceu a tempo."
        );
    }
}