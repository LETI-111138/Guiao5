package com.guiao5.guiao5;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys; // Importante para usar ENTER se necessário
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MainPageTest {
    private WebDriver driver;
    private MainPage mainPage;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        // Implicit wait ajuda, mas não resolve tudo.
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("https://www.jetbrains.com/");
        mainPage = new MainPage(driver);

        // --- CORREÇÃO CRÍTICA: Aceitar Cookies ---
        // Se não fizermos isto, o banner bloqueia os cliques nos outros botões
        acceptCookiesIfPresent();
    }

    private void acceptCookiesIfPresent() {
        try {
            // Tenta clicar no botão se ele estiver visível
            if (mainPage.acceptCookiesButton.isDisplayed()) {
                mainPage.acceptCookiesButton.click();
                System.out.println("Cookies aceites com sucesso.");
            }
        } catch (Exception e) {
            System.out.println("Banner de cookies não apareceu ou não foi necessário.");
        }
    }

    @AfterEach
    public void tearDown() {
        // Verifica se o driver não é nulo antes de fechar
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void search() {
        // 1. Clicar na lupa
        mainPage.searchButton.click();

        // 2. Escrever "Selenium" usando o Page Object (POM)
        // Corrigido: Usamos mainPage.searchInput em vez de driver.findElement
        mainPage.searchInput.sendKeys("Selenium");

        // 3. Submeter a pesquisa
        // Nota: Muitas vezes clicar no botão falha se ele estiver a animar.
        // Enviar ENTER é mais robusto, mas vou manter o clique conforme o teu original,
        // usando o elemento do Page Object.
        mainPage.fullSearchButton.click();

        // 4. Validação
        // Aqui ainda usamos findElement porque estamos numa NOVA página (resultados),
        // idealmente terias uma "SearchResultsPage.java", mas para este exercício serve.
        WebElement searchPageField = driver.findElement(By.cssSelector("input[data-test='search-input']"));
        assertEquals("Selenium", searchPageField.getAttribute("value"));
    }

    @Test
    public void toolsMenu() {
        // Clica no menu "Developer Tools"
        mainPage.toolsMenu.click();

        // Verifica se o submenu abriu
        WebElement menuPopup = driver.findElement(By.cssSelector("div[data-test='main-submenu']"));
        assertTrue(menuPopup.isDisplayed(), "O submenu não apareceu!");
    }

    @Test
    public void navigationToAllTools() {
        // Removi a linha 'mainPage.seeDeveloperToolsButton.click()' porque esse botão
        // muitas vezes não existe ou é o mesmo que o 'findYourToolsButton'.

        // Clica no botão principal "Find your tools"
        mainPage.findYourToolsButton.click();

        // Validação: Verifica se fomos para a página de produtos
        WebElement productsList = driver.findElement(By.id("products-page"));
        assertTrue(productsList.isDisplayed());

        // Nota: O título da página pode mudar dependendo de promoções,
        // por isso validações de Título exato podem ser frágeis.
        assertTrue(driver.getTitle().contains("Tools"), "O título da página devia conter 'Tools'");
    }
}