package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

public abstract class AbstractLiteral extends AbstractExpr{
    public abstract DVal getDValue();

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        compiler.addInstruction(new LOAD(getDValue(), Register.getR(registerNumber)));
    }
}
