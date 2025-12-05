package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.DynamicContentPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DynamicContentTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void contentShouldChangeAfterRefresh() {

        DynamicContentPage page = new DynamicContentPage(driver);
        page.open();

        // Guarda o primeiro estado
        List<String> firstState = page.getRowsText();

        // Recarrega para gerar novo conteúdo
        page.refresh();

        // Guarda o novo estado
        List<String> secondState = page.getRowsText();

        // Validação: normalmente o conteúdo muda
        assertNotEquals(
                firstState,
                secondState,
                "O conteúdo dinâmico deveria ser diferente após o refresh."
        );
    }
}
