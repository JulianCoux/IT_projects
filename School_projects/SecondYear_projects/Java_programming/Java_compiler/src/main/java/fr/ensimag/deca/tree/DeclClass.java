package fr.ensimag.deca.tree;

import fr.ensimag.deca.codegen.MethodName;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.TSTO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import fr.ensimag.ima.pseudocode.Label;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl25
 * @date 01/01/2024
 */
public class DeclClass extends AbstractDeclClass {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    private AbstractIdentifier name;
    private AbstractIdentifier superclass;
    private ListDeclField listField;
    private ListDeclMethod listMethod;
    private List<MethodName> methodNames;

    public DeclClass(AbstractIdentifier name, AbstractIdentifier superclass, ListDeclField listField,
                     ListDeclMethod listMethod) {
        this.name = name;
        this.superclass = superclass;
        this.listField = listField;
        this.listMethod = listMethod;

        this.methodNames = new LinkedList<MethodName>();
        for(AbstractDeclMethod absDeclMethod : listMethod.getList()) {
            DeclMethod declMethod = (DeclMethod) absDeclMethod;

            methodNames.add(new MethodName(name.toString(), declMethod.getName().toString()));
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class ");
        name.decompile(s);
        s.print(" extends ");
        superclass.decompile(s);
        s.print(" {");
        listField.decompile(s);
        listMethod.decompile(s);
    }

    private static EnvironmentExp env_exp = new EnvironmentExp(null);

    @Override
    protected void verifyClass(DecacCompiler compiler, DeclClass declSuper) throws ContextualError {
        //verifier le nom des classes et la hiérarchie de classes
        LOG.debug("verifyClass DeclClass: start");
        ClassDefinition superClassDefinition = (ClassDefinition) compiler.environmentType.defOfType(superclass.getName());

        EnvironmentType env_types = compiler.environmentType;

        ClassType classType = new ClassType(name.getName(), getLocation(), superClassDefinition);
        try{
            name.setDefinition(classType.getDefinition());
            name.setType(classType.getDefinition().getType());
            env_types.declareClass(name.getName(), classType.getDefinition());
        }catch (EnvironmentType.DoubleDefException e){
            throw new ContextualError("Error, Class already declared in DeclClass", this.getLocation());
        }

        if(compiler.environmentType.get(superclass.getName()) == null){
            throw new ContextualError("superClass is not a valid Class in DeclClass", getLocation());
        }
        superclass.setDefinition(superClassDefinition);
        superclass.setType(compiler.environmentType.defOfType(superclass.getName()).getType());

        if(declSuper != null) {
            ListDeclField listFieldsSuper = declSuper.getListFields();
            ListDeclMethod listMethodSuper = declSuper.listMethod;
            name.getClassDefinition().setNumberOfFields(superClassDefinition.getNumberOfFields());

            for (int i = listFieldsSuper.getList().size() - 1; i >= 0; i--) {
                DeclField fieldSuper = new DeclField((DeclField) listFieldsSuper.getList().get(i));
                ((LinkedList) listField.getModifiableList()).addFirst(fieldSuper);
            }

            for (int i = listMethodSuper.getList().size() - 1; i >= 0; i--) {
                DeclMethod methodSuper = (DeclMethod) listMethodSuper.getList().get(i);
                ((LinkedList) listMethod.getModifiableList()).addFirst(methodSuper);
            }
        }
        LOG.debug("verifyClass DeclClass: end");
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        LOG.debug("verifyClassMembers DeclClass: start");
        EnvironmentType env_types = compiler.environmentType;   //on récupère l'environnement des types

        listField.verifyListClassMembers(compiler, superclass.getClassDefinition(), name.getClassDefinition());
        listMethod.verifyListClassMembers(compiler, superclass.getClassDefinition(), name.getClassDefinition());


        LOG.debug("verifyClassMembers DeclClass: end");
    }

    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verifyClassBody DeclClass: start");
        //TODO faire la vérification de env_exp
        listField.verifyListClassBody(compiler, env_exp, name.getClassDefinition());
        listMethod.verifyListClassBody(compiler, env_exp, name.getClassDefinition());

        LOG.debug("verifyClassBody DeclClass: end");
    }



    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        name.prettyPrint(s, prefix, false);
        superclass.prettyPrint(s, prefix, false);
        listField.prettyPrint(s, prefix, false);
        listMethod.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        listField.iter(f);
        listMethod.iter(f);
    }

    @Override
    public List<Label> getMethodLabels() {
        List<Label> list = new LinkedList<Label>();
        for(MethodName methodName : methodNames) {
            list.add(methodName.toLabel());
        }

        return list;
    }

    @Override
    public List<MethodName> getMethodNames() {
        return methodNames;
    }

    @Override
    public AbstractIdentifier getName() {
        return name;
    }

    @Override
    public AbstractIdentifier getSuperclass() {
        return superclass;
    }

    @Override
    public ListDeclField getListFields() {
        return listField;
    }

    @Override
    public void addClassMethod(DecacCompiler compiler) {
        compiler.addComment("--------------------------------------------------");
        String strClassName = "Class " + getName();
        compiler.addComment(StringUtils.center(strClassName, 50 - strClassName.length()));
        compiler.addComment("--------------------------------------------------");

        addInit(compiler);
        addMethods(compiler);
    }

    private void addInit(DecacCompiler compiler) {
        DecacCompiler blockCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        blockCompiler.initStackController(compiler.getStack());

        compiler.addComment("---------- Initialisation des champs de " + getName());
        compiler.addLabel(new Label("init." + getName()));

        if(!getSuperclass().toString().equals("Object")) {
            blockCompiler.addInstruction(new BSR(new Label("init." + getSuperclass())));
            blockCompiler.addToStack(2);
            compiler.removeFromStack(2);
        }

        listField.codeGenListField(blockCompiler);

        blockCompiler.addInstruction(new RTS());


        if(blockCompiler.getMaxStack() != 0 || !compiler.getCompilerOptions().getNoCheck()) {
            blockCompiler.addFirst(new BOV(new Label("pile_pleine")));
            blockCompiler.addFirst(new TSTO(blockCompiler.getMaxStack()));
        }

        compiler.append(blockCompiler);
    }

    private void addMethods(DecacCompiler compiler) {
        for(AbstractDeclMethod declMethod : listMethod.getList()) {
            declMethod.codeGenMethod(compiler, getName().toString());
        }
    }
}
