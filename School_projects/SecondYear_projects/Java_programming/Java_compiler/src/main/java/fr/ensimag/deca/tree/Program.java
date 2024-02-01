package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodName;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl25
 * @date 01/01/2024
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclClass classes, AbstractMain main) {
        Validate.notNull(classes);
        Validate.notNull(main);
        this.classes = classes;
        this.main = main;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclClass classes;
    private AbstractMain main;
    private static int indexGB = 3;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verifyProgram Program: start");
        getClasses().verifyListClass(compiler);
        getClasses().verifyListClassMembers(compiler);
        getClasses().verifyListClassBody(compiler);
        getMain().verifyMain(compiler);
        LOG.debug("verifyProgram Program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        // Create the Method Table
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("      Construction des tables des methodes        ");
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("Construction de la table des methodes de Object");
        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(1, Register.GB)));
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(2, Register.GB)));

        HashMap<String, Integer> methodTableOffsetMap = new HashMap<String, Integer>();
        methodTableOffsetMap.put("Object", 1);

        for(AbstractDeclClass declClass : getClasses().getList()) {
            compiler.addComment("Construction de la table des methodes de " + declClass.getName());
            // Inherent the classes of the superclass
            getClasses().updateMethodNames(declClass);
            // Update the address of the method table of the class so the children can access it
            methodTableOffsetMap.put(declClass.getName().toString(), getIndexGB());

            // Add the method table of the parent
            String superName = declClass.getSuperclass().toString();
            RegisterOffset superOffset = new RegisterOffset(methodTableOffsetMap.get(superName), Register.GB);
            compiler.addInstruction(new LEA(superOffset, Register.R0));
            RegisterOffset currentOffset = new RegisterOffset(getIndexGB(), Register.GB);
            compiler.addInstruction(new STORE(Register.R0, currentOffset));
            declClass.getName().getClassDefinition().setMethodTableAddress(currentOffset);

            // Add each method
            for(MethodName methodName : declClass.getMethodNames()) {
                incrementIndexGB();

                LabelOperand codeLabel = new LabelOperand(methodName.toCodeLabel());
                compiler.addInstruction(new LOAD(codeLabel, Register.R0));
                compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(getIndexGB(), Register.GB)));
            }

            incrementIndexGB();
        }

        // Add the Method table and the variable to the currentStack
        compiler.addToStack(getIndexGB() - 2 + main.getNumGlobalVariables());

        // Create Main Program
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("           Code du programme principal            ");
        compiler.addComment("--------------------------------------------------");

        main.codeGenMain(compiler);

        // Add The methods
        addObjectsCode(compiler);
        getClasses().addClassesMethods(compiler);

        // Add header test
        compiler.addFirst(new ADDSP(getIndexGB() + main.getNumGlobalVariables() - 2));
        if(!compiler.getCompilerOptions().getNoCheck()) {
            compiler.addFirst(new BOV(new Label("pile_pleine")));
            compiler.addFirst(new TSTO(compiler.getMaxStack()));
        }

        addCompleteErrorHandles(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }

    private void addObjectsCode(DecacCompiler compiler) {
        compiler.addComment("--------------------------------------------------");
        compiler.addComment("                  Class Object                   ");
        compiler.addComment("--------------------------------------------------");

        compiler.addComment("---------- Code de la methode equals dans la classe Object");
        compiler.addLabel(new Label("code.Object.equals"));
        if(!compiler.getCompilerOptions().getNoCheck()) {
            compiler.addInstruction(new TSTO(new ImmediateInteger(1)));
            compiler.addInstruction(new BOV(new Label("pile_pleine")));
        }

        RegisterOffset thisParam = new RegisterOffset(-2, Register.LB);
        RegisterOffset firstParam = new RegisterOffset(-3, Register.LB);

        compiler.addInstruction(new LOAD(thisParam, Register.R0));
        compiler.addInstruction(new CMP(firstParam,Register.R0));
        compiler.addInstruction(new SEQ(Register.R0));

        compiler.addInstruction(new RTS());
    }

    public static int getIndexGB() {
        return indexGB;
    }

    public static void incrementIndexGB() {
        indexGB++;
    }

    public static void decrementIndexGB() {
        indexGB--;
    }

    private void addCompleteErrorHandles(DecacCompiler compiler) {
        if(compiler.getCompilerOptions().getNoCheck())
            return;
        addErrorHandle(compiler, "Erreur: Pile pleine ", new Label("pile_pleine"));
        addErrorHandle(compiler, "Erreur: Dereferencement de null", new Label("dereferencement_null"));
        addErrorHandle(compiler, "Erreur: Input/Output error", new Label("io_error"));
        addErrorHandle(compiler, "Erreur: Tas plein", new Label("tas_plein"));
        addErrorHandle(compiler, "Erreur: Division par zero", new Label("division_par_zero"));
        addErrorHandle(compiler, "Erreur: Résultat n’est pas codable sur un flottant", new Label("float_imprecis"));
    }

    private void addErrorHandle(DecacCompiler compiler, String message, Label label) {
        String strMessage = "Message d'erreur : " + label.toString();
        compiler.addComment("--------------------------------------------------");
        compiler.addComment(StringUtils.center(strMessage, 75 - strMessage.length()));
        compiler.addComment("--------------------------------------------------");
        compiler.addLabel(label);
        compiler.addInstruction(new WSTR(message));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
    }
}
