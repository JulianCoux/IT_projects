package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class NotEquals extends AbstractOpExactCmp {
    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    public void compareCondition(DecacCompiler compiler, Label E) {
        compiler.addInstruction(new BNE(E));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, int registerNumber, boolean not) {
        if(!not)
            compiler.addInstruction(new SNE(Register.getR(registerNumber)));
        else
            compiler.addInstruction(new SEQ(Register.getR(registerNumber)));
    }
}
