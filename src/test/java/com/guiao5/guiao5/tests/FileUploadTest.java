package com.guiao5.guiao5.tests;

import com.guiao5.guiao5.pages.FileUploadPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUploadTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFileUpload() {
        FileUploadPage uploadPage = new FileUploadPage(driver);
        uploadPage.open();

        // Usamos o pom.xml do próprio projeto como ficheiro de teste para garantir que existe
        // .getAbsolutePath() converte o caminho relativo para o caminho completo do sistema
        File fileToUpload = new File("pom.xml");
        String absolutePath = fileToUpload.getAbsolutePath();

        uploadPage.uploadFile(absolutePath);

        // Validações
        assertEquals(
                "File Uploaded!",
                uploadPage.getSuccessHeaderText(),
                "O cabeçalho de sucesso não apareceu."
        );

        assertEquals(
                "pom.xml",
                uploadPage.getUploadedFileName(),
                "O nome do ficheiro apresentado não é o correto."
        );
    }
}