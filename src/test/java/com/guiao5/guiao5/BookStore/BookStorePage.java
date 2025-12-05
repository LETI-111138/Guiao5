package com.guiao5.guiao5.BookStore;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object para a Bookstore demo (Vaadin).
 * Fornece métodos de alto nível para:
 *  - abrir a app
 *  - navegar até o painel de administração / novo produto
 *  - preencher um novo produto (tentando selectors normais e fallback JS)
 *
 * Ajusta os labels / textos conforme a versão da demo se necessário.
 */
public class BookStorePage {

    private static final String URL = "https://vaadin-bookstore-example.demo.vaadin.com/";
    private static final long DEFAULT_SLEEP_MS = 500L;

    // Selectors possíveis para o botão "New product" / "Add" (varia por versão)
    private static final List<String> NEW_PRODUCT_SELECTORS = Arrays.asList(
            "button:has-text('New product')",
            "vaadin-button:has-text('New product')",
            "button:contains('New product')",
            "button:has-text('Add product')",
            "vaadin-button:has-text('Add product')"
    );

    @Step("Abrir a aplicação Bookstore")
    public BookStorePage openPage() {
        open(URL);
        // espera inicial curta para render
        sleep(DEFAULT_SLEEP_MS);
        return this;
    }

    @Step("Ir para o painel Admin / Bookstore (se existir um link direto)")
    public BookStorePage openAdminViewIfPresent() {
        // tenta encontrar um link ou botão "Admin" ou "Manage" ou "Add"
        SelenideElement el = findVisible(
                "a:has-text('Admin')",
                "button:has-text('Admin')",
                "a:has-text('Manage')",
                "button:has-text('Manage')",
                "button:has-text('New product')"
        );
        if (el != null) {
            try {
                el.shouldBe(Condition.visible).click();
                sleep(DEFAULT_SLEEP_MS);
            } catch (Exception ignored) {}
        }
        return this;
    }

    @Step("Abrir formulário 'New product' (tentativa Selenide + fallback JS)")
    public BookStorePage openNewProductForm() {
        // 1) tentativa simples com seletores conhecidos
        for (String sel : NEW_PRODUCT_SELECTORS) {
            try {
                SelenideElement b = $(sel);
                if (b.exists() && b.is(Condition.visible)) {
                    b.click();
                    sleep(DEFAULT_SLEEP_MS);
                    return this;
                }
            } catch (Exception ignored) {}
        }

        // 2) fallback: pesquisar recursivamente em shadow roots via JS (retorna boolean)
        String js =
                "function findDeepButtonWithText(text, root) {\n" +
                        "  root = root || document;\n" +
                        "  const buttons = root.querySelectorAll('vaadin-button, button');\n" +
                        "  for (const b of buttons) { try { if (b.textContent && b.textContent.trim().includes(text)) { b.click(); return true; } } catch(e){} }\n" +
                        "  const elems = root.querySelectorAll('*');\n                        " +
                        "  for (const el of elems) {\n" +
                        "    try { if (el.shadowRoot) { if (findDeepButtonWithText(text, el.shadowRoot)) return true; } } catch(e){}\n" +
                        "  }\n" +
                        "  return false;\n" +
                        "}\n" +
                        "return findDeepButtonWithText('New product') || findDeepButtonWithText('Add product') || findDeepButtonWithText('Add');";

        boolean ok = (Boolean) executeJavaScript(js);
        if (!ok) {
            throw new IllegalStateException("Não consegui abrir o formulário 'New product' (procura falhou). URL atual: " + webdriver().driver().url());
        }

        sleep(DEFAULT_SLEEP_MS);
        return this;
    }

