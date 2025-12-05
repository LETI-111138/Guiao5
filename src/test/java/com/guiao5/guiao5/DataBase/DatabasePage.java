package com.guiao5.guiao5.DataBase;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class DatabasePage {

    // URL retirado da imagem na página 5 do guião
    private static final String URL = "https://vaadin-database-example.demo.vaadin.com/";

    // Seletores (Selenide)
    // Vaadin usa muitas vezes grids e layouts personalizados.
    // O $ é o equivalente a driver.findElement(By.cssSelector(...))
    private final SelenideElement grid = $("vaadin-grid");
    private final SelenideElement movieForm = $("div.movie-form"); // Painel lateral
    private final SelenideElement titleField = $("vaadin-text-field[label='Title']");
    private final SelenideElement scoreField = $("vaadin-text-field[label='Score']");

    public void openPage() {
        open(URL);
    }

    /**
     * Seleciona o primeiro filme da lista que contenha o texto indicado.
     */
    public void selectMovie(String movieName) {
        // Espera que a grelha esteja visível
        grid.shouldBe(visible);

        // Procura dentro da grelha o texto do filme e clica
        // O Selenide faz o scroll automático se necessário
        grid.$(byText(movieName)).click();
    }

    /**
     * Verifica se o formulário de detalhes à direita está visível
     */
    public boolean isMovieDetailsVisible() {
        return movieForm.isDisplayed();
    }

    /**
     * Obtém o título do filme que aparece no formulário de edição
     */
    public String getDisplayedTitle() {
        // Em Vaadin, o valor está muitas vezes numa propriedade 'value' ou no shadow dom.
        // O Selenide tem o método .getValue() que geralmente funciona bem para inputs.
        return titleField.shouldBe(visible).getValue();
    }
}