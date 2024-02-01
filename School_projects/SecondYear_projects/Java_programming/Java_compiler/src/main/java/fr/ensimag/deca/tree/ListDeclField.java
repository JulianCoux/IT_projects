package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ListDeclField extends TreeList<AbstractDeclField>{
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition classe) throws ContextualError {
        LOG.debug("verifyListClassMembers ListDeclField: start");
        int index = 0; //((superClass.getType().getName().getName().equals("Object")) ? 0 : superClass.getNumberOfFields());;
        for (AbstractDeclField c : getList()){   //TODO il y le calcul de (env_expr = env_expr + env_exp) à faire
            c.verifyClassMembers(compiler, superClass, classe, index);
            index++;
        }
        LOG.debug("verifyListClassMembers ListDeclField: end");
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler, EnvironmentExp env_exp, ClassDefinition classe) throws ContextualError {
        LOG.debug("verifyListClassBody ListDeclField: start");
        for (AbstractDeclField c: getList())
            c.verifyClassBody(compiler, env_exp, classe);
        LOG.debug("verifyListClassBody ListDeclField: end");
    }

    public void codeGenListField(DecacCompiler compiler) {
        // Get all the registers used
        List<GPRegister> regsUsed =  new LinkedList<>();
        for(AbstractDeclField declVarAbs : this.getList()) {
            if(declVarAbs.getInitialization() instanceof Initialization) {
                regsUsed.addAll(declVarAbs.getInitialization().getExpression().getRegisters(1));

                // Don't know if is faster doing only once with an enormous list
                regsUsed = regsUsed.stream().distinct().collect(Collectors.toList());
            }
        }

        if(!regsUsed.isEmpty()) {
            compiler.addComment("Sauvegarde des registres");
            for (GPRegister reg : regsUsed) {
                compiler.addInstruction(new PUSH(reg));
                compiler.addToStack(1);
            }
        }

        compiler.addComment("Initialisation des registres");
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));

        for(AbstractDeclField declVarAbs : this.getList()) {
            DAddr addr = new RegisterOffset(declVarAbs.getField().getFieldDefinition().getIndex(), Register.R1); // TODO: Maybe the index is a bit short, or maybe there is no index

            if(declVarAbs.getInitialization() instanceof Initialization) {
                declVarAbs.getInitialization().getExpression().codeGenInst(compiler);

                compiler.addInstruction(new STORE(Register.R2, addr));
            } else if(declVarAbs.getInitialization() instanceof NoInitialization) { // To use in the initialization of fields
                compiler.addInstruction(new LOAD(declVarAbs.getField().getType().getDefaultValue(), Register.R0));
                compiler.addInstruction(new STORE(Register.R0, addr));
            }
        }

        if(!regsUsed.isEmpty()) {
            compiler.addComment("Restauration des registres");
            for (int i = regsUsed.size(); i > 1; i--) {
                compiler.addInstruction(new POP(regsUsed.get(i - 1)));
                compiler.removeFromStack(1);
            }
        }
    }
}
