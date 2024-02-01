package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import org.apache.log4j.Logger;

import java.io.PrintStream;

public class Return extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(Identifier.class);
    private AbstractExpr expression;
    private Label endMethod;

    public Return(AbstractExpr expression) {
        this.expression = expression;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
                              ClassDefinition currentClass, Type returnType) throws ContextualError {
        LOG.debug("verifyInst Return : start");

        /* Should be a run time error for some reason
        //verifier que return != void
        if(returnType.isVoid()){
            throw new ContextualError("return type is VOID in Return", getLocation());
        } */

        expression.verifyRValue(compiler, localEnv, currentClass, returnType);
        LOG.debug("verifyInst Return : end");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        expression.codeGen(compiler, 0);

        compiler.addInstruction(new BRA(endMethod));
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        expression.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    public void setEndMethod(Label endMethod) {
        this.endMethod = endMethod;
    }

    public Label getEndMethod() {
        return endMethod;
    }
}
