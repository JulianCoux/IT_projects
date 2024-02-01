package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

public class InstanceOf extends AbstractOpExactCmp {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);

    public InstanceOf(AbstractExpr leftOperand, AbstractIdentifier rightOperand) {
        super(leftOperand, rightOperand);
    }

    public AbstractExpr getObject() {
        return getLeftOperand();
    }

    public AbstractIdentifier getClassName() {
        return (AbstractIdentifier) getRightOperand();
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr InstanceOf : start");
        //On veut vérifier que les deux éléments sont des types Arith
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getClassName().verifyType(compiler);

        this.setType(compiler.environmentType.BOOLEAN);
        if(leftType.sameType((rightType))){
            LOG.debug("verifyExpr InstanceOf : end");
            return compiler.environmentType.BOOLEAN;
        }

        LOG.debug("verifyExpr InstanceOf : end");
        throw new ContextualError("Type error in AbstractOpCmp", getLocation());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        getLeftOperand().decompile(s);
        s.print(" instanceof ");
        getRightOperand().decompile(s);
        s.print(")");
    }

    @Override
    protected String getOperatorName() {
        return "instanceof";
    }

    @Override
    public void compareCondition(DecacCompiler compiler, Label E) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGenBool(compiler, 2, false);
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        codeGenBool(compiler, registerNumber, false);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, int registerNumber, boolean not) {
        GPRegister register = Register.getR(registerNumber);
        // If its checking that it is an object, return true
        if(getClassName().toString().equals("Object"))
            compiler.addInstruction(new LOAD(1, register));
        else {
            Label instanceTrue = new Label("INSTANCE_true." + instanceCounter);
            Label instanceFalse = new Label("INSTANCE_false." + instanceCounter);
            Label instanceLoop = new Label("INSTANCE_start_loop." + instanceCounter);
            Label instanceEnd = new Label("INSTANCE_fin." + instanceCounter);
            instanceCounter++;

            // Check if the object is null
            getObject().codeGen(compiler, register.getNumber());
            compiler.addInstruction(new CMP(new NullOperand(), register));
            compiler.addInstruction(new BEQ(instanceFalse));

            // Get the address in the heap,
            compiler.addInstruction(new LOAD(new RegisterOffset(0, register), register));
            // Get method table of the class
            compiler.addInstruction(new LEA(new RegisterOffset(0, register), register)); // Maybe a LEA

            // Get the address of the method table of object
            DAddr methodTableAddr = ((ClassDefinition) compiler.environmentType.defOfType(getClassName().getName())).getMethodTableAddress();
            compiler.addInstruction(new LEA(methodTableAddr, Register.R1)); // Maybe a LEA

            // Add label of the while loop
            compiler.addLabel(instanceLoop);

            // Check the address of the method table is false
            compiler.addInstruction(new CMP(new NullOperand(), register));
            compiler.addInstruction(new BEQ(instanceFalse));

            // If the method tables are equal, than true
            compiler.addInstruction(new CMP(Register.R1, register));
            compiler.addInstruction(new BEQ(instanceTrue));

            // Go one level deeper, go to the begging of the while loop
            compiler.addInstruction(new LOAD(new RegisterOffset(0, register), register));
            compiler.addInstruction(new BRA(instanceLoop));

            // Put the label of returning true
            compiler.addLabel(instanceTrue);
            compiler.addInstruction(new LOAD((!not) ? 1 : 0, register));
            compiler.addInstruction(new BRA(instanceEnd));

            // Put the label of returning false
            compiler.addLabel(instanceFalse);
            compiler.addInstruction(new LOAD((!not) ? 0 : 1, register));

            // Put the label of end
            compiler.addLabel(instanceEnd);
        }
    }

    @Override
    public void codeGenNot(DecacCompiler compiler, int registerNumber) {
        codeGenBool(compiler, registerNumber, true);
    }

    private static int instanceCounter = 0;
}
