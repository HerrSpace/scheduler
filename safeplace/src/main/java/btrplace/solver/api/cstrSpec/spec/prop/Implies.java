package btrplace.solver.api.cstrSpec.spec.prop;

import btrplace.solver.api.cstrSpec.verification.spec.SpecModel;

/**
 * @author Fabien Hermenier
 */
public class Implies extends BinaryProp {

    private Or o;

    public Implies(Proposition p1, Proposition p2) {
        super(p1, p2);
        o = new Or(p1.not(), p2);
    }

    @Override
    public String operator() {
        return " --> ";
    }

    @Override
    public And not() {
        return o.not();
    }

    /*@Override
    public Or expand() {
        Or or = new Or();
        for (Proposition p : this.props) {
            or.add(p.expand());
        }
        return or;
    } */

    @Override
    public Boolean eval(SpecModel m) {
        return o.eval(m);
    }
}
