package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl25
 * @date 01/01/2024
 */
public class Initialization extends AbstractInitialization {
    private static final Logger LOG = Logger.getLogger(Identifier.class);

    @Override
    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        LOG.debug("verifyInitialization Initialization : start");
        Type exprType = expression.verifyExpr(compiler, localEnv, currentClass);
        if(!exprType.sameType(t) && !(exprType.isInt() && t.isFloat())){
            throw new ContextualError("not the Type expected in Initialization", getLocation());
        }
        this.expression = this.expression.verifyRValue(compiler, localEnv, currentClass, t);
        LOG.debug("verifyInitialization Initialization : end");
        //TODO mettre des setDefinition
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print(" = ");
        expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }
}
