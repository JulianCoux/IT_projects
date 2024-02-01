package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public class New extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(Identifier.class);


//    private final AbstractIdentifier operand;
    public New(AbstractIdentifier operand) {
        super(operand);
//        Validate.notNull(operand);
//        this.operand = operand;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        getOperand().decompile(s);
        s.print("()");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr New : start");
        SymbolTable symbolTable = new SymbolTable();
        SymbolTable.Symbol symbol = symbolTable.create(this.getOperand().toString());
        TypeDefinition typDef = compiler.environmentType.get(symbol);
        if(typDef == null){
            throw new ContextualError("Error in the declaration of new " + symbol + "() in New", getLocation());
        }
        this.setType(typDef.getType());
//        Identifier test = new Identifier(this.getType().getName());
//        test.setType(test.verifyType(compiler));
        AbstractIdentifier operand1 = (AbstractIdentifier) getOperand();
        operand1.verifyType(compiler);
        LOG.debug("verifyExpr New : end");
        return getType();
    }

    @Override
    protected String getOperatorName() {
        return "new";
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        compiler.addComment("Instruction " + getOperatorName() + " ligne " + getLocation());
        int classSize = ((Identifier) getOperand()).getClassDefinition().getNumberOfFields() + 1;
        addImaInstruction(compiler, new ImmediateInteger(classSize), Register.getR(registerNumber));
    }

    @Override
    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {
        compiler.addInstruction(new NEW(value, register));
        compiler.addErrorCheck(new Label("tas_plein"));

        Identifier ident = (Identifier) getOperand();
        DAddr methodTableAddr = ident.getClassDefinition().getMethodTableAddress();
        compiler.addInstruction(new LEA(methodTableAddr, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, register)));

        compiler.addInstruction(new PUSH(register));
        compiler.addToStack(1);

        compiler.addInstruction(new BSR(new Label("init." + ident)));
        compiler.addToStack(2);
        compiler.removeFromStack(2);

        compiler.addInstruction(new POP(register));
        compiler.removeFromStack(1);
    }
}
