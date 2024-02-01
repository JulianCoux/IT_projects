package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * read...() statement.
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractReadExpr extends AbstractExpr {
    public AbstractReadExpr() {
        super();
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        compiler.addInstruction(getReadInstruction());
        compiler.addErrorCheck(new Label("io_error"));
        if(registerNumber != 1)
            compiler.addInstruction(new LOAD(Register.R1, Register.getR(registerNumber)));
    }

    protected abstract Instruction getReadInstruction();
}
