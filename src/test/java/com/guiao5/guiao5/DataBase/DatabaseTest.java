package com.guiao5.guiao5.DataBase;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.guiao5.guiao5.DataBase.DatabasePage;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Vaadin Examples")
@Feature("Database Management")
public class DatabaseTest {

    @BeforeAll
    static void setup() {
        // Integração obrigatória para os screenshots no relatório Allure
        SelenideLogger.addListener("allure", new AllureSelenide());

        // Configurações globais (opcional, o Selenide tem defaults bons)
        Configuration.browser = "chrome";
        Configuration.timeout = 10000; // 10 segundos
    }

    @Test
    @Story("Visualizar Detalhes do Filme")
    @Description("Seleciona um filme na grelha e valida se os detalhes aparecem no formulário")
    public void testViewMovieInfo() {
        DatabasePage page = new DatabasePage();
        page.openPage();

        // Vamos clicar num filme que sabemos que existe na demo (ex: "Cop Land")
        // Podes trocar por outro nome que vejas no site ao abrir
        String movieToTest = "Cop Land";

        page.selectMovie(movieToTest);

        // Validação 1: O painel abriu?
        assertTrue(page.isMovieDetailsVisible(), "O painel de detalhes do filme devia abrir.");

        // Validação 2: O título no input corresponde ao clicado?
        assertEquals(movieToTest, page.getDisplayedTitle(), "O título no formulário devia ser igual ao selecionado.");
    }
}