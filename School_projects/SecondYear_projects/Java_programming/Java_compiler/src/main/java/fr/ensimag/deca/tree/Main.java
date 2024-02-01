package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.HALT;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl25
 * @date 01/01/2024
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verifyMain Main : start");
        EnvironmentExp localEnv = new EnvironmentExp(null);
        // A FAIRE: Appeler méthodes "verify*" de ListDeclVarSet et ListInst.
        // Vous avez le droit de changer le profil fourni pour ces méthodes
        // (mais ce n'est à priori pas nécessaire).
        if (declVariables != null){
            //LOG.debug("     Main : declVariables not null");
            declVariables.verifyListDeclVariable(compiler, localEnv, null);
        } else {
            throw new ContextualError("Liste des declarations de variables est null", getLocation());
        }

        if (insts != null){
            insts.verifyListInst(compiler, localEnv, null, compiler.environmentType.VOID);
        } else {
            throw new ContextualError("Liste des declarations des instructionx null", getLocation());
        }

        LOG.debug("verifyMain Main : end");
    }

    // affichage en flottant de la moitié du carré d'un entier
    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        compiler.addComment("Variables declarations:");
        declVariables.codeGenListVariables(compiler);

        compiler.addComment("Beginning of main instructions:");
        insts.codeGenListInst(compiler);

        compiler.addInstruction(new HALT());
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }

    @Override
    public int getNumGlobalVariables() {
        return declVariables.size();
    }
}
