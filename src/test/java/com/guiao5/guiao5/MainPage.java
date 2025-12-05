package com.guiao5.guiao5;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

// page_url = https://www.jetbrains.com/
public class MainPage {

    // --- COOKIES ---
    @FindBy(css = "[data-test='allow-all-cookies-button']")
    public WebElement acceptCookiesButton;

    // --- MENU DE NAVEGAÇÃO ---
    // Procura o item de menu que contém o texto "Developer Tools"
    @FindBy(xpath = "//*[@data-test='main-menu-item' and contains(text(), 'Developer Tools')]")
    public WebElement toolsMenu;

    // --- PESQUISA ---
    @FindBy(css = "[data-test='site-header-search-action']")
    public WebElement searchButton;

    @FindBy(css = "[data-test='search-input']")
    public WebElement searchInput;

    @FindBy(css = "button[data-test='full-search-button']")
    public WebElement fullSearchButton;

    // --- BOTÕES DA PÁGINA PRINCIPAL ---
    // O botão principal "Find your tools" (geralmente no centro do ecrã)
    @FindBy(css = "[data-test='suggestion-action']")
    public WebElement findYourToolsButton;

    public MainPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}