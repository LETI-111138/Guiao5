package com.guiao5.guiao5.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage {
    private final WebDriver driver;
    private final By dropdownLocator = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://the-internet.herokuapp.com/dropdown");
    }

    /** Seleciona uma opção pelo texto visível (ex: "Option 1") */
    public void selectOptionByText(String text) {
        WebElement dropdownElement = driver.findElement(dropdownLocator);
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(text);
    }

    /** Obtém o texto da opção atualmente selecionada */
    public String getSelectedOption() {
        WebElement dropdownElement = driver.findElement(dropdownLocator);
        Select select = new Select(dropdownElement);
        return select.getFirstSelectedOption().getText();
    }
}
