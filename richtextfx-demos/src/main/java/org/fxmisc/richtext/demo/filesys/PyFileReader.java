package org.fxmisc.richtext.demo.filesys;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author yq
 */
public class PyFileReader {

    public static Optional<List<String>> readFileAsCode(PyFile pyFile) {
        try {
            File file = new File(pyFile.getAbsPath());
            if (!file.exists()){
                return Optional.empty();
            }
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
            return Optional.ofNullable(lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
