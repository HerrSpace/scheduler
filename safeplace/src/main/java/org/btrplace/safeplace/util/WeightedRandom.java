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

package org.btrplace.safeplace.util;

import java.util.Random;

/**
 * @author Fabien Hermenier
 */
public class WeightedRandom {

    private int[] values;

    private int sum;
    private Random rnd;

    public WeightedRandom(int... weights) {

        rnd = new Random();

        sum = 0;
        for (int v : weights) {
            sum += v;
        }
        values = new int[sum - 1];
        int i = 0;
        for (int max : weights) {
            for (; i < values.length && i < max; i++) {
                values[i] = max;
            }
        }
    }

    public int nextInt() {
        return values[rnd.nextInt(values.length)];
    }
}
