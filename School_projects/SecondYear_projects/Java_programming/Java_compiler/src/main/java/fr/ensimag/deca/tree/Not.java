package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BooleanValue;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import org.apache.log4j.Logger;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class Not extends AbstractUnaryExpr implements BooleanValue {
    private static final Logger LOG = Logger.getLogger(Identifier.class);
    public Not(AbstractExpr operand) {
        super(operand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr Not : start");
        Type type = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!type.isBoolean())
            throw new ContextualError("a BOOL was expected in Not", getLocation());
        this.setType(type);
        LOG.debug("verifyExpr Not : end");
        return type;
    }

    @Override
    protected String getOperatorName() {
        return "!";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName());
        getOperand().decompile(s);
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        if(getOperand() instanceof BooleanValue)
            ((BooleanValue) getOperand()).codeGenNot(compiler, registerNumber);
    }

    @Override
    public void codeGenNot(DecacCompiler compiler, int registerNumber) {
        getOperand().codeGen(compiler, registerNumber);
    }
}
