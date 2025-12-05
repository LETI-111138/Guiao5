package com.guiao5.guiao5.BookStore;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de integração para Bookstore demo.
 * Fluxo:
 *  - abrir app
 *  - (tentar) abrir vista Admin
 *  - abrir formulário "New product"
 *  - preencher e guardar novo produto
 *  - verificar que o produto aparece (pesquisa profunda em shadow DOM)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookStoreTest {

    private BookStorePage page;
    private String productName;

    @BeforeAll
    void beforeAll() {
        Configuration.browser = "chrome";
        Configuration.timeout = 10_000;
        Configuration.browserSize = "1920x1080";
        Configuration.reportsFolder = "reports";
        // Configuration.headless = true; // descomenta para CI
    }

    @BeforeEach
    void setup() {
        page = new BookStorePage();
        productName = "Livro-Teste-" + System.currentTimeMillis();
    }

    @Test
    @DisplayName("Adicionar produto na Bookstore (fluxo admin -> new product -> validar presença)")
    void addProductAndVerifyAppears() {
        page.openPage()
                .openAdminViewIfPresent()
                .openNewProductForm()
                .fillNewProduct(
                        productName,
                        "19.90",
                        "5",
                        "Available",
                        "Romance"
                );

        // espera pequena para render final
        sleepMillis(2000);

        // Verificação profunda: procura recursiva por texto em document + shadowRoots
        boolean found = isTextPresentDeep(productName);
        assertTrue(found, "Produto recém-criado não foi encontrado na aplicação: " + productName);
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    // -----------------------
    // Helper JS: pesquisa recursiva em shadow roots por texto
    // -----------------------
    private boolean isTextPresentDeep(String text) {
        // script que retorna boolean
        String script =
                "function deepContainsText(root, text){\n" +
                        "  try{\n" +
                        "    if(!root) return false;\n" +
                        "    var t = '';\n" +
                        "    try { t = root.innerText || root.textContent || ''; } catch(e) { t = ''; }\n" +
                        "    if(t && t.toLowerCase().indexOf(text.toLowerCase()) !== -1) return true;\n" +
                        "    var children = root.children || [];\n" +
                        "    for(var i=0;i<children.length;i++){ try{ if(deepContainsText(children[i], text)) return true; } catch(e){} }\n" +
                        "    try{ if(root.shadowRoot){ if(deepContainsText(root.shadowRoot, text)) return true; } }catch(e){}\n" +
                        "    try{ if(root.childNodes){ for(var j=0;j<root.childNodes.length;j++){ try{ if(deepContainsText(root.childNodes[j], text)) return true; }catch(e){} } } }catch(e){}\n" +
                        "    return false;\n" +
                        "  }catch(e){ return false; }\n" +
                        "}\n" +
                        "return deepContainsText(document, arguments[0]);";

        try {
            Object res = executeJavaScript(script, text);
            // pode ser Boolean ou String "true"/"false" em alguns ambientes: tratamos ambos
            if (res instanceof Boolean) {
                return (Boolean) res;
            } else if (res instanceof String) {
                return Boolean.parseBoolean((String) res);
            } else {
                return false;
            }
        } catch (Exception e) {
            // Se houver erro ao executar JS, falhamos com false (o assert no teste dará erro)
            return false;
        }
    }

    // utilitário leve de sleep para evitar confusão com Selenide.sleep (mais explícito)
    private void sleepMillis(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
