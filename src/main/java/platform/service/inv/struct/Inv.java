package platform.service.inv.struct;

import common.struct.enumeration.CheckResult;
import platform.service.inv.struct.InvData;

public interface Inv {
    CheckResult check(InvData data);
}
