package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.ima.pseudocode.DAddr;

/**
 * Left-hand side value of an assignment.
 * 
 * @author gl25
 * @date 01/01/2024
 */

public abstract class AbstractLValue extends AbstractExpr {
    /* Had to take these information into AbstractLValue, I had put Selection in AbstractIdentifier but that was
    * getting to complicated, so I took only the necessary to AbstractLValue */

    private DAddr address;
    private Definition definition;

    public DAddr getAddress() {
        return address;
    }

    public void setAddress(DAddr address) {
        this.address = address;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public Definition getDefinition() {
        return definition;
    }

    public abstract void codeGenLValue(DecacCompiler compiler, int registerNumber);
}
