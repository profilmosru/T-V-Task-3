package su.mirea.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BDDAssistant {
    private StringBuilder scriptBuilder;
    public StringBuilder scriptBuilderCopy;

    public BDDAssistant() {
        scriptBuilder = new StringBuilder();
    }

    public BDDAssistant freeze(){
        if (scriptBuilderCopy != null){
            throw  new RuntimeException("Already frozen.");
        }
        scriptBuilderCopy = scriptBuilder;
        scriptBuilder = new StringBuilder();
        return this;
    }

    public BDDAssistant unfreeze(){
        if (scriptBuilderCopy == null){
            throw  new RuntimeException("Already wake.");
        }
        scriptBuilder = scriptBuilderCopy;
        scriptBuilderCopy = null;
        return this;
    }

    public BDDAssistant loadScenarioFromFile(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            scriptBuilder.append(line).append(System.lineSeparator());
        }
        return this;
    }

    public BDDAssistant story(String story) {
        scriptBuilder.append("Story: ").append(story).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant asA(String role) {
        scriptBuilder.append("As a ").append(role).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant inOrderTo(String goal) {
        scriptBuilder.append("In order to ").append(goal).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant iWantTo(String action) {
        scriptBuilder.append("I want to ").append(action).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant scenario(String scenario) {
        scriptBuilder.append(System.lineSeparator()).append("Scenario: ").append(scenario).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant given(String given) {
        scriptBuilder.append("Given ").append(given).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant when(String when) {
        scriptBuilder.append("When ").append(when).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant then(String then) {
        scriptBuilder.append("Then ").append(then).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant but(String but) {
        scriptBuilder.append("But ").append(but).append(System.lineSeparator());
        return this;
    }

    public BDDAssistant and(String and) {
        scriptBuilder.append("And ").append(and).append(System.lineSeparator());
        return this;
    }

   /* public boolean matchScript(String expectedScript) {
        return scriptBuilder.toString().trim().equals(expectedScript.trim());
    } */
   public boolean matchScript(String expectedScript) {
       String[] expectedLines = expectedScript.trim().split("\n");
       String[] actualLines = scriptBuilder.toString().trim().split("\n");

       if (expectedLines.length != actualLines.length) {
           // The number of lines is different, so the scripts don't match
           return false;
       }

       for (int i = 0; i < expectedLines.length; i++) {
           if (!expectedLines[i].equals(actualLines[i])) {
               // The lines are different, so the scripts don't match
               System.out.println("Difference at line " + (i + 1));
               System.out.println("Expected: " + expectedLines[i]);
               System.out.println("Actual: " + actualLines[i]);
               return false;
           }
       }

       // All lines match, so the scripts match
       return true;
   }
    public void writeScriptToFile(String filePath) {
        try {
            Files.write(Paths.get(filePath), getScript().getBytes());
            System.out.println("Script written to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to write script to file: " + e.getMessage());
        }
    }
    public String readScriptFromFile(String filePath) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            return new String(bytes);
        } catch (IOException e) {
            System.out.println("Failed to read script from file: " + e.getMessage());
            return null;
        }
    }
    public String getScript() {
        return scriptBuilder.toString().trim();
    }
}