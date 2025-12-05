package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.CheckboxesPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

public class CheckboxesTest {

    private WebDriver driver;
    private CheckboxesPage checkboxesPage;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        checkboxesPage = new CheckboxesPage(driver);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testCheckboxesSelection() {
        checkboxesPage.open();

        // índice 0 = primeiro checkbox; índice 1 = segundo checkbox
        assertFalse(
                checkboxesPage.isCheckboxSelected(0),
                "Checkbox 1 deve começar desmarcada"
        );
        assertTrue(
                checkboxesPage.isCheckboxSelected(1),
                "Checkbox 2 deve começar marcada"
        );

        checkboxesPage.selectCheckbox(0);
        checkboxesPage.unselectCheckbox(1);

        assertTrue(
                checkboxesPage.isCheckboxSelected(0),
                "Checkbox 1 deve ficar marcada"
        );
        assertFalse(
                checkboxesPage.isCheckboxSelected(1),
                "Checkbox 2 deve ficar desmarcada"
        );
    }
}
