package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.log4j.Logger;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl25
 * @date 01/01/2024
 */
public class Assign extends AbstractBinaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    // The class that this expression is located
    private ClassDefinition currentClass;

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr Assign : start");
        Type leftOp = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        AbstractExpr expr = getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftOp);
        LOG.debug("verifyExpr Assign : end");
        setType(expr.getType());
        setCurrentClass(currentClass);
        return expr.getType();
    }

    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        compiler.addComment("Instruction " + getOperatorName() + " ligne " + getLocation());
        GPRegister[][] allRegisters = Register.getUsableRegisters(2, registerNumber);
        pushRegisters(compiler, allRegisters[0]);

        // What a horrible code
        GPRegister resultReg= (allRegisters[1][0].getNumber() == registerNumber) ? allRegisters[1][0] : allRegisters[1][1];
        GPRegister processReg = (allRegisters[1][1].getNumber() == registerNumber) ? allRegisters[1][0] : allRegisters[1][1];

        AbstractLValue leftOperandId = (AbstractLValue) getLeftOperand();
        DAddr leftAddress = leftOperandId.getAddress();

        getRightOperand().codeGen(compiler, processReg.getNumber());
        if(getLeftOperand().getDefinition().isField()) {
            int fieldIndex;

            if(getLeftOperand() instanceof Selection)
                fieldIndex = ((Selection) getLeftOperand()).getIndex();
            else
                fieldIndex = ((AbstractIdentifier) getLeftOperand()).getFieldDefinition().getIndex();

            // Check if it is in the main or in a method
            if(currentClass != null && getLeftOperand() instanceof AbstractIdentifier) { // TODO: Add case where the assign is in a class in a different class than the obj in Selection
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), resultReg));
            } else {
                getLeftOperand().codeGenLValue(compiler, resultReg.getNumber());
            }

            compiler.addInstruction(new STORE(processReg, new RegisterOffset(fieldIndex, resultReg)));
        } else {
            getRightOperand().codeGen(compiler, resultReg.getNumber());
            compiler.addInstruction(new STORE(resultReg, leftAddress));
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGen(compiler, 2);
    }

    public void decompile(IndentPrintStream s) {
        getLeftOperand().decompile(s);
        s.print(" " + getOperatorName() + " ");
        getRightOperand().decompile(s);
    }

    public ClassDefinition getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(ClassDefinition currentClass) {
        this.currentClass = currentClass;
    }
}
