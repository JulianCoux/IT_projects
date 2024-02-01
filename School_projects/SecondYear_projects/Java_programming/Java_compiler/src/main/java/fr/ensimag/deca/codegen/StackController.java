package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;

import java.util.LinkedList;
import java.util.List;

// TODO: Need to take into account the stack of the children, can't be in the compiler or maybe need to be resolved in the end
public class StackController {
    // Stack of the father when the method was called
    private final int superStackAtCall;

    // Highest occupation of the stack at one point, without counting the parents stack
    private int maxStack = 0;

    // Counter of stack of that moment of the code
    private int currentStack = 0;

    public StackController(int superStack) {
        superStackAtCall = superStack;
    }

    public void addToStack(int i) {
        currentStack += i;
        if(maxStack < currentStack)
            maxStack = currentStack;
    }

    public void removeFromStack(int i) {
        currentStack -= i;
    }

    // Return the max occupation of the stack with the parent stack occupation
    public int getMaxStack() {
        return maxStack;
        //return superStackAtCall + (Math.max(maxStack, childrenMax));
    }

    public void append(DecacCompiler compiler) {
        this.removeFromStack(2);
    }

    public int getStack() {
        return currentStack;
    }
}
