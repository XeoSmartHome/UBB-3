import java.util.ArrayList;
import java.util.List;

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
        while (true) {
            // If the exponent is the same, add the coefficients
            if (iterator.exponent == monomial.exponent) {
                iterator.coefficient += monomial.coefficient;
                // If the coefficient is 0, remove the monomial
                if(iterator.coefficient == 0) {
                    if(iterator.previous != null) {
                        iterator.previous.next = iterator.next;
                    }
                    if(iterator.next != null) {
                        iterator.next.previous = iterator.previous;
                    }
                }
                return;
            }
            // If the exponent is smaller, add the monomial before the iterator
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
            // If the iterator is the last monomial, add the monomial after the iterator
            if(iterator.next == null) {
                iterator.next = monomial;
                monomial.previous = iterator;
                return;
            }
            iterator = iterator.next;
        }
    }

    int getLength() {
        int length = 0;
        Monomial iterator = first;
        while (iterator != null) {
            length++;
            iterator = iterator.next;
        }
        return length;
    }
    public String toString() {
        StringBuilder string = new StringBuilder();
        Monomial iterator = first;
        while (iterator != null) {
            string.append(iterator.coefficient).append("x^").append(iterator.exponent).append(" + ");
            iterator = iterator.next;
        }
        return string.toString();
    }

    public List<Monomial> getAsList() {
        List<Monomial> list = new ArrayList<>();
        Monomial iterator = first;
        while (iterator != null) {
            list.add(iterator);
            iterator = iterator.next;
        }
        return list;
    }
}
