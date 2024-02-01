package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class And extends AbstractOpBool {
    private static int andCount = 0;

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    public void codeGenIfBranch(DecacCompiler compiler, Label ifLabel, Label elseLabel) {
        compiler.addComment("Beginning of AND");
        // Check first operand
        getLeftOperand().codeGen(compiler, 2);
        compiler.addInstruction(new CMP(0, Register.R2));
        compiler.addInstruction(new BEQ(elseLabel));

        // Check second operand
        getRightOperand().codeGen(compiler, 2);
        compiler.addInstruction(new CMP(0, Register.R2));
        compiler.addInstruction(new BNE(ifLabel));
        compiler.addInstruction(new BRA(elseLabel));
    }

    public void codeGenBool(DecacCompiler compiler, GPRegister register, boolean not) {
        // Create the label to the code that sets the boolean result
        Label isTrue = new Label("AND_bool." + andCount);
        Label isFalse = new Label("AND_not_bool." + andCount);
        Label end = new Label("AND_bool_fin." + andCount);
        andCount++;

        // Generate the code to resolve the operands (They'll branch to isTrue if they are true)
        if(!not) // Inverts true and false if called by the Not operator
            codeGenIfBranch(compiler, isTrue, isFalse);
        else
            codeGenIfBranch(compiler, isFalse, isTrue);

        compiler.addLabel(isTrue);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), register));
        compiler.addInstruction(new BRA(end));

        compiler.addLabel(isFalse);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), register));

        compiler.addLabel(end);
    }
}

