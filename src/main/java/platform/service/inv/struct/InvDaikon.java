package platform.service.inv.struct;

import common.struct.enumeration.CheckResult;
import daikon.inv.Invariant;
import daikon.inv.binary.BinaryInvariant;
import daikon.inv.ternary.TernaryInvariant;
import daikon.inv.unary.UnaryInvariant;
import daikon.inv.InvariantStatus;

import java.util.List;

public class InvDaikon implements Inv {
    List<Invariant> invs;

    public InvDaikon(List<Invariant> invs) {
        this.invs = invs;
        invs.forEach(inv -> {
            System.out.println(
                    inv.format() +
                            " " +
                            inv.getClass().getName() +
                            " (" +
                            inv.ppt.num_samples() +
                            " " +
                            "samples)" +
                            " " +
                            inv.getConfidence());
        });
    }

    @Override
    public CheckResult check(InvData data) {
        CheckResult ret = CheckResult.INV_PASSED;
        for (Invariant inv : invs) {
            if (inv instanceof daikon.inv.unary.UnaryInvariant) {
                UnaryInvariant inv1 = (UnaryInvariant) inv;
                if (inv1.check(data.checkVals.get(inv1.ppt.var_infos[0].name()), 1, 1) != InvariantStatus.NO_CHANGE) {
                    ret = CheckResult.INV_VIOLATED;
                    break;
                }
            } else if (inv instanceof daikon.inv.binary.BinaryInvariant) {
                BinaryInvariant inv2 = (BinaryInvariant) inv;
                if (inv2.check(data.checkVals.get(inv2.ppt.var_infos[0].name()),
                        data.checkVals.get(inv2.ppt.var_infos[1].name()), 1, 1) != InvariantStatus.NO_CHANGE) {
                    ret = CheckResult.INV_VIOLATED;
                    break;
                }
            } else if (inv instanceof daikon.inv.ternary.TernaryInvariant) {
                TernaryInvariant inv3 = (TernaryInvariant) inv;
                if (inv3.check(data.checkVals.get(inv3.ppt.var_infos[0].name()),
                        data.checkVals.get(inv3.ppt.var_infos[1].name()),
                        data.checkVals.get(inv3.ppt.var_infos[2].name()), 1, 1) != InvariantStatus.NO_CHANGE) {
                    ret = CheckResult.INV_VIOLATED;
                    break;
                }
            }
        }
        return ret;
    }
}
