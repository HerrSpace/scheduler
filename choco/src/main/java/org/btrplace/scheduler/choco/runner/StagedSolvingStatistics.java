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

package org.btrplace.scheduler.choco.runner;

import org.btrplace.model.Instance;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.scheduler.choco.Parameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate the statistics of a multi-stage resolution.
 *
 * @author Fabien Hermenier
 */
public class StagedSolvingStatistics implements SolvingStatistics {

    private List<SolvingStatistics> stages;

    /**
     * Make a new statistic.
     */
    public StagedSolvingStatistics(SolvingStatistics firstStage) {
        stages = new ArrayList<>();
        stages.add(firstStage);
    }


    /**
     * Append statistics.
     *
     * @param stats the statistics to add
     * @return {@code this}
     */
    public StagedSolvingStatistics append(SolvingStatistics stats) {
        stages.add(stats);
        return this;
    }

    private SolvingStatistics last() {
        return stages.get(stages.size() - 1);
    }

    private SolvingStatistics first() {
        return stages.get(0);
    }

    /**
     * Get the number of stages.
     * @return a positive integer
     */
    public int getNbStages() {
        return stages.size();
    }

    /**
     * Get the statistics associated to a given stage
     *
     * @param st the stage
     * @return the resulting statistics if any. {@code null} otherwise
     */
    public SolvingStatistics getStage(int st) {
        return stages.get(st);
    }

    /**
     * Return the aggregated core problem build duration.
     *
     * @return a long
     */
    @Override
    public long getCoreBuildDuration() {
        return stages.stream().mapToLong(SolvingStatistics::getCoreBuildDuration).sum();
    }

    /**
     * Return the aggregated specialisation duration.
     *
     * @return a long
     */
    @Override
    public long getSpecializationDuration() {
        return stages.stream().mapToLong(SolvingStatistics::getSpecializationDuration).sum();
    }

    /**
     * Return the timestamp of the first phase.
     *
     * @return a timestamp. -1 if not started yet
     */
    @Override
    public long getStart() {
        return first().getStart();
    }

    /**
     * Return the statistics of the last solution
     *
     * @return a list that might be empty
     */
    @Override
    public List<SolutionStatistics> getSolutions() {
        return last().getSolutions();
    }

    @Override
    public int getNbManagedVMs() {
        return last().getNbManagedVMs();
    }

    @Override
    public Parameters getParameters() {
        return first().getParameters();
    }

    /**
     * Print each stage statistics
     *
     * @return a formatted string
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        int i = 1;
        for (SolvingStatistics st : stages) {
            b.append("---- Stage ").append(i).append("/").append(stages.size()).append(" ----\n");
            b.append(st.toString()).append("\n");
            i++;
        }
        return b.toString();
    }

    @Override
    public Metrics getMetrics() {
        Metrics m = first().getMetrics().copy();
        for (int i = 1; i < stages.size(); i++) {
            m.add(stages.get(i).getMetrics());
        }
        return m;

    }

    @Override
    public Instance getInstance() {
        return first().getInstance();
    }

    @Override
    public boolean completed() {
        return last().completed();
    }

    @Override
    public ReconfigurationPlan lastSolution() {
        return last().lastSolution();
    }

    /**
     * Print the statistics as a CSV line.
     * Statistics are computed wrt. the different stages:
     * - the maximum number of managed VMs
     * - the cumulative getCoreBuildDuration()
     * - the cumulative getSpecializationDuration()
     * - the cumulative getMetrics().timeCount
     * - the number of solutions for the last stage or 0 if any of the stages does not have at least a solution
     * - completed ? 1 if all the stages are completed
     *
     * @return a CSV formatted string
     */
    @Override
    public String toCSV() {
        int nbManagedVMs = -1;
        long core = 0;
        long spe = 0;
        long d = 0;
        int solutions = 0;
        if (!stages.isEmpty()) {
            solutions = stages.get(stages.size() - 1).getSolutions().size();
        }
        boolean completed = true;
        for (SolvingStatistics sol : stages) {
            nbManagedVMs = Math.max(nbManagedVMs, sol.getNbManagedVMs());
            core += sol.getCoreBuildDuration();
            spe += sol.getSpecializationDuration();
            d += sol.getMetrics().timeCount();
            completed &= sol.completed();
            if (sol.getSolutions().isEmpty()) {
                solutions = 0;
            }
        }

        return String.format("%d;%d;%d;%d;%d;%d", nbManagedVMs,
                core,
                spe,
                d,
                solutions,
                completed ? 1 : 0);

    }
}
