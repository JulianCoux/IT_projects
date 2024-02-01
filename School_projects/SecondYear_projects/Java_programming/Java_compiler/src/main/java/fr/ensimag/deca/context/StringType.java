package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.ImmediateString;

/**
 *
 * @author Ensimag
 * @date 01/01/2024
 */
public class StringType extends Type {

    public StringType(SymbolTable.Symbol name) {
        super(name);
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public boolean sameType(Type otherType) {
        return otherType.isString();
    }

    @Override
    public DVal getDefaultValue() {
        return new ImmediateInteger(0);
    }
}
