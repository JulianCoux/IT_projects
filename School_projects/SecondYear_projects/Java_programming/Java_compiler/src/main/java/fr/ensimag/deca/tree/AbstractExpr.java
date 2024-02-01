package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractExpr extends AbstractInst {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        LOG.debug("verifyRValue AbstractExpr : start");

        Type type = this.verifyExpr(compiler, localEnv, currentClass);

        if (type.sameType(expectedType)){
            LOG.debug("verifyRValue AbstractExpr : end");
            return this;
        }
        if (type.isInt() && expectedType.isFloat()) {
            LOG.debug("verifyRValue AbstractExpr : end");
            ConvFloat convFloat = new ConvFloat(this);
            convFloat.verifyExpr(compiler, localEnv, currentClass);
            return convFloat;
        }
        if (type.isClass()){
            LOG.debug("verifyRValue AbstractExpr : end");
            return this;
        }
        LOG.debug("verifyRValue AbstractExpr : end");
        throw new ContextualError("Type incompatibility in AbstractExpr", getLocation());
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.debug("verifyInst AbstractExpr : start");
        verifyExpr(compiler, localEnv, currentClass);
        LOG.debug("verifyInst AbstractExpr : end");
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.verifyExpr(compiler, localEnv, currentClass);
        if (!type.isBoolean()) {
            throw new ContextualError(type + " is not a valid condition for IfThenElse", getLocation());
        }
    }

    public boolean areBothArith(Type leftType, Type rightType) throws ContextualError{
        if(isArithType(leftType) && isArithType(rightType)){
            return true;
        }
        return false;
    }

    public boolean isArithType(Type unaryType) throws ContextualError{
        if (unaryType.isFloat() || unaryType.isInt()){
            return true;
        }
        return false;
    }



    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean hex) {
        this.codeGen(compiler, 1);
        if (this.getType() != null) {
            if (this.getType().toString() == "int") {
                compiler.addInstruction(new WINT());
            } else {
                if (hex){
                    compiler.addInstruction(new WFLOATX());
                } else {
                    compiler.addInstruction(new WFLOAT());
                }
            }
        }
    }



    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGen(compiler, 2);
    }

    /**
     * Generate code to resolve the expression and stores the result in the register Rm
     * */
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        throw new UnsupportedOperationException("not yet implemented");
    }
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }

    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {

    }

    /* The division inside expressions should be floats, */
    private boolean finalDivide = true;
}
