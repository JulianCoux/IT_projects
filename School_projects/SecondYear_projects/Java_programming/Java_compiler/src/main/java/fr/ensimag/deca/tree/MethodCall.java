package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.io.PrintStream;
import java.util.List;

public class MethodCall extends AbstractExpr {
    private AbstractExpr obj;
    private AbstractIdentifier meth;
    private ListExpr param;

    public MethodCall(AbstractExpr obj, AbstractIdentifier meth, ListExpr param) {
        if(obj == null)
            this.obj = new This(true);
        else
            this.obj = obj;

        this.meth = meth;
        this.param = param;
    }
    @Override
    public void decompile(IndentPrintStream s) {
        if (obj != null) {
            obj.decompile(s);
            s.print(".");
        }
        meth.decompile(s);
        s.print("(");
        param.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        obj.prettyPrint(s, prefix, false);
        meth.prettyPrint(s, prefix, false);
        param.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        param.iter(f);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        // Vérifie que l'objet sur lequel est appelée la méthode est une classe
        Type typeObj = this.obj.verifyExpr(compiler, localEnv, currentClass);
        if (!typeObj.isClass()) {
            throw new ContextualError("Object must be a class in MethodCall", getLocation());
        }

        // Vérifie que la classe possède la méthode
        ClassType classType = (ClassType) typeObj;
        SymbolTable.Symbol methodName = this.meth.getName();
        ExpDefinition methodDef = classType.getDefinition().getMembers().get(methodName);
        meth.setDefinition(methodDef);
        if (!(methodDef instanceof MethodDefinition)){
            throw new ContextualError("The method '" + methodName.toString() + "' doesn't exist in MethodCall", getLocation());
        }
        MethodDefinition methodDefOk = (MethodDefinition) methodDef;

        //Vérifie les paramètres de la méthode
        Signature signature = methodDefOk.getSignature();
        signature.verifyParameters(compiler, this.param.getModifiableList(), getLocation(), localEnv, currentClass);

        //SetType
        Type returnType = signature.getReturnType();
        setType(returnType);
        return returnType;
    }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber){
        compiler.addComment("Appel de methode " + obj.getType() + "." + meth + " ligne " + getLocation());
        GPRegister register = Register.R2;
        if(registerNumber > 2) {
            compiler.addInstruction(new PUSH(Register.R2));
            compiler.addToStack(1);
        }

        // Add to the pile register the number of parameters + 1
        int frameSize = param.size() + 1;
        compiler.addInstruction(new ADDSP(frameSize));
        compiler.addToStack(frameSize);

        // Stores the method table in the pile
        if(obj instanceof Identifier) { // Add selection later if possible
            DAddr objAddr = ((Identifier) obj).getExpDefinition().getOperand();
            compiler.addInstruction(new LOAD(objAddr, Register.R2));
            compiler.addInstruction(new STORE(Register.R2, new RegisterOffset(0, Register.SP)));
        } else if(obj instanceof This || obj instanceof Selection) {
            obj.codeGen(compiler, 2);
            compiler.addInstruction(new STORE(Register.R2, new RegisterOffset(0, Register.SP)));
        }

        // Store the parameters
        for (int i = param.size(); i > 0; i--) {
            param.getList().get(i - 1).codeGen(compiler, 2);

            compiler.addInstruction(new STORE(Register.R2, new RegisterOffset(-i, Register.SP)));
        }
        
        // Loads the address of the method table
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.SP), Register.R2));
        compiler.addInstruction(new CMP(new NullOperand(), Register.R2));
        if(!compiler.getCompilerOptions().getNoCheck())
            compiler.addInstruction(new BEQ(new Label("dereferencement_null")));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.R2), Register.R2));

        // BSR to the table
        compiler.addInstruction(new BSR(new RegisterOffset(meth.getMethodDefinition().getIndex(), Register.R2)));
        compiler.addToStack(2);
        compiler.removeFromStack(2);

        compiler.addInstruction(new SUBSP(frameSize));
        compiler.removeFromStack(frameSize);

        if(registerNumber > 2) {
            compiler.addInstruction(new POP(Register.R2));
            compiler.removeFromStack(1);
        }

        if(!getType().isVoid())
            compiler.addInstruction(new LOAD(Register.R0, Register.getR(registerNumber)));

    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        codeGen(compiler, 2);
    }
}
