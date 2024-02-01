package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author gl25
 * @date 01/01/2024
 */
public class ListInst extends TreeList<AbstractInst> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
                               ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        LOG.debug("verifyListInst ListInst : start");
        for(AbstractInst i: getList())
            i.verifyInst(compiler, localEnv, currentClass, returnType);
        LOG.debug("verifyListInst ListInst : end");
    }

    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            if(i instanceof IfThenElse) {
                ((IfThenElse) i).setReturnLabel(getReturnLabel());
            } else if(i instanceof Return) {
                ((Return) i).setEndMethod(getReturnLabel());
            } else if(i instanceof While) {
                ((While) i).setReturnLabel(getReturnLabel());
            }

            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractInst i : getList()) {
            i.decompileInst(s);
            s.println();
        }
    }

    private Label returnLabel;

    public Label getReturnLabel() {
        return returnLabel;
    }

    public void setReturnLabel(Label returnLabel) {
        this.returnLabel = returnLabel;
    }

    public List<GPRegister> getRegisters() {
        List<GPRegister> regsUsed = new LinkedList<>();

        for(AbstractInst inst : getList()) {
            regsUsed.addAll(inst.getRegisters(2));

            // Don't know if is faster doing only once with an enormous list
            regsUsed = regsUsed.stream().distinct().collect(Collectors.toList());
        }

        return regsUsed;
    }
}
