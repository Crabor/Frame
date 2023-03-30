package platform.service.inv.struct;

import common.struct.enumeration.CheckResult;

public interface Inv {
    CheckResult check(InvData data);
}
