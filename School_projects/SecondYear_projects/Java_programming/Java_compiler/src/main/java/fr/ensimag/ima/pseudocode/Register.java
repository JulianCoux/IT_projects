package fr.ensimag.ima.pseudocode;

/**
 * Register operand (including special registers like SP).
 * 
 * @author Ensimag
 * @date 01/01/2024
 */
public class Register extends DVal {
    private String name;
    protected Register(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Global Base register
     */
    public static final Register GB = new Register("GB");
    /**
     * Local Base register
     */
    public static final Register LB = new Register("LB");
    /**
     * Stack Pointer
     */
    public static final Register SP = new Register("SP");
    /**
     * General Purpose Registers. Array is private because Java arrays cannot be
     * made immutable, use getR(i) to access it.
     */
    private static final GPRegister[] R = initRegisters();
    /**
     * General Purpose Registers (The first 4 are guaranteed)
     */
    public static GPRegister getR(int i) {
        if(i <= maxRegister)
            return R[i];
        else
            return null;
    }
    /**
     * Convenience shortcut for R[0]
     */
    public static final GPRegister R0 = R[0];
    /**
     * Convenience shortcut for R[1]
     */
    public static final GPRegister R1 = R[1];
    /**
     * Convenience shortcut for R[2]
     */
    public static final GPRegister R2 = R[2];
    /**
     * Convenience shortcut for R[3]
     */
    public static final GPRegister R3 = R[3];

    static private GPRegister[] initRegisters() {
        GPRegister [] res = new GPRegister[16];
        for (int i = 0; i <= 15; i++) {
            res[i] = new GPRegister("R" + i, i);
        }
        return res;
    }

    /**
     * Maximum number of register possible to use
     */
    private static int maxRegister = 15;

    public static void setMaxRegister(int X) {
        if(X >= 4 && X <= 16)
            maxRegister = X - 1; // The array starts at zero
    }

    public static int getMaxRegister() {
        return maxRegister;
    }

    /* Max: 7 */
    /* R0 R1 R2 R3 R4 R5 R6 R7 R8 R9 R10 R11 R12 R13 R14 R15 */
    /* 4, 6 */
    /* _, _, _, _ */

    /** Return an array of 2 arrays of Registers, the first array contains the register that need to be saved
     * The second array contain the array available to use after the
     * */
    public static GPRegister[][] getUsableRegisters(int numberOfRegisters, int firstRegister) {
        GPRegister[][] registers = new GPRegister[2][numberOfRegisters];

        // All way the registers after the firstRegister I assumed they are safe to use,
        // I only need to save the ones before it
        int safeMax = Math.min(firstRegister, Register.getMaxRegister() + 1);
        int trueStart = safeMax;

        // Check if there are enough registers, If not, update the trueStart
        if((firstRegister + numberOfRegisters - 1) >= getMaxRegister()) {
            // Discover the number of register that need to be saved
            trueStart -= (safeMax + (numberOfRegisters - 1)) - getMaxRegister();
        }

        for (int i = trueStart; i <= (trueStart + numberOfRegisters - 1); i++) {
            // If the register could have been used, saves in the array of used register
            if(i < safeMax)
                registers[0][i - trueStart] = getR(i);
            registers[1][i - trueStart] = getR(i);
        }

        return registers;
    }
}
