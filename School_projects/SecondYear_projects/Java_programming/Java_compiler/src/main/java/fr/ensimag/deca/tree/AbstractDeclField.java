package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

public abstract class AbstractDeclField extends Tree {
    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler, ClassDefinition superClass,
                                               ClassDefinition classe, int index)                                                            //TODO pas sur des paramètres dedans
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler, EnvironmentExp env_exp, ClassDefinition classe)                        //TODO pas sur des paramètres dedans
            throws ContextualError;

    public abstract AbstractVisibility getVisibility();

    public abstract AbstractIdentifier getType();

    public abstract AbstractIdentifier getField();

    public abstract AbstractInitialization getInitialization();

}
