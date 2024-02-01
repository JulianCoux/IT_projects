package fr.ensimag.deca;

import java.io.File;

import fr.ensimag.ima.pseudocode.Register;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl25
 * @date 01/01/2024
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    private static void compileFilesInParallel(CompilerOptions options) {
        List<Callable<Boolean>> compilationTasks = new ArrayList<>();

        for (File source : options.getSourceFiles()) {
            compilationTasks.add(() -> {
                DecacCompiler compiler = new DecacCompiler(options, source);
                return compiler.compile();
            });
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try {
            List<Future<Boolean>> results = executor.invokeAll(compilationTasks);

            // Traiter les résultats, gérer les erreurs si nécessaire
            for (Future<Boolean> result : results) {
                if (result.get()) {
                    // Gérer les erreurs ou effectuer d'autres opérations après la compilation.
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Gérer les exceptions selon les besoins
        } finally {
            executor.shutdown();
        }
    }
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
            Register.setMaxRegister(options.getRegisterX());
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            if (options.getParse() || options.getVerification() || options.getParallel() || options.getDebug() > 0){ // ATTENTION A PAS OUBLIER -n et -r quand ça sera fait
                System.out.println("The '-b' option can only be used without any other option.");
            } else {
                System.out.println("Projet GL : gl25");
            }
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
        }
        if (options.getParallel()) {
            compileFilesInParallel(options);
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
