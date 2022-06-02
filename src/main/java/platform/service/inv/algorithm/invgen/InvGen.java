package platform.service.inv.algorithm.invgen;

import com.opencsv.exceptions.CsvValidationException;
import platform.service.inv.struct.inv.Inv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface InvGen {
    void run();
    Map<String, Map<String, Map<Integer, Map<Integer, Inv>>>> getInvMap();
}
