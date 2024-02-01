package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.INT;
import org.apache.log4j.Logger;

public class Cast extends AbstractUnaryExpr {
    private static final Logger LOG = Logger.getLogger(AbstractExpr.class);
    private AbstractIdentifier type;

    public Cast(AbstractExpr operand, AbstractIdentifier type) {
        super(operand);
        this.type = type;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(")(");
        getOperand().decompile(s);
        s.print(")");
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr Cast : start");
//        Type T1 = this.getType(); // NON, on veut récupérer le type qui cast, ex : (int)(1.6) => T1=int
//        LOG.debug("T1 type = " + T1);
//        Type T2 = getOperand().verifyExpr(compiler, localEnv, currentClass);  // OK, on récupère le type à cast, ex : (int)(1.6) => T2=float
//        LOG.debug("T2 type = " + T2);
//        //TODO comment récupérer les types T1 et T2
//        if(T1.sameType(T2)){
//            throw new ContextualError("(cast) useless in Cast : same type", getLocation());
//        }
//        if(T1.isVoid() || T2.isVoid()){
//            throw new ContextualError("a Void cannot be cast", getLocation());
//        }
//        if(T1.isFloat() && T2.isInt() || T2.isFloat() && T1.isInt()){
//            LOG.debug("verifyExpr Cast : end");
//            return T1;
//        }

        //Manière plus simple mais pas définitive (on ne récupère pas le type du cast), T1 dans l'exemple au dessus
        Type type_a_cast = getOperand().verifyExpr(compiler, localEnv, currentClass);  // OK, on récupère le type à cast, ex : (int)(1.6) => T2=float
        if(type_a_cast.isFloat()){ //on le cast en INT
            setType(compiler.environmentType.INT);
            LOG.debug("verifyExpr Cast : end");
            return compiler.environmentType.INT;
        }
        if(type_a_cast.isInt()){ //on le cast en FLOAT
            setType(compiler.environmentType.FLOAT);
            LOG.debug("verifyExpr Cast : end");
            return compiler.environmentType.FLOAT;
        }
        throw new ContextualError("type incompatible in Cast", getLocation());
    }

    @Override
    protected String getOperatorName() {
        return "cast to " + type.getName();
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        getOperand().codeGenInst(compiler);
        if(this.getType().isInt())
            compiler.addInstruction(new INT(Register.R2, Register.R2));
        else if(this.getType().isFloat()) {
            compiler.addInstruction(new FLOAT(Register.R2, Register.R2));
        }
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        getOperand().codeGen(compiler, registerNumber);
        if(this.getType().isInt())
            compiler.addInstruction(new INT(Register.getR(registerNumber), Register.getR(registerNumber)));
        else if(this.getType().isFloat()) {
            compiler.addInstruction(new FLOAT(Register.getR(registerNumber), Register.getR(registerNumber)));
        }
    }

    @Override
    public void addImaInstruction(DecacCompiler compiler, DVal value, GPRegister register) {
    }
}
