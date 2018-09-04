package hu.hevi.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@SpringBootApplication
public class App {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        String homeDir = System.getProperty("user.home");

        String directoryName = homeDir.concat("/.note");

        File directory = new File(directoryName);
        if (!directory.exists()) {
            boolean mkdirResult = directory.mkdir();
            if (!mkdirResult) {
                throw new IOException("Couldn't create directory in: " + directory);
            }
        }

        String fileName = directoryName.concat("/note.nt");
        File file = new File(fileName);
        if (!file.exists()) {
            boolean newFileResult = file.createNewFile();
            if (!newFileResult) {
                throw new IOException("Couldn't create file in: " + directory);
            }
        }

    }

    @Configuration
    @ComponentScan(lazyInit = true, basePackages = "hu.hevi")
    static class LocalConfig {
    }
}
