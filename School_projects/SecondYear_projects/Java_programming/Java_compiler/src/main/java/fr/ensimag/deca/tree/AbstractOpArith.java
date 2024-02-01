package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import org.apache.log4j.Logger;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Type new_type(DecacCompiler compiler, Type leftType, Type rightType, EnvironmentExp localEnv,
                         ClassDefinition currentClass) throws ContextualError{
        //We want to check that the type is either Int or Float, and if it is neither, we throw an exception
        if(!this.isArithType(leftType) || !this.isArithType(rightType)){
            throw new ContextualError("The type is neither Int nor Float in AbstractOpArith", getLocation());
        }
        if(leftType.sameType((rightType))){
            return leftType;
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
            return compiler.environmentType.FLOAT;
        }

        return leftType;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr AbstractOpArith : start");
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        Type newType = new_type(compiler, leftType, rightType, localEnv, currentClass);
        setType(newType);
        LOG.debug("verifyExpr AbstractOpArith : end");
        return newType;
    }
}
