package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import java.io.PrintStream;

public class Selection extends AbstractLValue {
    private static final Logger LOG = Logger.getLogger(Identifier.class);
    private AbstractExpr obj;
    private AbstractIdentifier field;
    private int index;

    public Selection(AbstractExpr obj, AbstractIdentifier field) {
        this.obj = obj;
        this.field = field;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr Selection : start");
        Type typeObj = obj.verifyExpr(compiler, localEnv, currentClass);
        ClassDefinition classDefinition = compiler.environmentType.defOfType(typeObj.getName()).asClassDefinition(typeObj.toString() + " is not a class", this.getLocation());
        obj.setType(classDefinition.getType());

        FieldDefinition defField;
        if (obj instanceof This) {
            defField = currentClass.getMembers().get(field.getName()).asFieldDefinition(field + " is not a field", this.getLocation());
        } else if(obj instanceof Identifier || obj instanceof Selection) {
            ClassDefinition classDef = obj.getType().asClassType(obj + " is not an object to select from", getLocation()).getDefinition();
            defField = classDef.getMembers().get(field.getName()).asFieldDefinition(field + " is not a field", this.getLocation());
        } else
            throw new ContextualError("Selection of non-object", getLocation());

        field.setDefinition(defField);
        setIndex(defField.getIndex());
        setDefinition(defField);

        if(defField.getVisibility() == Visibility.PROTECTED){
            ClassType currentType = currentClass.getType();
            if (currentType == null || !typeObj.isSubType(currentClass.getType())) {
                throw new ContextualError(currentType + " is not a subclass of " + typeObj, getLocation());
            }
        } else if(defField.getVisibility() == Visibility.PUBLIC) {
            return defField.getType();
        }

        LOG.debug("verifyExpr Selection : end");
        return defField.getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        obj.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        codeGenLValue(compiler, registerNumber);
        GPRegister register = Register.getR(registerNumber);
        compiler.addInstruction(new LOAD(new RegisterOffset(getIndex() , register), register));
    }

    @Override
    public void codeGenLValue(DecacCompiler compiler, int registerNumber) {
        GPRegister register = Register.getR(registerNumber);

        getObj().codeGen(compiler, registerNumber);

        compiler.addInstruction(new CMP(new NullOperand(), register));
        if(!compiler.getCompilerOptions().getNoCheck())
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
    }

    public AbstractExpr getObj() {
        return obj;
    }

    public AbstractIdentifier getField() {
        return field;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public Type getType() {
        return field.getType();
    }
}
