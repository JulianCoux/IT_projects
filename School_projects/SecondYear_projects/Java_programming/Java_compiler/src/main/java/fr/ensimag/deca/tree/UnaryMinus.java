package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.OPP;
import org.apache.log4j.Logger;

/**
 * @author gl25
 * @date 01/01/2024
 */
public class UnaryMinus extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(Identifier.class);

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr UnaryMinus : start");
        //We want to check that the type is Arith (INT or FLOAT)
        Type type = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if(!type.isInt() && !type.isFloat())
            throw new ContextualError("an Arith type was expected in UnaryMinus", getLocation());
        this.setType(type);
        LOG.debug("verifyExpr UnaryMinus : end");
        return type;
    }

    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(getOperatorName());
        getOperand().decompile(s);
    }

    @Override
    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {
        compiler.addInstruction(new OPP(value, register));
    }

}
