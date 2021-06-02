package sample;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Kookaburra {
    public static ArrayList<ComplexNumber> generate() throws IOException {
        String fileName = "C:\\Java\\FourierTransformExperiments\\FourierTest\\src\\sample\\kookaburra_path.txt";
        ArrayList<ComplexNumber> signals = readFile(fileName);

        ComplexNumber offset = calculateOffset(signals);
        ComplexNumber scale = new ComplexNumber(0.8, 0);

        applyOffsetAndScale(signals, offset, scale);

        return signals;
    }

    private static ArrayList<ComplexNumber> readFile(String fileName) throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(fileName));

        return bufferedReader.lines()
                .map(line -> line.split(","))
                .map(coordsStr -> Arrays.stream(coordsStr)
                        .map(Double::parseDouble)
                        .toArray(Double[]::new))
                .map(coords -> new ComplexNumber(coords[0], coords[1]))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static ComplexNumber calculateOffset(ArrayList<ComplexNumber> signals) {
        ComplexNumber min = signals.get(0).copy();
        ComplexNumber max = signals.get(0).copy();

        getMinAndMax(signals, min, max);

        double reOffset = min.getRe() + ((max.getRe() - min.getRe()) / 2);
        double imOffset = min.getIm() + ((max.getIm() - min.getIm()) / 2);

        return new ComplexNumber(reOffset, imOffset);
    }

    private static void getMinAndMax(ArrayList<ComplexNumber> signals, ComplexNumber min, ComplexNumber max) {
        for (ComplexNumber signal : signals) {
            if (signal.getRe() < min.getRe()) {
                min.setRe(signal.getRe());
            } else if (signal.getRe() > max.getRe()) {
                max.setRe(signal.getRe());
            }

            if (signal.getIm() < min.getIm()) {
                min.setIm(signal.getIm());
            } else if (signal.getIm() > max.getIm()) {
                max.setIm(signal.getIm());
            }
        }
    }

    private static void applyOffsetAndScale(ArrayList<ComplexNumber> signals, ComplexNumber offset, ComplexNumber scale) {
        signals.forEach(signal -> {
            signal.sub(offset);
            signal.multiply(scale);
        });
    }
}
