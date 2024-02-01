package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BooleanValue;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import org.apache.log4j.Logger;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractOpBool extends AbstractBinaryExpr implements BooleanValue {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr AbstractOpBool : start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        //Check that both have the same type
        if(leftType.isBoolean() && leftType.sameType(rightType)){
            setType(compiler.environmentType.BOOLEAN);
            LOG.debug("verifyExpr AbstractOpBool : end");
            return leftType;
        }
        throw new ContextualError("a BOOL was expected in AbstractOpBool", getLocation());
    }

    public abstract void codeGenBool(DecacCompiler compiler, GPRegister register, boolean not);

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        super.codeGen(compiler, registerNumber);
        codeGenBool(compiler, Register.getR(registerNumber), false);
    }

    @Override
    public void codeGenNot(DecacCompiler compiler, int registerNumber) {
        super.codeGen(compiler, registerNumber);
        codeGenBool(compiler, Register.getR(registerNumber), true);
    }
}
