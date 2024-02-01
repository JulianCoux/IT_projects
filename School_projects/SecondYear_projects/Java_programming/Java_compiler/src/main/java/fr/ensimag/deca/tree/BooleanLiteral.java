package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BooleanValue;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.log4j.Logger;

import java.io.PrintStream;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class BooleanLiteral extends AbstractLiteral implements BooleanValue {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //LOG.debug("verifyExpr BooleanLiteral : start");
        setType(compiler.environmentType.BOOLEAN);
        //LOG.debug("verifyExpr BooleanLiteral : end");
        return compiler.environmentType.BOOLEAN;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Boolean.toString(value));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    String prettyPrintNode() {
        return "BooleanLiteral (" + value + ")";
    }

    @Override
    public DVal getDValue() {
        return new ImmediateInteger((getValue()) ? 1 : 0);
    }


    @Override
    public void codeGenNot(DecacCompiler compiler, int registerNumber) {
        compiler.addInstruction(new LOAD(new ImmediateInteger((getValue()) ? 0: 1), Register.getR(registerNumber)));
    }
}
