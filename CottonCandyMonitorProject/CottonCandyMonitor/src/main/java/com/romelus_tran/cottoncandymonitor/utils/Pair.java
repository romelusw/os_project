package com.romelus_tran.cottoncandymonitor.utils;

/**
 * An immutable simple tuple (pair) data structure.
 *
 * @author Woody Romelus
 */
public class Pair<L, R> {
    private L left;
    private R right;

    /**
     * Default constructor.
     *
     * @param leftObj the left object
     * @param rightObj the right object
     */
    public Pair(L leftObj, R rightObj) {
        left = leftObj;
        right = rightObj;
    }

    /**
     * Getter for left element from this pair.
     * @return the left element
     */
    public L getLeft () { return left; }

    /**
     * Getter for the right element from this pair.
     * @return the right element
     */
    public R getRight () { return right; }

    @Override
    public boolean equals(Object obj) {
        boolean retVal = false;
        if (obj == this) {
            return true;
        }
        if (obj instanceof Pair) {
            final Pair p = (Pair) obj;
            retVal = p.getLeft().equals(getLeft())
                    && p.getRight().equals(getRight());
        }
        return retVal;
    }

    @Override
    public String toString() {
        return "Pair Left:[" + getLeft() + "] Right: [" + getRight() + "].";
    }
}