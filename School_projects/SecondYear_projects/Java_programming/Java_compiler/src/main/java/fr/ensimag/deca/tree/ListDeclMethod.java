package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

public class ListDeclMethod extends TreeList<AbstractDeclMethod>{
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition classe) throws ContextualError {
        LOG.debug("verifyListClassMembers ListDeclMethod: start");

        int index = 2;
        for (AbstractDeclMethod c : getList()){
            c.verifyClassMembers(compiler, superClass, classe, index);
            classe.setNumberOfMethods(classe.getNumberOfMethods() + 1);
            index++;
        }
        LOG.debug("verifyListClassMembers ListDeclMethod: end");
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler, EnvironmentExp env_exp, ClassDefinition classe) throws ContextualError {
        LOG.debug("verifyListClassBody ListDeclMethod: start");
        for (AbstractDeclMethod c: getList())
            c.verifyClassBody(compiler, env_exp, classe);
        LOG.debug("verifyListClassBody ListDeclMethod: end");
    }
}
