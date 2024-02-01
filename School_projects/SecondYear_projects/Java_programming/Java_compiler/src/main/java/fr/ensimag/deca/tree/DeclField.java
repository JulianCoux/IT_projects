package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

import java.io.PrintStream;

public class DeclField extends AbstractDeclField {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    private AbstractVisibility visibility;
    private AbstractIdentifier type;
    private AbstractIdentifier field;
    private AbstractInitialization initialization;

    public DeclField(AbstractVisibility visibility, AbstractIdentifier type,
                     AbstractIdentifier field, AbstractInitialization initialization) {
        this.visibility = visibility;
        this.type = type;
        this.field = field;
        //field.setDefinition(new FieldDefinition());
        this.initialization = initialization;
        setVisibility(getStatusVisibility());
    }

    public DeclField(DeclField otherField) {
        this.visibility = otherField.getVisibility();
        this.type = otherField.getType();
        this.field = new Identifier((Identifier) otherField.getField());
        this.initialization = otherField.getInitialization();
        setLocation(otherField.getLocation());
        setVisibility(getStatusVisibility());
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyClassMembers(DecacCompiler compiler, ClassDefinition superClass, ClassDefinition classe, int index) throws ContextualError {
        LOG.debug("verifyClassMembers DeclField: start");

        Type type_def = type.verifyType(compiler);
        if(type_def.isVoid()){
            throw new ContextualError("type is Void in DeclField", getLocation());
        }
        if(classe.getSuperClass().getMembers() == null){ //Vérifier que env_exp_super(name) est défini  //TODO pas sur que ça soit comme ça ??
            throw new ContextualError("env_exp_super(name) not defined in DeclField", getLocation());
        }
        Visibility visib = getStatusVisibility();

        FieldDefinition fieldDefinition = new FieldDefinition(type_def, getLocation(), visib, classe, index);
        try{
            classe.getMembers().declare(field.getName(), fieldDefinition);
            classe.incNumberOfFields();
        }catch (EnvironmentExp.DoubleDefException e){
            throw new ContextualError("Error, field already declared in DeclField", this.getLocation());
        }

        field.setType(type_def);
        field.setDefinition(fieldDefinition);
        LOG.debug("verifyClassMembers DeclField: end");
    }

    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyClassBody(DecacCompiler compiler, EnvironmentExp env_exp, ClassDefinition classe) throws ContextualError {
        LOG.debug("verifyClassBody DeclField: start");
        initialization.verifyInitialization(compiler, type.getType(), classe.getMembers(), classe);
        //a la place de classe.getMembers() env_exp

        LOG.debug("verifyClassBody DeclField: end");
    }

    public Visibility getStatusVisibility(){
        if(this.visibility.isPublic()){
            return Visibility.PUBLIC;
        }
        else{
            return Visibility.PROTECTED;
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        visibility.decompile(s);
        s.print(" ");
        type.decompile(s);
        s.print(" ");
        field.decompile(s);
        initialization.decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        visibility.prettyPrint(s, prefix, false);
        type.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {

    }

    @Override
    public AbstractVisibility getVisibility() {
        return visibility;
    }

    @Override
    public AbstractIdentifier getType() {
        return type;
    }

    @Override
    public AbstractIdentifier getField() {
        return field;
    }

    @Override
    public AbstractInitialization getInitialization() {
        return initialization;
    }
}
