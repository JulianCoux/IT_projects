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
public class Lower extends AbstractOpIneq {
    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    public void compareCondition(DecacCompiler compiler, Label E) {
        compiler.addInstruction(new BLT(E));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, int registerNumber, boolean not) {
        if(!not)
            compiler.addInstruction(new SLT(Register.getR(registerNumber)));
        else
            compiler.addInstruction(new SGE(Register.getR(registerNumber)));
    }
}
