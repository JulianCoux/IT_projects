package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;

public interface BooleanValue {
    public abstract void codeGenNot(DecacCompiler compiler, int registerNumber);
}
