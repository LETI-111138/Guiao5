package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class DynamicContentPage {

    private final WebDriver driver;

    // Locator das linhas do conteúdo dinâmico
    private final By rowsLocator = By.cssSelector("#content .row");

    public DynamicContentPage(WebDriver driver) {
        this.driver = driver;
    }

    /** Abre a página de conteúdo dinâmico */
    public void open() {
        driver.get("https://the-internet.herokuapp.com/dynamic_content");
    }

    /**
     * Obtém o texto de todas as linhas visíveis na área dinâmica.
     */
    public List<String> getRowsText() {
        return driver.findElements(rowsLocator)
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    /** Recarrega a página para gerar novo conteúdo */
    public void refresh() {
        driver.navigate().refresh();
    }
}
