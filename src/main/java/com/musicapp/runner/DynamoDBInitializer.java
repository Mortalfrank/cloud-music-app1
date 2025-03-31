package com.musicapp.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.io.IOException;


@Profile("init-db") // mvn spring-boot:run -Dspring-boot.run.profiles=init-db
@Component
public class DynamoDBInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        runPythonScript("assignment1.py");
        runPythonScript("addStudent.py");
    }

    private void runPythonScript(String scriptName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("python3", scriptName);
        pb.redirectErrorStream(true); // Merge the error stream and the output stream
        Process process = pb.start();

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed: " + scriptName);
        }
    }
}