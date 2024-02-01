package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractStringLiteral extends AbstractExpr {

    public abstract String getValue();

    public abstract String getASMValue();
}
