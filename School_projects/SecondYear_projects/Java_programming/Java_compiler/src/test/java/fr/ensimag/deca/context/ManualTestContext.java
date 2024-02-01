package fr.ensimag.deca.context;

import fr.ensimag.deca.CompilerOptions;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.syntax.AbstractDecaLexer;
import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;
import java.io.IOException;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Driver to test the contextual analysis (together with lexer/parser)
 * 
 * @author Ensimag
 * @date 01/01/2024
 */
public class ManualTestContext {
    public static void main(String[] args) throws IOException {
        Logger.getRootLogger().setLevel(Level.DEBUG);
        DecaLexer lex = AbstractDecaLexer.createLexerFromArgs(args);
        CommonTokenStream tokens = new CommonTokenStream(lex);
        DecaParser parser = new DecaParser(tokens);
        DecacCompiler compiler = new DecacCompiler(new CompilerOptions(), null);
        parser.setDecacCompiler(compiler);
        AbstractProgram prog = parser.parseProgramAndManageErrors(System.err);
        if (prog == null) {
            System.exit(1);
            return; // Unreachable, but silents a warning.
        }
        try {
            prog.verifyProgram(compiler);
        } catch (LocationException e) {
            e.display(System.err);
            System.exit(1);
        }
        prog.prettyPrint(System.out);


        System.out.println("test_codeGen\n");
        prog.codeGenProgram(compiler);
        System.out.println(compiler.displayIMAProgram());

        System.out.println("\nSortie standard :");
        try {
            String premierArgument = args[0];
            int indicePoint = premierArgument.lastIndexOf(".");
            String premierArgumentAss = premierArgument.substring(0, indicePoint) + ".ass";
            String command1 = "/src/main/bin/decac " + premierArgument;
            String command2 = "../global/bin/ima " + premierArgumentAss;

            // Création du process builder pour la première commande (decac)
            ProcessBuilder processBuilder1 = new ProcessBuilder("/bin/bash", "-c", command1);

            // Rediriger la sortie standard du processus vers le flux de sortie du programme Java
            processBuilder1.redirectErrorStream(true);

            // Démarrer le processus 1 (decac)
            Process process1 = processBuilder1.start();

            // Attendre la fin du processus 1 (decac)
            int exitCode1 = process1.waitFor();

            // Création du process builder pour la deuxième commande (ima)
            ProcessBuilder processBuilder2 = new ProcessBuilder("/bin/bash", "-c", command2);

            // Rediriger la sortie standard du processus vers le flux de sortie du programme Java
            processBuilder2.redirectErrorStream(true);

            // Démarrer le processus 2 (ima)
            Process process2 = processBuilder2.start();

            // Lire la sortie du processus 2 (ima)
            BufferedReader reader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Attendre la fin du processus 2 (ima)
            int exitCode2 = process2.waitFor();


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
