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

package org.btrplace.safeplace.spec.type;

import org.btrplace.safeplace.spec.term.Constant;

/**
 * @author Fabien Hermenier
 */
public class ColType implements Type {

    protected Type type;

    public ColType(Type t) {
        type = t;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ColType)) {
            return false;
        }
        if (type == null) {
            return true;
        }
        ColType colType = (ColType) o;

        return type.equals(colType.type);

    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String label() {
        StringBuilder b = new StringBuilder("col<");
        if (type == null) {
            b.append('?');
        } else {
            b.append(type.label());
        }
        return b.append('>').toString();
    }

    @Override
    public boolean match(String n) {
        return false;
    }

    @Override
    public Constant newValue(String n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return label();
    }

    @Override
    public Type inside() {
        return type;
    }

    public Type enclosingType() {
        return type;
    }

    @Override
    public boolean comparable(Type t) {
        return t.equals(NoneType.getInstance()) || equals(t);
    }
}