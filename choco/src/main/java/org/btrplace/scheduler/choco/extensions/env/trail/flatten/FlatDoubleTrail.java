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
 * adouble with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btrplace.scheduler.choco.extensions.env.trail.flatten;

import org.btrplace.scheduler.choco.extensions.env.trail.DoubleTrail;

/**
 * @author Fabien Hermenier
 */
public class FlatDoubleTrail implements DoubleTrail {


    /**
     * Stack of backtrackable search variables.
     */
    private org.btrplace.scheduler.choco.extensions.env.StoredDouble[] variableStack;


    /**
     * Stack of values (former values that need be restored upon backtracking).
     */
    private double[] valueStack;


    /**
     * Stack of timestamps indicating the world where the former value
     * had been written.
     */
    private int[] stampStack;


    /**
     * Points the level of the last entry.
     */
    private int currentLevel;


    /**
     * A stack of pointers (for each start of a world).
     */
    private int[] worldStartLevels;

    /**
     * Constructs a trail with predefined size.
     *
     * @param nUpdates maximal number of updates that will be stored
     * @param nWorlds  maximal number of worlds that will be stored
     */

    public FlatDoubleTrail(int nUpdates, int nWorlds) {
        currentLevel = 0;
        variableStack = new org.btrplace.scheduler.choco.extensions.env.StoredDouble[nUpdates];
        valueStack = new double[nUpdates];
        stampStack = new int[nUpdates];
        worldStartLevels = new int[nWorlds];
    }


    /**
     * Moving up to the next world.
     *
     * @param worldIndex current world index
     */

    public void worldPush(int worldIndex) {
        worldStartLevels[worldIndex] = currentLevel;
        if (worldIndex == worldStartLevels.length - 1) {
            resizeWorldCapacity(worldStartLevels.length * 3 / 2);
        }

    }


    /**
     * Moving down to the previous world.
     *
     * @param worldIndex current world index
     */

    public void worldPop(int worldIndex) {
        final int wsl = worldStartLevels[worldIndex];
        while (currentLevel > wsl) {
            currentLevel--;
            final org.btrplace.scheduler.choco.extensions.env.StoredDouble v = variableStack[currentLevel];
            v._set(valueStack[currentLevel], stampStack[currentLevel]);
        }
    }


    /**
     * Returns the current size of the stack.
     */

    public int getSize() {
        return currentLevel;
    }


    /**
     * Comits a world: merging it with the previous one.
     */

    public void worldCommit(int worldIndex) {
        // principle:
        //   currentLevel decreases to end of previous world
        //   updates of the committed world are scanned:
        //     if their stamp is the previous one (merged with the current one) -> remove the update (garbage collecting this position for the next update)
        //     otherwise update the worldStamp
        final int startLevel = worldStartLevels[worldIndex];
        final int prevWorld = worldIndex - 1;
        int writeIdx = startLevel;
        for (int level = startLevel; level < currentLevel; level++) {
            final org.btrplace.scheduler.choco.extensions.env.StoredDouble var = variableStack[level];
            final double val = valueStack[level];
            final int stamp = stampStack[level];
            var.overrideTimeStamp(prevWorld);// update the stamp of the variable (current stamp refers to a world that no doubleer exists)
            if (stamp != prevWorld) {
                // shift the update if needed
                if (writeIdx != level) {
                    valueStack[writeIdx] = val;
                    variableStack[writeIdx] = var;
                    stampStack[writeIdx] = stamp;
                }
                writeIdx++;
            }  //else:writeIdx is not incremented and the update will be discarded (since a good one is in prevWorld)
        }
        currentLevel = writeIdx;
    }


    /**
     * Reacts when a StoredInt is modified: push the former value & timestamp
     * on the stacks.
     */

    public void savePreviousState(org.btrplace.scheduler.choco.extensions.env.StoredDouble v, double oldValue, int oldStamp) {
        valueStack[currentLevel] = oldValue;
        variableStack[currentLevel] = v;
        stampStack[currentLevel] = oldStamp;
        currentLevel++;
        if (currentLevel == variableStack.length) {
            resizeUpdateCapacity();
        }
    }

    @Override
    public void buildFakeHistory(org.btrplace.scheduler.choco.extensions.env.StoredDouble v, double initValue, int olderStamp) {
        // from world 0 to fromStamp (excluded), create a fake history based on initValue
        // kind a copy of the current elements
        // first save the current state on the top of the stack
        savePreviousState(v, initValue, olderStamp - 1);
        // second: ensures capacities
        while (currentLevel + olderStamp > variableStack.length) {
            resizeUpdateCapacity();
        }
        int i1, f, s = currentLevel;
        for (int w = olderStamp; w > 1; w--) {
            f = worldStartLevels[w];
            i1 = f + w - 1;
            s -= f;
            System.arraycopy(variableStack, f, variableStack, i1, s);
            System.arraycopy(valueStack, f, valueStack, i1, s);
            System.arraycopy(stampStack, f, stampStack, i1, s);
            variableStack[i1 - 1] = v;
            valueStack[i1 - 1] = initValue;
            stampStack[i1 - 1] = w - 2;
            worldStartLevels[w] += w - 1;
            currentLevel++;
            s = f;
        }
    }


    private void resizeUpdateCapacity() {
        final int newCapacity = ((variableStack.length * 3) / 2);
        // first, copy the stack of variables
        final org.btrplace.scheduler.choco.extensions.env.StoredDouble[] tmp1 = new org.btrplace.scheduler.choco.extensions.env.StoredDouble[newCapacity];
        System.arraycopy(variableStack, 0, tmp1, 0, variableStack.length);
        variableStack = tmp1;
        // then, copy the stack of former values
        final double[] tmp2 = new double[newCapacity];
        System.arraycopy(valueStack, 0, tmp2, 0, valueStack.length);
        valueStack = tmp2;
        // then, copy the stack of world stamps
        final int[] tmp3 = new int[newCapacity];
        System.arraycopy(stampStack, 0, tmp3, 0, stampStack.length);
        stampStack = tmp3;
    }

    public void resizeWorldCapacity(int newWorldCapacity) {
        final int[] tmp = new int[newWorldCapacity];
        System.arraycopy(worldStartLevels, 0, tmp, 0, worldStartLevels.length);
        worldStartLevels = tmp;
    }

}
