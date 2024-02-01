package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.codegen.MethodName;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Label;

import java.util.List;

/**
 * Class declaration.
 *
 * @author gl25
 * @date 01/01/2024
 */
public abstract class AbstractDeclClass extends Tree {

    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler, DeclClass declSuper)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;

    public abstract List<Label> getMethodLabels();
    public abstract List<MethodName> getMethodNames();

    public abstract AbstractIdentifier getName();

    public abstract AbstractIdentifier getSuperclass();

    public abstract ListDeclField getListFields();

    public abstract void addClassMethod(DecacCompiler compiler);
}
