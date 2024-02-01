package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.BooleanValue;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.log4j.Logger;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr implements BooleanValue {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr AbstractOpCmp : start");
        //On veut vérifier que les deux éléments sont des types Arith
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!this.areBothArith(leftType, rightType) && !(leftType.isBoolean() && rightType.isBoolean())){   //TODO : on peut accepter les types boolean pour les EQ et NEQ
            throw new ContextualError("one operator is not an Arith type in AbstractOpCmp", getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        if(leftType.sameType((rightType))){
            LOG.debug("verifyExpr AbstractOpCmp : end");
            return compiler.environmentType.BOOLEAN;
        }
        if(leftType.isFloat() || rightType.isFloat()){
            if (leftType.isFloat()){
                ConvFloat conv_float = new ConvFloat(getRightOperand());
                conv_float.verifyExpr(compiler, localEnv, currentClass);
                setRightOperand(conv_float);
            } else {
                ConvFloat conv_float = new ConvFloat(getLeftOperand());
                conv_float.verifyExpr(compiler, localEnv, currentClass);
                setLeftOperand(conv_float);
            }
            LOG.debug("verifyExpr AbstractOpCmp : end");
            return compiler.environmentType.BOOLEAN;
        }
        LOG.debug("verifyExpr AbstractOpCmp : end");
        throw new ContextualError("Type error in AbstractOpCmp", getLocation());
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        super.codeGen(compiler, registerNumber);

        codeGenBool(compiler, registerNumber, false);
    }

    private static int countDeclLabels = 0;

    protected abstract void codeGenBool(DecacCompiler compiler, int registerNumber, boolean not);

    @Override
    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {
        compiler.addInstruction(new CMP(value, register));
    }

    // Compare the condition and branch to the label E if true
    public abstract void compareCondition(DecacCompiler compiler, Label E);

    @Override
    public void codeGenNot(DecacCompiler compiler, int registerNumber) {
        super.codeGen(compiler, registerNumber);

        codeGenBool(compiler, registerNumber, true);
    }
}
