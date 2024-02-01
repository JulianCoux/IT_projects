package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.AbstractExpr;
import fr.ensimag.deca.tree.ConvFloat;
import fr.ensimag.deca.tree.ListDeclClass;
import fr.ensimag.deca.tree.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Signature of a method (i.e. list of arguments)
 *
 * @author gl25
 * @date 01/01/2024
 */
public class Signature {
    List<Type> args = new ArrayList<Type>();
    private Type returnType;

    public void add(Type t) {
        args.add(t);
    }
    
    public Type paramNumber(int n) {
        return args.get(n);
    }
    
    public int size() {
        return args.size();
    }

    public void verifyParameters(DecacCompiler compiler, List<AbstractExpr> parameters, Location location, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError{
        //Vérifie qu'on a le même nombre d'arg
        if (parameters.size() != args.size()){
            throw new ContextualError("Wrong number of arguments in Signature", location);
        }
        for (int i = 0; i < args.size(); i++){
            Type exceptType = args.get(i);
            Type actuType = parameters.get(i).verifyExpr(compiler, localEnv, currentClass);
            if(exceptType.isFloat() && actuType.isInt()) {
                ConvFloat cast = new ConvFloat(parameters.get(i));
                cast.verifyExpr(compiler, localEnv, currentClass);
                parameters.set(i, cast);
            }
            else if (!actuType.sameType(exceptType)){
                throw new ContextualError("Incompatible Type in Signature", location);
            }
        }
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }
}
