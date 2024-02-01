package fr.ensimag.deca.context;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.deca.tree.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestEnvironmentExp {

    private EnvironmentExp environment;
    private SymbolTable.Symbol x;
    private FieldDefinition def;

    //Fonction qui sera executé avant chaque test
    @BeforeEach
    public void init() {
        // Création d'un environnement pour chaque test
        environment = new EnvironmentExp(null);
        //Création Symbol pour chaque test
        SymbolTable symbolTable = new SymbolTable();
        x = symbolTable.create("x");
        //Création ExpDefinition (FieldDefinition) pour chaque test
        IntType i = new IntType(x);
        Location l = new Location(0, 0, "filename");
        Visibility v = Visibility.PUBLIC;
        ClassDefinition cd = new ClassDefinition(new ClassType(x, l, null), l, null);
        def = new FieldDefinition(i, l, v, cd, 0);
    }


    @Test
    public void testDeclarationGet() {
        // Test de la déclaration et récupération d'une définition
        try {
            environment.declare(x, def);
        } catch (EnvironmentExp.DoubleDefException err){
            err.printStackTrace();
        }
        ExpDefinition defGet = environment.get(x);
        assertEquals(def, defGet);
    }

    @Test
    public void testDoubleDeclaration() {
        //Déclaration d'une autre definition
        FieldDefinition def2 = def;

        // Test de la double déclaration
        assertThrows(EnvironmentExp.DoubleDefException.class, () -> {
            environment.declare(x, def);
            // Cela doit lancer une DoubleDefException
            environment.declare(x, def2);

        });
    }

    @Test
    public void testParent(){
        //On delcalre l'environment
        try {
            environment.declare(x, def);
        } catch (EnvironmentExp.DoubleDefException err){
            err.printStackTrace();
        }

        // Créer un environment fils
        EnvironmentExp filsEnvironment = new EnvironmentExp(environment);

        // Test de récupération de la définition depuis le parent environment
        ExpDefinition defGet = filsEnvironment.get(x);
        assertEquals(def, defGet);
    }

    public static void main(String[] args) {
        // Vous pouvez exécuter vos tests ici si nécessaire
    }
}

