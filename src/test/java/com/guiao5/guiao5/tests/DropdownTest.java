package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.DropdownPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropdownTest {

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
    public void testSelectOption() {
        DropdownPage dropdownPage = new DropdownPage(driver);
        dropdownPage.open();

        // Selecionar "Option 1" e verificar
        dropdownPage.selectOptionByText("Option 1");
        assertEquals(
                "Option 1",
                dropdownPage.getSelectedOption(),
                "A opção selecionada deveria ser 'Option 1'"
        );

        // Selecionar "Option 2" e verificar
        dropdownPage.selectOptionByText("Option 2");
        assertEquals(
                "Option 2",
                dropdownPage.getSelectedOption(),
                "A opção selecionada deveria ser 'Option 2'"
        );
    }
}