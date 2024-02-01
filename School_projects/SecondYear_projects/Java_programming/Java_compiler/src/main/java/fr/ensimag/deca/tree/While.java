package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;
import java.util.List;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class While extends AbstractInst {
    private static int whileCount = 0;
    private static final Logger LOG = Logger.getLogger(Identifier.class);
    private AbstractExpr condition;
    private ListInst body;
    private Label returnLabel = null;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("Beginning of WHILE Loop");
        Label startCodeLabel = new Label("E_Debut." + whileCount);
        Label condLabel = new Label("E_Cond." + whileCount);
        whileCount++;

        // Get all the registers used
        List<GPRegister> regsUsed =  getBody().getRegisters();

        // Register Saving
        if(regsUsed != null && !regsUsed.isEmpty()) {
            compiler.addComment("Sauvegarde des registres");
            for (GPRegister reg : regsUsed) {
                compiler.addInstruction(new PUSH(reg));
                compiler.addToStack(1);
            }
        }

        body.setReturnLabel(getReturnLabel());
        compiler.addInstruction(new BRA(condLabel));

        compiler.addLabel(startCodeLabel);
        body.codeGenListInst(compiler);

        compiler.addLabel(condLabel);

        condition.codeGen(compiler, 1);
        compiler.addInstruction(new CMP(0, Register.R1));
        compiler.addInstruction(new BNE(startCodeLabel));

        // Register Restauration
        if(regsUsed != null && !regsUsed.isEmpty()) {
            compiler.addComment("Restauration des registres");
            for (int i = regsUsed.size(); i > 1; i--) {
                compiler.addInstruction(new POP(regsUsed.get(i - 1)));
                compiler.removeFromStack(1);
            }
        }
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.debug("verifyInst While : start");
        condition.verifyExpr(compiler, localEnv, currentClass);
        condition.verifyCondition(compiler, localEnv, currentClass);
        for(AbstractInst instruction : body.getList()){
            instruction.verifyInst(compiler, localEnv, currentClass, returnType);
        }
        LOG.debug("verifyInst While : end");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    public Label getReturnLabel() {
        return returnLabel;
    }

    public void setReturnLabel(Label returnLabel) {
        this.returnLabel = returnLabel;
    }
}
