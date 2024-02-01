package fr.ensimag.deca.syntax;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

public class TestLex {

    @Test
    public void testCommandLineOnFilesValid() {
        String folderPath = "../../../../../deca/syntax/ownTests/valid/";

        // On récupère tous les fichiers du dossier
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Pour chaque fichier, exécute la commande et vérifie que la sortie est bien Test_lex [OK]
                    testCommandOnFile(file);
                }
            }
        }
    }

    @Test
    public void testCommandLineOnFilesInvalid() {
        // Spécifiez le chemin du deuxième dossier que vous souhaitez parcourir
        String errorFolderPath = "../../../../../deca/syntax/ownTests/invalid/";

        File errorFolder = new File(errorFolderPath);
        File[] errorFiles = errorFolder.listFiles();

        if (errorFiles != null) {
            for (File file : errorFiles) {
                if (file.isFile()) {
                    // Pour chaque fichier, exécutez la commande et vérifiez qu'on est une erreur
                    testCommandOnFileWithError(file);
                }
            }
        }
    }

    private void testCommandOnFileWithError(File file) {
        try {
            String command = "python3 ../../../../../../../lexer_test.py " + file.getAbsolutePath();

            Process process = Runtime.getRuntime().exec(command);

            // Attendre que le processus se termine
            int exitCode = process.waitFor();

            // Obtenez la sortie standard du processus
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder inputOutput = new StringBuilder();
            String line;
            while ((line = inputReader.readLine()) != null) {
                inputOutput.append(line).append("\n");
            }

            // Vérifier si le processus a retourné une erreur (exit code non nul)
            if (exitCode != 0) {
                // Imprimez ou traitez la sortie standard comme nécessaire
                System.err.println("Erreur lors de l'exécution de la commande sur le fichier " + file.getName());
                System.err.println("Sortie standard : " + inputOutput.toString());
                fail("Erreur lors de l'exécution de la commande sur le fichier " + file.getName());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Erreur lors de l'exécution de la commande sur le fichier " + file.getName());
        }
    }
    

    private void testCommandOnFile(File file) {
        try {
            String command = "python3 ../../../../../../../lexer_test.py " + file.getAbsolutePath();

            // On exécute la commande
            Process process = Runtime.getRuntime().exec(command);

            // On récupère la sortie
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Attendez la fin du processus
            process.waitFor();

            // Vérifiez si la sortie est "Test_lex [OK]"
            assertEquals("Test_lex [OK]", output.toString().trim());

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            fail("Erreur lors de l'exécution de la commande sur le fichier " + file.getName());
        }
    }

    public static void main(String[] args) {
        // Vous pouvez exécuter vos tests ici si nécessaire
    }
}
