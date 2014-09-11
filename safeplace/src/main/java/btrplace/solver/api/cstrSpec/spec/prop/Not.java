/*
 * Copyright (c) 2014 University Nice Sophia Antipolis
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

package btrplace.solver.api.cstrSpec.spec.prop;

import btrplace.solver.api.cstrSpec.verification.spec.SpecModel;

/**
 * @author Fabien Hermenier
 */
public class Not implements Proposition {

    private Proposition p;

    public Not(Proposition p) {
        this.p = p;
    }

    @Override
    public Proposition not() {
        return p;
    }

    @Override
    public Boolean eval(SpecModel m) {
        return !p.eval(m);
    }

    @Override
    public String toString() {
        return "~" + p;
    }

    @Override
    public Proposition simplify(SpecModel m) {
        return new Not(p.simplify(m));
    }
}
