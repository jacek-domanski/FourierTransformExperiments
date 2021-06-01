package sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Kookaburra {
    public static ArrayList<ComplexNumber> generate() throws IOException {
        String fileName = "C:\\Java\\FourierTransformExperiments\\FourierTest\\src\\sample\\kookaburra_path_sample.txt";
        File file = new File(fileName);
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName));

        bufferedReader.lines().forEach(System.out::println);

        ArrayList<ComplexNumber> signals = new ArrayList<>();


        double minRe = signals.get(0).getRe();
        double minIm = signals.get(0).getIm();
        double maxRe = signals.get(0).getRe();
        double maxIm = signals.get(0).getIm();

        for (int i = 0; i < signals.size(); i++) {
            minRe = Math.min(minRe, signals.get(i).getRe());
            minIm = Math.min(minIm, signals.get(i).getIm());

            maxRe = Math.max(maxRe, signals.get(i).getRe());
            maxIm = Math.max(maxIm, signals.get(i).getIm());
        }

        double reOffset =minRe + ((maxRe - minRe) / 2);
        double imOffset =minIm + ((maxIm - minIm) / 2);

        for (int i = 0; i < signals.size(); i++) {
            signals.set(i, new ComplexNumber(
                    (signals.get(i).getRe() - reOffset)*0.8,
                    (signals.get(i).getIm() - imOffset)*0.8));
        }
        return signals;
    }
}
