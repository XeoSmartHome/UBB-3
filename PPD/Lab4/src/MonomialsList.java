public class MonomialsList {
    public Monomial first = null;

    MonomialsList() {

    }

    void add(Monomial monomial) {
        if (first == null) {
            first = monomial;
            return;
        }
        Monomial iterator = first;
        while (iterator != null) {
            if (iterator.exponent == monomial.exponent) {
                iterator.coefficient += monomial.coefficient;
                return;
            }
            if(iterator.exponent > monomial.exponent) {
                if(iterator == first) {
                    first = monomial;
                    monomial.next = iterator;
                    iterator.previous = monomial;
                    return;
                }
                iterator.previous.next = monomial;
                monomial.previous = iterator.previous;
                monomial.next = iterator;
                iterator.previous = monomial;
                return;
            }
            iterator = iterator.next;
        }
    }

    void print() {
        Monomial iterator = first;
        while (iterator != null) {
            System.out.print(iterator.coefficient + "x^" + iterator.exponent + " + ");
            iterator = iterator.next;
        }
        System.out.println();
    }
}
