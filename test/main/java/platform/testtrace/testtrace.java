package platform.testtrace;

import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class testtrace {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(
                Paths.get("output/grouptrace/csv/platform.app.userapps.MySyncApp-line18-overview"),
                StandardCharsets.UTF_8);
        for (String line : lines) {
            List<Integer> iters = JSONArray.parseArray(line.substring(line.indexOf('['), line.length()), Integer.class);
            for (Integer iter : iters) {
                System.out.println(iter);
            }
        }
    }
}
