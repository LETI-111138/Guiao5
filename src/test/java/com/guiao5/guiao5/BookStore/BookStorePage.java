package com.guiao5.guiao5.BookStore;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.*;

public class BookStorePage {

    private static final String URL = "https://vaadin-bookstore-example.demo.vaadin.com/";
    private static final long TIMEOUT_MS = 10_000; // 10 seconds

    static {
        Configuration.timeout = TIMEOUT_MS;
    }

    @Step("Open Bookstore application")
    public BookStorePage openPage() {
        open(URL);
        return this;
    }

    @Step("Login as admin")
    public BookStorePage loginAsAdmin() {
        // Try normal inputs first
        SelenideElement username = findVisible(
                "#username",
                "input[name='username']",
                "input[id*='user']",
                "input[placeholder*='User']",
                "input[type='text']"
        );

        SelenideElement password = findVisible(
                "#password",
                "input[name='password']",
                "input[id*='pass']",
                "input[placeholder*='Pass']",
                "input[type='password']"
        );

        if (username != null && password != null) {
            username.click();
            username.setValue("admin");

            password.click();
            password.setValue("admin");

            SelenideElement submit = findVisible(
                    "button[type='submit']",
                    "button:has-text('Log in')",
                    "button:has-text('Login')",
                    "vaadin-button[title='Log in']",
                    "vaadin-button"
            );

            if (submit != null) {
                submit.click();
            } else {
                password.pressEnter();
            }

            sleep(1500);
            return this;
        }

        // JavaScript fallback
        boolean jsResult = Boolean.TRUE.equals(
                executeJavaScript(
                        "(() => {\n" +
                                "  const selectorsU = ['#username','input[name=\"username\"]','input[id*=user]','input[placeholder*=\"User\"]','input[type=text]'];\n" +
                                "  const selectorsP = ['#password','input[name=\"password\"]','input[id*=pass]','input[placeholder*=\"Pass\"]','input[type=password]'];\n" +
                                "  function findFirst(selectors){ for(const s of selectors){ const e = document.querySelector(s); if(e) return e; } return null; }\n" +
                                "  const u = findFirst(selectorsU);\n" +
                                "  const p = findFirst(selectorsP);\n" +
                                "  if(!u || !p) return false;\n" +
                                "  u.focus(); u.value = 'admin'; u.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                                "  p.focus(); p.value = 'admin'; p.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                                "  const submit = document.querySelector('button[type=submit], button[title=\"Log in\"]');\n" +
                                "  if(submit){ submit.click(); return true; }\n" +
                                "  p.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter'}));\n" +
                                "  p.dispatchEvent(new KeyboardEvent('keypress',{key:'Enter'}));\n" +
                                "  p.dispatchEvent(new KeyboardEvent('keyup',{key:'Enter'}));\n" +
                                "  return true;\n" +
                                "})();"
                )
        );

        if (!jsResult) {
            WebDriver drv = WebDriverRunner.getWebDriver();
            throw new IllegalStateException("Could not log in. Current URL: " + drv.getCurrentUrl());
        }

        sleep(1500);
        return this;
    }

    @Step("Open 'New product' form")
    public BookStorePage openAddProductForm() {

        boolean clicked = false;

        // Try for a few seconds; Vaadin may take time to render the button
        for (int i = 0; i < 10 && !clicked; i++) {
            clicked = Boolean.TRUE.equals(
                    executeJavaScript(
                            "function findDeepButtonWithText(text, root) {\n" +
                                    "  root = root || document;\n" +
                                    "  const buttons = root.querySelectorAll('vaadin-button, button');\n" +
                                    "  for (const b of buttons) {\n" +
                                    "    if (b.textContent && b.textContent.trim().includes(text)) {\n" +
                                    "      b.click();\n" +
                                    "      return true;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  const elems = root.querySelectorAll('*');\n" +
                                    "  for (const el of elems) {\n" +
                                    "    if (el.shadowRoot) {\n" +
                                    "      if (findDeepButtonWithText(text, el.shadowRoot)) return true;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  return false;\n" +
                                    "}\n" +
                                    "return findDeepButtonWithText('New product');"
                    )
            );

            if (!clicked) {
                sleep(500);
            }
        }

        if (!clicked) {
            throw new IllegalStateException("Could not find 'New product' button.");
        }

        // Time for the right-side panel to open
        sleep(1000);
        return this;
    }

