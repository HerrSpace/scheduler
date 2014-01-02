package btrplace.solver.api.cstrSpec.invariant.func;

import btrplace.model.Node;
import btrplace.model.VM;
import btrplace.solver.api.cstrSpec.invariant.Term;
import btrplace.solver.api.cstrSpec.invariant.type.*;

import java.util.List;
import java.util.Set;

/**
 * @author Fabien Hermenier
 */
public abstract class FunctionBuilder {

    public abstract String id();

    public abstract Function build(List<Term> args);

    public Term<VM> asVM(Term t) {
        if (t.type().equals(VMType.getInstance())) {
            return t;
        }
        throw new IllegalArgumentException("Expected '" + VMType.getInstance() + "' got '" + t.type() + "'");
    }

    public Term<Node> asNode(Term t) {
        if (t.type().equals(NodeType.getInstance())) {
            return t;
        }
        throw new IllegalArgumentException("Expected '" + NodeType.getInstance() + "' got '" + t.type() + "'");
    }

    public Term<Integer> asInt(Term t) {
        if (t.type().equals(IntType.getInstance())) {
            return t;
        }
        throw new IllegalArgumentException("Expected '" + IntType.getInstance() + "' got '" + t.type() + "'");
    }

    public Term<String> asString(Term t) {
        if (t.type().equals(StringType.getInstance())) {
            return t;
        }
        throw new IllegalArgumentException("Expected '" + StringType.getInstance() + "' got '" + t.type() + "'");
    }


    public Term<Set> asSet(Term t) {
        if (t.type() instanceof SetType) {
            return t;
        }
        throw new IllegalArgumentException("Expected a set but got '" + t.type() + "'");
    }


    public abstract Type[] signature();
}
