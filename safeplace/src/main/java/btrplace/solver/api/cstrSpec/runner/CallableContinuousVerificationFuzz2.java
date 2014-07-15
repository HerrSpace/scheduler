package btrplace.solver.api.cstrSpec.runner;

import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.api.cstrSpec.Constraint;
import btrplace.solver.api.cstrSpec.fuzzer.ReconfigurationPlanFuzzer;
import btrplace.solver.api.cstrSpec.spec.term.Constant;
import btrplace.solver.api.cstrSpec.verification.CheckerResult;
import btrplace.solver.api.cstrSpec.verification.TestCase;
import btrplace.solver.api.cstrSpec.verification.Verifier;
import btrplace.solver.api.cstrSpec.verification.spec.VerifDomain;

import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class CallableContinuousVerificationFuzz2 extends DefaultCallableVerification {
    public CallableContinuousVerificationFuzz2(ParallelConstraintVerificationFuzz master, ReconfigurationPlanFuzzer fuzz, Verifier ve, List<VerifDomain> vDoms, Constraint c) {
        super(master, fuzz, ve, vDoms, c);
    }

    public TestCase runTest(ReconfigurationPlan p, List<Constant> args) {
        CheckerResult specRes = specVerifier.verify(c, p, args);
        CheckerResult againstRes = ve.verify(c, p, args);

        return new TestCase(specRes, againstRes, ve, this.c, p, args, false);
    }

}
