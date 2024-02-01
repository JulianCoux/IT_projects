package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WINT;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Single precision, floating-point literal
 *
 * @author gl25
 * @date 01/01/2024
 */
public class FloatLiteral extends AbstractLiteral {
    private static final Logger LOG = Logger.getLogger(Program.class);
    public float getValue() {
        return value;
    }

    public DVal getDValue() {
        return new ImmediateFloat(getValue());
    }

    private float value;

    public FloatLiteral(float value) {
        Validate.isTrue(!Float.isInfinite(value),
                "literal values cannot be infinite");
        Validate.isTrue(!Float.isNaN(value),
                "literal values cannot be NaN");
        this.value = value;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        //LOG.debug("verifyExpr FloatLiteral : start");
        setType(compiler.environmentType.FLOAT);
        //LOG.debug("verifyExpr FloatLiteral : end");
        return compiler.environmentType.FLOAT;
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(Float.toString(value));
    }

    @Override
    String prettyPrintNode() {
        return "Float (" + getValue() + ")";
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

}
