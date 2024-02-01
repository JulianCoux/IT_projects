package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List of declarations (e.g. int x; float y,z).
 *
 * @author gl25
 * @date 01/01/2024
 */
public class ListDeclVar extends TreeList<AbstractDeclVar> {
    private static final Logger LOG = Logger.getLogger(Program.class);

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclVar i : this.getList())
            i.decompile(s);
    }

    /**
     * Implements non-terminal "list_decl_var" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param localEnv
     *   its "parentEnvironment" corresponds to "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to
     *      the "env_exp_r" attribute
     * @param currentClass
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclVariable(DecacCompiler compiler, EnvironmentExp localEnv,
                                ClassDefinition currentClass) throws ContextualError {
        LOG.debug("verifyListDeclVariable ListDeclVar : start");
        for(AbstractDeclVar d: getList()) {
            d.verifyDeclVar(compiler, localEnv, currentClass);
        }
        LOG.debug("verifyListDeclVariable ListDeclVar : end");

    }

    public void codeGenListVariables(DecacCompiler compiler) {
        for(AbstractDeclVar declVar : this.getList()) {
            AbstractInitialization initVar;
            Identifier identVar;
            initVar = declVar.getInitialization();

            try {
                identVar = (Identifier) declVar.getVarName();
            } catch (ClassCastException e) {
                throw new DecacInternalError("AbstractIdentifier is not a Identifier");
            }

            // Initialize the address of the variable
            RegisterOffset addr = new RegisterOffset(Program.getIndexGB(), Register.GB);

            // Don't know if it should be a test or a try and catch
            if(identVar.getDefinition().isExpression()){
                ExpDefinition identDefinition = identVar.getExpDefinition();

                identDefinition.setOperand(addr);

                if(initVar instanceof Initialization) {
                    ((Initialization) initVar).getExpression().codeGen(compiler, 2);

                    compiler.addInstruction(new STORE(Register.R2, addr));
                }
            }

            Program.incrementIndexGB();
        }
    }

    public List<GPRegister> getRegisters() {
        List<GPRegister> regsUsed = new LinkedList<>();

        for(AbstractDeclVar declVar : getList()) {
            if (declVar.getInitialization().getExpression() != null) {
                regsUsed.addAll(declVar.getInitialization().getExpression().getRegisters(2));

                // Don't know if is faster doing only once with an enormous list
                regsUsed = regsUsed.stream().distinct().collect(Collectors.toList());
            }
        }

        return regsUsed;
    }
}
