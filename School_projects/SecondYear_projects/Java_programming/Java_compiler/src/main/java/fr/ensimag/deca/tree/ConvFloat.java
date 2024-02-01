package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import org.apache.log4j.Logger;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl25
 * @date 01/01/2024
 */
public class ConvFloat extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr ConvFloat : start");
//        getOperand().verifyExpr(compiler, localEnv, currentClass);
        //TODO : avec le if ça ne marche pas
//        if(type.isInt() || type.isFloat())
//            throw new ContextualError("a Arith type was expected in ConvFloat", getLocation());
        this.setType(compiler.environmentType.FLOAT);
        LOG.debug("verifyExpr ConvFloat : end");
        return compiler.environmentType.FLOAT;
    }

    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName());
        getOperand().decompile(s);
    }

    @Override
    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {
        compiler.addInstruction(new FLOAT(value, register));
    }
}
