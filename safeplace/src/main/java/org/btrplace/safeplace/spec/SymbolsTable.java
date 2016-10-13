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

package org.btrplace.safeplace.spec;

import org.btrplace.safeplace.spec.term.*;
import org.btrplace.safeplace.spec.term.func.*;
import org.btrplace.safeplace.spec.type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Fabien Hermenier
 */
public class SymbolsTable {

    private Map<String, Var> table;

    private Map<String, Function> funcs;

    private SymbolsTable parent;

    private List<Primitive> primitives;

    private Map<String, Constraint> cstrs;

    public SymbolsTable() {
        this(null);
    }

    public SymbolsTable(SymbolsTable p) {
        table = new HashMap<>();
        this.funcs = new HashMap<>();
        parent = p;
        primitives = new ArrayList<>();
        cstrs = new HashMap<>();
    }


    public SymbolsTable enterSpec() {
        SymbolsTable syms = new SymbolsTable(this);
        //Copy the primitives

        syms.put(new InDomain<>("nodes", NodeType.getInstance()));
        syms.put(new InDomain<>("vms", VMType.getInstance()));
        syms.put(new InDomain<>("vmState", VMStateType.getInstance()));
        syms.put(new InDomain<>("nodeState", NodeStateType.getInstance()));
        syms.put(new InDomain<>("int", IntType.getInstance()));
        syms.put(new InDomain<>("time", TimeType.getInstance()));
        syms.put(new InDomain<>("action", ActionType.getInstance()));
        syms.put(new InDomain<>("bool", BoolType.getInstance()));
        syms.put(new InDomain<>("float", RealType.getInstance()));
        syms.put(new InDomain<>("string", StringType.getInstance()));
        syms.put(None.instance());
        return syms;
    }

    public SymbolsTable enterScope() {
        return new SymbolsTable(this);
    }

    public SymbolsTable leaveScope() {
        return parent;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (parent != null) {
            b.append(parent.toString());
            b.append("--------------\n");
        }
        for (Map.Entry<String, Var> e : table.entrySet()) {
            b.append("var \t").append(e.getKey()).append("\t").append(e.getValue().type()).append("\n");
        }
        for (Map.Entry<String, Function> e : funcs.entrySet()) {
            b.append("func\t").append(e.getValue()).append("\t").append(e.getValue().type()).append("\n");
        }
        return b.toString();
    }

    public boolean put(Function f) {
        if (funcs.containsKey(f.id())) {
            return false;
        }
        funcs.put(f.id(), f);
        return true;
    }

    public boolean put(Constraint c) {
        if (cstrs.containsKey(c.id())) {
            return false;
        }
        cstrs.put(c.id(), c);
        return true;
    }

    public Function getFunction(String id) {
        if (funcs.containsKey(id)) {
            return funcs.get(id);
        }
        if (parent != null) {
            return parent.getFunction(id);
        }
        return null;
    }

    public Constraint getConstraint(String id) {
        if (cstrs.containsKey(id)) {
            return cstrs.get(id);
        }
        if (parent != null) {
            return parent.getConstraint(id);
        }
        return null;
    }

    private boolean putVar(Var v) {
        if (table.containsKey(v.label())) {
            return false;
        }
        table.put(v.label(), v);
        return true;
    }

    public Var getVar(String n) {
        if (table.containsKey(n)) {
            return table.get(n);
        }
        if (parent != null) {
            return parent.getVar(n);
        }
        return null;
    }

    public void put(Primitive p) {
        if (putVar(p)) {
            primitives.add(p);
        }
    }

    public boolean put(UserVar v) {
        return putVar(v);
    }
}