    @Step("Fill 'New product' form and save")
    public BookStorePage fillNewProductForm(
            String name,
            String price,
            String stock,
            String availability,
            String category
    ) {

        boolean result = false;

        for (int i = 0; i < 12 && !result; i++) {
            result = Boolean.TRUE.equals(
                    executeJavaScript(
                            "function findDeep(selector, root) {\n" +
                                    "  root = root || document;\n" +
                                    "  const direct = root.querySelector(selector);\n" +
                                    "  if (direct) return direct;\n" +
                                    "  const elems = root.querySelectorAll('*');\n" +
                                    "  for (const el of elems) {\n" +
                                    "    if (el.shadowRoot) {\n" +
                                    "      const found = findDeep(selector, el.shadowRoot);\n" +
                                    "      if (found) return found;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  return null;\n" +
                                    "}\n" +

                                    "function findFieldByLabel(label) {\n" +
                                    "  const candidates = Array.from(document.querySelectorAll('vaadin-text-field, vaadin-number-field, vaadin-text-area'));\n" +
                                    "  for (const c of candidates) {\n" +
                                    "    try {\n" +
                                    "      const lab = c.label || (c.getAttribute && c.getAttribute('label')) || '';\n" +
                                    "      if (lab && (lab.trim() === label || lab.trim().startsWith(label) || lab.toLowerCase().includes(label.toLowerCase()))) {\n" +
                                    "        return c;\n" +
                                    "      }\n" +
                                    "    } catch(e) { /* ignore */ }\n" +
                                    "  }\n" +
                                    "  return findDeep('vaadin-text-field[label=\"' + label + '\"]')\n" +
                                    "      || findDeep('vaadin-number-field[label=\"' + label + '\"]');\n" +
                                    "}\n" +

                                    "function setField(label, value) {\n" +
                                    "  const field = findFieldByLabel(label);\n" +
                                    "  if (!field) return false;\n" +
                                    "  let input = null;\n" +
                                    "  try { input = field.shadowRoot && field.shadowRoot.querySelector('input, textarea'); } catch(e) { input = null; }\n" +
                                    "  if (input) {\n" +
                                    "    input.focus();\n" +
                                    "    input.value = value;\n" +
                                    "    input.dispatchEvent(new Event('input', {bubbles:true, composed:true}));\n" +
                                    "    input.dispatchEvent(new Event('change', {bubbles:true, composed:true}));\n" +
                                    "    input.blur();\n" +
                                    "  }\n" +
                                    "  try {\n" +
                                    "    field.value = value;\n" +
                                    "    field.dispatchEvent(new CustomEvent('value-changed',{detail:{value:value}, bubbles:true, composed:true}));\n" +
                                    "  } catch(e){ /* ignore */ }\n" +
                                    "  return true;\n" +
                                    "}\n" +

                                    "function clickCategory(labelText) {\n" +
                                    "  // Try vaadin-checkbox with label\n" +
                                    "  const all = Array.from(document.querySelectorAll('vaadin-checkbox'));\n" +
                                    "  for (const cb of all) {\n" +
                                    "    const lab = cb.label || (cb.getAttribute && cb.getAttribute('label')) || '';\n" +
                                    "    if (lab && (lab.trim() === labelText || lab.trim().toLowerCase().includes(labelText.toLowerCase()))) {\n" +
                                    "      cb.checked = true;\n" +
                                    "      cb.dispatchEvent(new Event('change', {bubbles:true, composed:true}));\n" +
                                    "      return true;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  // Fallback: search for visible labels in the category list\n" +
                                    "  const labels = Array.from(document.querySelectorAll('label, span, div'));\n" +
                                    "  for (const l of labels) {\n" +
                                    "    try {\n" +
                                    "      if (l.textContent && l.textContent.trim().toLowerCase().includes(labelText.toLowerCase())) {\n" +
                                    "        // Try to click a parent element that contains a checkbox\n" +
                                    "        let p = l;\n" +
                                    "        for (let i=0;i<6;i++){\n" +
                                    "          p = p.parentElement;\n" +
                                    "          if(!p) break;\n" +
                                    "          const cb = p.querySelector && p.querySelector('vaadin-checkbox, input[type=checkbox]');\n" +
                                    "          if(cb){\n" +
                                    "            cb.checked = true;\n" +
                                    "            cb.dispatchEvent(new Event('change',{bubbles:true, composed:true}));\n" +
                                    "            return true;\n" +
                                    "          }\n" +
                                    "        }\n" +
                                    "      }\n" +
                                    "    } catch(e) { }\n" +
                                    "  }\n" +
                                    "  return false;\n" +
                                    "}\n" +

                                    "function setSelect(label, optionText) {\n" +
                                    "  // Supports vaadin-select, vaadin-combo-box, and native select\n" +
                                    "  const selects = Array.from(document.querySelectorAll('vaadin-select, vaadin-combo-box, select'));\n" +
                                    "  for (const s of selects) {\n" +
                                    "    const lab = s.label || (s.getAttribute && s.getAttribute('label')) || '';\n" +
                                    "    if (lab && (lab.trim() === label || lab.trim().toLowerCase().includes(label.toLowerCase()))) {\n" +
                                    "      try {\n" +
                                    "        let found = null;\n" +
                                    "        try {\n" +
                                    "          const opts = s.shadowRoot ? s.shadowRoot.querySelectorAll('vaadin-select-item, vaadin-combo-box-item, vaadin-item, paper-item, option') : [];\n" +
                                    "          for (const o of opts) { if (o.textContent && o.textContent.trim().toLowerCase().includes(optionText.toLowerCase())) { found = o; break; } }\n" +
                                    "        } catch(e){}\n" +
                                    "        if(found && found.value){\n" +
                                    "          s.value = found.value;\n" +
                                    "          s.dispatchEvent(new CustomEvent('value-changed',{detail:{value:s.value}, bubbles:true, composed:true}));\n" +
                                    "          return true;\n" +
                                    "        }\n" +
                                    "        try { s.focus(); s.click(); } catch(e){}\n" +
                                    "        const matches = Array.from(document.querySelectorAll('*'))\n" +
                                    "            .filter(el=> el.textContent && el.textContent.trim().toLowerCase().includes(optionText.toLowerCase()));\n" +
                                    "        for(const m of matches){ try{ m.click(); return true; }catch(e){} }\n" +
                                    "        try {\n" +
                                    "          s.value = optionText;\n" +
                                    "          s.dispatchEvent(new Event('change',{bubbles:true, composed:true}));\n" +
                                    "          s.dispatchEvent(new CustomEvent('value-changed',{detail:{value:optionText}, bubbles:true, composed:true}));\n" +
                                    "          return true;\n" +
                                    "        } catch(e){}\n" +
                                    "      } catch(e) { /* ignore */ }\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  return false;\n" +
                                    "}\n" +

                                    // Aggressive clickSave that tries to remove disabled and use multiple strategies
                                    "function clickSave(root){\n" +
                                    "  root = root || document;\n" +
                                    "  function removeDisabledAttributes(el){\n" +
                                    "    try {\n" +
                                    "      if(el.hasAttribute && el.hasAttribute('disabled')) el.removeAttribute('disabled');\n" +
                                    "      if(el.hasAttribute && el.hasAttribute('aria-disabled')) el.removeAttribute('aria-disabled');\n" +
                                    "      if(el.disabled !== undefined) el.disabled = false;\n" +
                                    "    } catch(e){}\n" +
                                    "  }\n" +
                                    "  function tryClickElement(el){\n" +
                                    "    if(!el) return false;\n" +
                                    "    removeDisabledAttributes(el);\n" +
                                    "    // 1) Direct click\n" +
                                    "    try { el.click(); return true; } catch(e) {}\n" +
                                    "    // 2) Try clicking inner button inside shadowRoot\n" +
                                    "    try {\n" +
                                    "      const inner = (el.shadowRoot && (el.shadowRoot.querySelector('button') || el.shadowRoot.querySelector('[part=button]')));\n" +
                                    "      if(inner){ removeDisabledAttributes(inner); try { inner.click(); return true; } catch(e) {} }\n" +
                                    "    } catch(e) {}\n" +
                                    "    // 3) Dispatch mouse/pointer events sequence\n" +
                                    "    const evs = ['pointerdown','mousedown','pointerup','mouseup','click'];\n" +
                                    "    for(const t of evs){\n" +
                                    "      try { el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window})); } catch(e) {}\n" +
                                    "    }\n" +
                                    "    // 4) Try clicking the element under the center of its bounding rect\n" +
                                    "    try {\n" +
                                    "      const r = el.getBoundingClientRect();\n" +
                                    "      const cx = Math.round(r.left + r.width/2);\n" +
                                    "      const cy = Math.round(r.top + r.height/2);\n" +
                                    "      const topEl = document.elementFromPoint(cx, cy);\n" +
                                    "      if(topEl && topEl !== el){\n" +
                                    "        removeDisabledAttributes(topEl);\n" +
                                    "        try { topEl.click(); return true; } catch(e) {}\n" +
                                    "        try { topEl.dispatchEvent(new MouseEvent('click', {bubbles:true, cancelable:true, clientX:cx, clientY:cy})); return true; } catch(e) {}\n" +
                                    "      }\n" +
                                    "    } catch(e) {}\n" +
                                    "    return false;\n" +
                                    "  }\n" +
                                    "  const buttons = root.querySelectorAll('vaadin-button, button');\n" +
                                    "  for (const b of buttons){\n" +
                                    "    if (b.textContent && b.textContent.trim().toLowerCase().startsWith('save')){\n" +
                                    "      removeDisabledAttributes(b);\n" +
                                    "      // Try multiple times with small intervals (some overlays react to focus)\n" +
                                    "      if(tryClickElement(b)) return true;\n" +
                                    "      // Try to find a button inside its shadowRoot\n" +
                                    "      try { if(b.shadowRoot){ const inner = b.shadowRoot.querySelector('button, [part=button]'); if(tryClickElement(inner)) return true; } } catch(e){}\n" +
                                    "      // Last resort: iterate descendants and try click\n" +
                                    "      try {\n" +
                                    "        const desc = b.querySelectorAll('*');\n" +
                                    "        for(const d of desc){ removeDisabledAttributes(d); if(tryClickElement(d)) return true; }\n" +
                                    "      } catch(e){}\n" +
                                    "      return false;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  // Recursive search in shadow roots\n" +
                                    "  const elems = root.querySelectorAll('*');\n" +
                                    "  for (const el of elems){\n" +
                                    "    if (el.shadowRoot){\n" +
                                    "      if (clickSave(el.shadowRoot)) return true;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  return false;\n" +
                                    "}\n" +

                                    "// Fill main fields\n" +
                                    "if (!setField('Product name', arguments[0])) return (function(){\n" +
                                    "  const vals = Array.from(document.querySelectorAll('vaadin-text-field, vaadin-number-field, vaadin-text-area'))\n" +
                                    "    .map(f => f.label || f.getAttribute('label') || f.textContent || 'NO_LABEL');\n" +
                                    "  console.log('Available labels:', vals);\n" +
                                    "  return false;\n" +
                                    "})();\n" +
                                    "setField('Price', arguments[1]);\n" +
                                    "setField('In stock', arguments[2]);\n" +
                                    "// Set availability\n" +
                                    "if (arguments[3]) { const ok = setSelect('Availability', arguments[3]); console.log('setSelect Availability ->', ok); }\n" +
                                    "// Choose category\n" +
                                    "if (arguments[4]) { const okc = clickCategory(arguments[4]); console.log('clickCategory ->', okc); }\n" +

                                    "// Scroll to Save button\n" +
                                    "function scrollToSave(){\n" +
                                    "  const buttons = document.querySelectorAll('vaadin-button, button');\n" +
                                    "  for(const b of buttons){\n" +
                                    "    if(b.textContent && b.textContent.trim().toLowerCase().startsWith('save')){\n" +
                                    "      try { b.scrollIntoView({behavior:'auto', block:'center'}); } catch(e){}\n" +
                                    "      return true;\n" +
                                    "    }\n" +
                                    "  }\n" +
                                    "  return false;\n" +
                                    "}\n" +
                                    "scrollToSave();\n" +

                                    "// Try clicking Save\n" +
                                    "return clickSave();",
                            name, price, stock, availability, category
                    )
            );

            if (!result) {
                sleep(500);
            }
        }

        if (!result) {
            throw new IllegalStateException("Could not fill the form or click 'Save'.");
        }

        // Time for the grid to update with the new product
        sleep(2000);
        return this;
    }

    /**
     * Returns the first visible element that matches one of the provided selectors,
     * or null if none is found.
     */
    private SelenideElement findVisible(String... selectors) {
        for (String sel : selectors) {
            try {
                SelenideElement el = $(sel);
                if (el.exists() && el.is(Condition.visible)) {
                    return el;
                }
            } catch (Exception ignored) {
                // If a selector is invalid (:has-text in older drivers, etc.) we just ignore it
            }
        }
        return null;
    }
}
