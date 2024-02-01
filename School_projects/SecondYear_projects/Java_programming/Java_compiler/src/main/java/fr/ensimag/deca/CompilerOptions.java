package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl25
 * @date 01/01/2024
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    public boolean getParse() { return parse;}
    public boolean getVerification() { return verification;}
    public boolean getNoCheck() { return noCheck;}
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public int getRegisterX() {
        return registerX;
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false;
    private boolean verification = false;
    private boolean noCheck = false;
    private List<File> sourceFiles = new ArrayList<File>();
    private int registerX;

    
    public void parseArgs(String[] args) throws CLIException {
        // A FAIRE : parcourir args pour positionner les options correctement.
        parcoursArgs(args);

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

    }

    private void parcoursArgs(String[] args) throws CLIException {
        //Application des options
        int j = 0;
        for (int i = 0; i < args.length; i++){
            if (args[i].charAt(0) != '-') {
                j = i;
                break;
            }
            switch (args[i]){
                case "-b":
                    printBanner = true;
                    break;
                case "-p":
                    parse = true;
                    break;
                case "-v":
                    verification = true;
                    break;
                case "-n":
                    noCheck = true;
                    break;
                case "-r": // TODO: It isn't recognized in the terminal
                    i++;
                    j = i;
                    int x = Integer.parseInt(args[i]);
                    if(x < 4 || x > 16)
                        throw new CLIException("The number of register can't be less than 4 or greater than 16");

                    registerX = x;
                    break;
                case "-d":
                    debug++;
                    break;
                case "-P":
                    parallel = true;
                    break;
            }
        }
        //Ajout des fichiers src dans sourceFiles
        for (int k = j; k < args.length; k++){
            sourceFiles.add(new File(args[k]));
        }
    }

    protected void displayUsage() {
        System.out.println("-b      (banner)        : displays a banner with the team name");
        System.out.println("-p      (parse)         : stops decac after the tree construction step\n" +
                "                          step, and displays the decompiled tree\n" +
                "                          (i.e. if there is only one source file to compile\n" +
                "                          to compile, the output should be a syntactically correct\n" +
                "                          deca program)");
        System.out.println("-v      (verification)  : stops decac after verification step\n" +
                "                          (produces no output if no error)");
        System.out.println("-n      (no check)      : removes the runtime tests specified in\n" +
                "                          11.1 and 11.3 of the Deca semantics.");
        System.out.println("-r X    (registers)     : limits available unmarked registers to\n" +
                "                          R0 ... R{X-1}, with 4 <= X <= 16");
        System.out.println("-d      (debug)         : enables debug traces. Repeat\n" +
                "                          several times to get more\n" +
                "                          traces.");
        System.out.println("-P      (parallel)      : if there are several source files,\n" +
                "                          starts compiling the files \n" +
                "                          in parallel (to speed up compilation)");
        System.out.println("Note: The '-p' and '-v' options are incompatible.");
    }
}