    @Step("Preencher novo produto (tenta selectors normais e depois fallback JS)")
    public BookStorePage fillNewProduct(String name, String price, String stock, String availability, String category) {
        // 1) tenta preencher campos visíveis com selectors simples (caso a UI não use shadow DOM)
        boolean basicAttempt = tryFillBasicFields(name, price, stock, availability, category);
        if (basicAttempt) {
            // tenta clicar em Save com Selenide também
            SelenideElement save = findVisible("button:has-text('Save')", "vaadin-button:has-text('Save')", "button:has-text('Create')");
            if (save != null) {
                save.click();
                sleep(DEFAULT_SLEEP_MS * 2);
                return this;
            }
        }

        // 2) fallback JS: similar ao que tens no AddProductPage, simplificado
        String jsFill =
                "(function(){\n" +
                        "  function findDeep(selector, root){ root = root||document; try{ const d = root.querySelector(selector); if(d) return d;}catch(e){} const elems = root.querySelectorAll('*'); for(const el of elems){ try{ if(el.shadowRoot){ const f = findDeep(selector, el.shadowRoot); if(f) return f; } }catch(e){} } return null; }\n" +
                        "  function findFieldByLabel(label){ const candidates = Array.from(document.querySelectorAll('vaadin-text-field, vaadin-number-field, vaadin-text-area')); for(const c of candidates){ try{ const lab = c.label || (c.getAttribute && c.getAttribute('label')) || ''; if(lab && (lab.trim()===label || lab.trim().startsWith(label) || lab.toLowerCase().includes(label.toLowerCase()))) return c; }catch(e){} } return findDeep('vaadin-text-field[label=\"'+arguments[0]+'\"]') || findDeep('vaadin-number-field[label=\"'+arguments[0]+'\"]'); }\n" +
                        "  function setField(label, value){ const field = findFieldByLabel(label); if(!field) return false; var input = null; try{ input = field.shadowRoot && field.shadowRoot.querySelector('input, textarea'); }catch(e){} if(input){ try{ input.focus(); input.value = value; input.dispatchEvent(new Event('input',{bubbles:true, composed:true})); input.dispatchEvent(new Event('change',{bubbles:true, composed:true})); input.blur(); }catch(e){} } try{ field.value = value; field.dispatchEvent(new CustomEvent('value-changed',{detail:{value:value}, bubbles:true, composed:true})); }catch(e){} return true; }\n" +
                        "  function clickCategory(labelText){ const all = Array.from(document.querySelectorAll('vaadin-checkbox')); for(const cb of all){ try{ const lab = cb.label || (cb.getAttribute && cb.getAttribute('label')) || ''; if(lab && (lab.trim()===labelText || lab.trim().toLowerCase().includes(labelText.toLowerCase()))){ cb.checked = true; cb.dispatchEvent(new Event('change',{bubbles:true, composed:true})); return true; } }catch(e){} } const labels = Array.from(document.querySelectorAll('label, span, div')); for(const l of labels){ try{ if(l.textContent && l.textContent.trim().toLowerCase().includes(labelText.toLowerCase())){ let p = l; for(let i=0;i<6;i++){ p = p.parentElement; if(!p) break; const cb = p.querySelector && p.querySelector('vaadin-checkbox, input[type=checkbox]'); if(cb){ cb.checked = true; cb.dispatchEvent(new Event('change',{bubbles:true, composed:true})); return true; } } } }catch(e){} } return false; }\n" +
                        "  function setSelect(label, optionText){ const selects = Array.from(document.querySelectorAll('vaadin-select, vaadin-combo-box, select')); for(const s of selects){ try{ const lab = s.label || (s.getAttribute && s.getAttribute('label')) || ''; if(lab && (lab.trim()===label || lab.trim().toLowerCase().includes(label.toLowerCase()))){ try{ s.focus(); s.click(); }catch(e){} const matches = Array.from(document.querySelectorAll('*')).filter(el=> el.textContent && el.textContent.trim().toLowerCase().includes(optionText.toLowerCase())); for(const m of matches){ try{ m.click(); return true;}catch(e){} } try{ s.value = optionText; s.dispatchEvent(new Event('change',{bubbles:true, composed:true})); s.dispatchEvent(new CustomEvent('value-changed',{detail:{value:optionText}, bubbles:true, composed:true})); return true;}catch(e){} } }catch(e){} } return false; }\n" +
                        "  function clickSave(){ const buttons = document.querySelectorAll('vaadin-button, button'); for(const b of buttons){ try{ if(b.textContent && b.textContent.trim().toLowerCase().startsWith('save')){ try{ b.click(); }catch(e){} return true;} }catch(e){} } return false; }\n" +
                        "  if(!setField('Product name', arguments[0])) return false; setField('Price', arguments[1]); setField('In stock', arguments[2]); if(arguments[3]) setSelect('Availability', arguments[3]); if(arguments[4]) clickCategory(arguments[4]); clickSave(); return true; })();";

        boolean ok = (Boolean) executeJavaScript(jsFill, name, price, stock, availability, category);
        if (!ok) {
            throw new IllegalStateException("Falha ao preencher o formulário (JS fallback). URL: " + webdriver().driver().url());
        }

        // pequena espera para o grid atualizar
        sleep(DEFAULT_SLEEP_MS * 2);
        return this;
    }

    // ============================
    // Helpers
    // ============================

    /**
     * Tenta preencher campos simples (sem shadow DOM) com Selenide.
     * Retorna true se encontrou e preencheu pelo menos o nome do produto.
     */
    private boolean tryFillBasicFields(String name, String price, String stock, String availability, String category) {
        try {
            SelenideElement nameField = findVisible("input[placeholder*='Product name']", "input[name='name']", "input[id*='name']", "vaadin-text-field[label*='Product name']");
            if (nameField == null) return false;
            nameField.shouldBe(Condition.visible).click();
            nameField.setValue(name);

            SelenideElement priceField = findVisible("input[placeholder*='Price']", "input[name='price']", "vaadin-number-field[label*='Price']");
            if (priceField != null) { priceField.click(); priceField.setValue(price); }

            SelenideElement stockField = findVisible("input[placeholder*='In stock']", "input[name='stock']", "vaadin-number-field[label*='In stock']");
            if (stockField != null) { stockField.click(); stockField.setValue(stock); }

            // availability / category são opcionais em modos básicos
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Devolve o primeiro elemento visível entre os seletores fornecidos.
     */
    private SelenideElement findVisible(String... selectors) {
        for (String s : selectors) {
            try {
                SelenideElement el = $(s);
                if (el.exists() && el.is(Condition.visible)) return el;
            } catch (Exception ignored) {}
        }
        return null;
    }
}
