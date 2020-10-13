package net.displayphoenix.ui;

/**
 * @author TBroski
 */
public class Constraints {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;

    private final int[] contraints;

    public Constraints(int... constraints) {
        this.contraints = constraints;
    }

    public int[] getContraints() {
        return contraints;
    }
}
