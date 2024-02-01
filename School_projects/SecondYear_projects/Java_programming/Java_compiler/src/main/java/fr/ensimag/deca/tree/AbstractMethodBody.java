package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.ima.pseudocode.GPRegister;

import java.util.List;

public abstract class AbstractMethodBody extends Tree {
    private DeclMethod declMethod;

    public abstract List<GPRegister> getRegisters();
    public abstract void codeGenMethod(DecacCompiler compiler);
    public abstract void verifyClassBody(DecacCompiler compiler, EnvironmentExp env_exp, EnvironmentExp env_exp_params, ClassDefinition classe, AbstractIdentifier type) throws ContextualError;

    public DeclMethod getDeclMethod() {
        return declMethod;
    }

    public void setDeclMethod(DeclMethod declMethod) {
        this.declMethod = declMethod;
    }
}
