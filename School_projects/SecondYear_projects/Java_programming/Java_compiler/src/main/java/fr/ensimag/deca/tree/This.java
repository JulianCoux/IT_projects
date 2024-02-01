package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.log4j.Logger;

import java.io.PrintStream;

public class This extends AbstractExpr {
    private static final Logger LOG = Logger.getLogger(Identifier.class);

    private boolean impl;

    public This(boolean impl) {
        this.impl = impl;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyExpr This : start");
        if (currentClass == null){
            throw new ContextualError("Class is null in This", getLocation());
        }
        //set<Type
        this.setType(currentClass.getType());
        LOG.debug("verifyExpr This : end");

        return getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        if(!impl)
            s.print("this");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) { }

    @Override
    protected void iterChildren(TreeFunction f) { }

    @Override
    protected void codeGen(DecacCompiler compiler, int registerNumber) {
        RegisterOffset implicitThis = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(implicitThis, Register.getR(registerNumber)));
    }
}
