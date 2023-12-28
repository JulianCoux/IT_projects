package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

public class Selection extends AbstractBinaryExpr {
    private ListExpr params = new ListExpr();

    public Selection(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    public Selection(AbstractExpr leftOperand, AbstractExpr rightOperand, ListExpr params) {
        super(leftOperand, rightOperand);
        this.params = params;
    }

    @Override
    protected String getOperatorName() {
        return ".";
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public ListExpr getParams() {
        return params;
    }

    public void setParams(ListExpr params) {
        this.params = params;
    }
}
