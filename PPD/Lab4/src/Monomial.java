public class Monomial {
    Monomial(int coefficient, int exponent) {
        this.coefficient = coefficient;
        this.exponent = exponent;
    }

    public int coefficient;
    public int exponent;
    public Monomial previous = null;
    public Monomial next = null;

    public String toString() {
        return coefficient + " " + exponent;
    }
}
