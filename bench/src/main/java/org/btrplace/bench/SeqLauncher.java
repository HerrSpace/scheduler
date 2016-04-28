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

package org.btrplace.bench;

import org.btrplace.scheduler.choco.DefaultParameters;
import org.btrplace.scheduler.choco.Parameters;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Vincent Kherbache
 */
public class SeqLauncher {

    // Define options list
    @Option(name = "-v", usage = "Set the verbosity level")
    private int verbosity = 0; //silent by default

    @Option(name = "-r", aliases = "--repair", usage = "Enable the 'repair' feature")
    private boolean repair;
    @Option(name = "-m", aliases = "--optimize", usage = "Enable the 'optimize' feature")
    private boolean optimize;
    @Option(name = "-t", aliases = "--timeout", usage = "Set a timeout for each bench (in sec)")
    private int timeout = 300; //5min by default
    @Option(required = true, name = "-i", aliases = "--input-list", usage = "the list of benchmarks file name")
    private String inputFile;

    public static void main(String[] args) {
        new SeqLauncher().doMain(args);
    }

    public void doMain(String[] args) {

        // Parse the cmdline arguments
        CmdLineParser cmdParser = new CmdLineParser(this);
        cmdParser.getProperties().withUsageWidth(80);
        try {
            cmdParser.parseArgument(args);
            if (timeout <= 0)
                throw new IllegalArgumentException("Timeout need to be > 0 !");
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("seqBenchLauncher [-r] [-m] [-t n_sec] -i file_name");
            cmdParser.printUsage(System.err);
            System.err.println();
            return;
        }

        Parameters ps = new DefaultParameters();
        ps.doRepair(repair)
                .doOptimize(optimize)
                .setTimeLimit(timeout)
                .setVerbosity(verbosity);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String strLine;

            while ((strLine = br.readLine()) != null) {
                Launcher.launch(ps, strLine, stripExtension(strLine) + ".csv");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String stripExtension(final String s) {
        return s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;
    }
}
