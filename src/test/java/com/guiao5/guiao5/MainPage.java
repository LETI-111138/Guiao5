package com.guiao5.guiao5;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MainPage {

    // --- COOKIES ---
    @FindBy(css = "[data-test='allow-all-cookies-button']")
    public WebElement acceptCookiesButton;

    // --- MENU ---
    // Procura qualquer elemento que contenha "Developer Tools"
    @FindBy(xpath = "//*[contains(text(), 'Developer Tools')]")
    public WebElement toolsMenu;

    // Link para validação do menu
    @FindBy(xpath = "//a[contains(text(), 'IntelliJ IDEA')]")
    public WebElement menuIntellijLink;

    // --- PESQUISA ---
    @FindBy(css = "[data-test='site-header-search-action']")
    public WebElement searchButton;

    // CORREÇÃO CRÍTICA:
    // Em vez de procurar por IDs que mudam, procuramos pelo input dentro do formulário.
    // É a forma mais segura de encontrar a caixa de texto.
    @FindBy(css = "[data-test='search-input']")
    public WebElement searchInput;

    // Botão "Submit" ou "Full Search"
    @FindBy(css = "button[data-test='full-search-button']")
    public WebElement fullSearchButton;

    // --- BOTÕES PRINCIPAIS ---
    @FindBy(xpath = "//*[@data-test='suggestion-action'] | //a[contains(., 'Find your tools')]")
    public WebElement findYourToolsButton;

    public MainPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }
}