/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
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

package org.btrplace.json.model;

import net.minidev.json.JSONObject;
import org.btrplace.json.JSONConverterException;
import org.btrplace.model.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.btrplace.json.model.AttributesConverter}.
 *
 * @author Fabien Hermenier
 */
public class AttributesConverterTest {

    @Test
    public void testSimple() throws JSONConverterException {
        Model mo = new DefaultModel();
        Attributes attrs = new DefaultAttributes();

        VM vm1 = mo.newVM();
        VM vm3 = mo.newVM(3);

        Node n1 = mo.newNode();

        attrs.put(n1, "boot", 7);
        attrs.put(vm1, "template", "xen");
        attrs.put(vm1, "forge", 3);
        attrs.put(vm3, "template", "kvm");
        attrs.put(vm3, "clone", true);
        attrs.put(vm3, "foo", 1.3);

        JSONObject o = AttributesConverter.toJSON(attrs);
        System.out.println(o);
        Attributes attrs2 = AttributesConverter.fromJSON(mo, o);
        Assert.assertTrue(attrs.equals(attrs2));
    }
}
