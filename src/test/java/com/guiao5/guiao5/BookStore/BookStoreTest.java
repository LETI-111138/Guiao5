package com.guiao5.guiao5.BookStore;

import com.codeborne.selenide.Configuration;
import com.guiao5.guiao5.BookStore.BookStorePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class BookStoreTest {

    private BookStorePage page;

    @BeforeAll
    static void globalSetup() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10000; // 10s
    }

    @BeforeEach
    void setUp() {
        page = new BookStorePage();
    }

    @Test
    void login_criarNovoProduto() {
        page.openPage()
                .loginAsAdmin()
                .openAddProductForm()
                .fillNewProductForm(
                        "Livro de Teste - " + System.currentTimeMillis(),
                        "12.34",
                        "10",
                        "Available",
                        "Romance"
                );
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }
}