package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl25
 * @date 01/01/2024
 */
public class DeclVar extends AbstractDeclVar {
    private static final Logger LOG = Logger.getLogger(Program.class);
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        LOG.debug("Verify DeclVar : start");
        EnvironmentType env_Type = compiler.environmentType;
        TypeDefinition type_Def = env_Type.defOfType(this.type.getName());
//        LOG.debug(type_Def);
//        type.setDefinition(type_Def);
        Type decl_type = type.verifyType(compiler);
        if(decl_type == null || decl_type.isVoid()){   //ici, on vérifie si le type que on utilise existe et qu'il n'est pas void
            throw new ContextualError("Error of type in DeclVar", getLocation());
        }

        // Stores the type in the identifier
        varName.setType(type_Def.getType());

        initialization.verifyInitialization(compiler, type_Def.getType(), localEnv, currentClass);
        try{
            VariableDefinition varDef = new VariableDefinition(type_Def.getType(), this.getLocation());
            varName.setDefinition(varDef);
            localEnv.declare(this.varName.getName(), varDef);
        }catch(EnvironmentExp.DoubleDefException e){
            throw new ContextualError("Error, type already declared in Declvar", this.getLocation());
        }
        LOG.debug("Verify DeclVar : end");
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public AbstractIdentifier getType() {
        return type;
    }

    @Override
    public AbstractIdentifier getVarName() {
        return varName;
    }

    @Override
    public AbstractInitialization getInitialization() {
        return initialization;
    }
}
