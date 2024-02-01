package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.SEQ;
import fr.ensimag.ima.pseudocode.instructions.SNE;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class Equals extends AbstractOpExactCmp {
    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    public void compareCondition(DecacCompiler compiler, Label E) {
        compiler.addInstruction(new BEQ(E));
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, int registerNumber, boolean not) {
        if(!not)
            compiler.addInstruction(new SEQ(Register.getR(registerNumber)));
        else
            compiler.addInstruction(new SNE(Register.getR(registerNumber)));
    }
}