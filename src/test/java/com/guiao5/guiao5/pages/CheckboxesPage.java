package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CheckboxesPage {

    private final WebDriver driver;

    // URL da página
    private static final String URL = "https://the-internet.herokuapp.com/checkboxes";

    // Localizadores (mais robustos que XPath com index)
    private final By checkboxes = By.cssSelector("#checkboxes input[type='checkbox']");

    public CheckboxesPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Abre a página dos checkboxes */
    public void open() {
        driver.get(URL);
    }

    /** Devolve o checkbox por índice (0 = primeiro, 1 = segundo) */
    private WebElement getCheckbox(int index) {
        return driver.findElements(checkboxes).get(index);
    }

    /** Marca o checkbox indicado */
    public void selectCheckbox(int index) {
        WebElement cb = getCheckbox(index);
        if (!cb.isSelected()) cb.click();
    }

    /** Desmarca o checkbox indicado */
    public void unselectCheckbox(int index) {
        WebElement cb = getCheckbox(index);
        if (cb.isSelected()) cb.click();
    }

    /** Verifica se está marcado */
    public boolean isCheckboxSelected(int index) {
        return getCheckbox(index).isSelected();
    }
}
