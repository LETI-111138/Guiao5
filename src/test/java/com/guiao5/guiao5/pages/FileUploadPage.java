package com.guiao5.guiao5.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;

public class FileUploadPage {
    private final WebDriver driver;

    private final By fileInput = By.id("file-upload");
    private final By uploadButton = By.id("file-submit");
    private final By uploadedFilesList = By.id("uploaded-files");
    private final By successHeader = By.cssSelector("div.example h3");

    public FileUploadPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get("https://the-internet.herokuapp.com/upload");
    }

    /**
     * Faz upload de um ficheiro.
     * @param absolutePath Caminho COMPLETO do ficheiro no teu PC (ex: C:/temp/teste.txt)
     */
    public void uploadFile(String absolutePath) {
        // Truque do Selenium: Enviar o caminho diretamente para o input, sem clicar no botão de browse
        driver.findElement(fileInput).sendKeys(absolutePath);
        driver.findElement(uploadButton).click();
    }

    public String getUploadedFileName() {
        return driver.findElement(uploadedFilesList).getText();
    }

    public String getSuccessHeaderText() {
        return driver.findElement(successHeader).getText();
    }
}