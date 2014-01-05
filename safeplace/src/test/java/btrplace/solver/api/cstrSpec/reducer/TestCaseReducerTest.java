package btrplace.solver.api.cstrSpec.reducer;

import btrplace.model.*;
import btrplace.model.constraint.Offline;
import btrplace.plan.DefaultReconfigurationPlan;
import btrplace.plan.ReconfigurationPlan;
import btrplace.plan.event.BootNode;
import btrplace.plan.event.BootVM;
import btrplace.plan.event.MigrateVM;
import btrplace.plan.event.ShutdownNode;
import btrplace.solver.api.cstrSpec.Constraint;
import btrplace.solver.api.cstrSpec.spec.SpecReader;
import btrplace.solver.api.cstrSpec.verification.ImplVerifier;
import btrplace.solver.api.cstrSpec.verification.TestCase;
import btrplace.solver.api.cstrSpec.verification.TestResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class TestCaseReducerTest {

    @Test
    public void test() throws Exception {
        Model mo = new DefaultModel();
        Node n1 = mo.newNode();
        Node n2 = mo.newNode();
        Node n3 = mo.newNode();

        VM vm1 = mo.newVM();
        VM vm2 = mo.newVM();
        VM vm3 = mo.newVM();

        Mapping m = mo.getMapping();
        m.addOnlineNode(n1);
        m.addOnlineNode(n2);
        m.addOfflineNode(n3);
        m.addRunningVM(vm1, n1);
        m.addReadyVM(vm2);
        m.addSleepingVM(vm3, n2);

        ReconfigurationPlan p = new DefaultReconfigurationPlan(mo);
        p.add(new ShutdownNode(n2, 0, 3));
        //p.add(new MigrateVM(vm1, n1, n2, 2, 7));
        //p.add(new ResumeVM(vm3, n2, n2, 3, 5));
        p.add(new BootVM(vm2, n1, 2, 7));
        //p.add(new ShutdownNode(n1, 12, 15));
        p.add(new BootNode(n3, 0, 3));
        p.add(new MigrateVM(vm1, n1, n3, 4, 10));

        SpecReader ex = new SpecReader();
        Constraint cstr = ex.extractConstraints(new File("src/test/resources/noVMonOfflineNode.cspec")).get(0);

        ImplVerifier verif = new ImplVerifier();
        //System.out.println(p.getOrigin().getMapping());
        List<Object> in = new ArrayList<>();
        in.add(n1);
        TestCase tc = new TestCase(0, p, new Offline(Collections.singleton(n2)), cstr.eval(p.getResult(), in));
        System.out.println(cstr.getProposition());
        TestResult tr = verif.verify(tc);
        System.out.println(tr);
        //TestCaseReducer r = new TestCaseReducer();
        //r.reducePlan(0, tc, cstr, in);

        TestCaseReducer tcr = new TestCaseReducer();
        List<TestCase> reduced = tcr.reducePlan(tc, cstr, in);

        System.out.println("------------\nReduced Test Cases\n-----------");
        for (TestCase t : reduced) {
            System.out.println(t);
            System.out.println(verif.verify(t));
            System.out.println("---");
        }
        Assert.fail();
    }
}
