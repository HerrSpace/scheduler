/*
 * Copyright (c) 2017 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btrplace.btrpsl.constraint;

import org.btrplace.btrpsl.element.BtrpNumber;
import org.btrplace.btrpsl.element.BtrpOperand;
import org.btrplace.btrpsl.tree.BtrPlaceTree;
import org.btrplace.model.Node;
import org.btrplace.model.constraint.MaxOnline;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A builder for {@link org.btrplace.model.constraint.MaxOnline} constraints.
 *
 * @author Fabien Hermenier
 */
public class MaxOnlineBuilder extends DefaultSatConstraintBuilder {

    /**
     * Make a new builder.
     */
    public MaxOnlineBuilder() {
        super("maxOnline", new ConstraintParam[]{new ListOfParam("$n", 1, BtrpOperand.Type.NODE, false), new NumberParam("$nb")});
    }

    /**
     * Build an online constraint.
     *
     * @param args must be 1 set of nodes. The set must not be empty
     * @return a constraint
     */
    @Override
    public List<MaxOnline> buildConstraint(BtrPlaceTree t, List<BtrpOperand> args) {
        if (checkConformance(t, args)) {
          @SuppressWarnings("unchecked")
          List<Node> ns = (List<Node>) params[0].transform(this, t, args.get(0));
            if (ns == null) {
                return Collections.emptyList();
            }
            Set<Node> s = new HashSet<>(ns);
            if (s.size() != ns.size()) {                //Prevent duplicates
                return Collections.emptyList();
            }

            BtrpNumber n = (BtrpNumber) args.get(1);
            if (!n.isInteger()) {
                t.ignoreError("Parameter '" + params[1].getName() + "' expects an integer");
                return Collections.emptyList();
            }
            int v = n.getIntValue();
            if (v < 0) {
                t.ignoreError("Parameter '" + params[1].getName() + "' expects a positive integer (" + v + " given)");
                return Collections.emptyList();
            }

            return Collections.singletonList(new MaxOnline(new HashSet<>(ns), v, true));
        }
        return Collections.emptyList();
    }
}
