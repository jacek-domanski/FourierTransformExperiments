package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

public class Main extends Application {
    private final int SIZE_X = 1280;
    private final int SIZE_Y = 768;

    private final Point ORIGIN = new Point((float) SIZE_X /2, (float) SIZE_Y /2);
    private final double TIME_MULTIPLIER = 10.0;
    private final double TRAILING_SAMPLES_COEFFICIENT = 0.95;

    private Pane canvas;
    private double time;

    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Circle> trendCircles = new ArrayList<>();

    private ArrayList<Wave> signalFrequency;

    @Override
    public void start(Stage primaryStage) throws Exception{
        addCanvas();
        signalFrequency = discreteFourierTransform(Kookaburra.generate());
        LinesUpdater linesUpdater = new LinesUpdater();
        linesUpdater.start();

        stageHandling(primaryStage);
    }

    private void addCanvas(){
        canvas = new Pane();
        canvas.setPrefSize(SIZE_X, SIZE_Y);

        Stop[] stops =
            new Stop[] {
                new Stop(0, Color.web("#000428")),
                new Stop(1, Color.web("#004e92"))};
        LinearGradient backgroundLinearGradient =
            new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        canvas.setBackground(new Background(
                new BackgroundFill(backgroundLinearGradient,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    private ArrayList<Wave> discreteFourierTransform(ArrayList<ComplexNumber> input) {
        // Implementation of https://en.wikipedia.org/wiki/Discrete_Fourier_transform#Definition
        // input - x
        // waves - X
        // WAVE_COUNT - N

        ArrayList<Wave> waves = new ArrayList<>();

        final int WAVE_COUNT = input.size();
        for (int i = 0; i < WAVE_COUNT; i++) {
            waves.add(generateWave(input, i));
        }

        sortWavesByAmplitude(waves);
        printAmplitudeSumAndWaveCount(waves);
        return waves;
    }

    private Wave generateWave(ArrayList<ComplexNumber> input, int waveNo) {
        Wave wave = new Wave(0,0);
        final int WAVE_COUNT = input.size();

        for (int i = 0; i < WAVE_COUNT; i++) {
            double phi = (2 * PI * waveNo * i) / WAVE_COUNT;
            ComplexNumber inputComponent = input.get(i);
            ComplexNumber sinusoidalComponent = new ComplexNumber(cos(phi), -sin(phi));
            wave.add(ComplexNumber.multiply(inputComponent, sinusoidalComponent));
        }
        wave.setRe(wave.getRe()/WAVE_COUNT);
        wave.setIm(wave.getIm()/WAVE_COUNT);

        wave.setWaveNo(waveNo);
        return wave;
    }

    private void sortWavesByAmplitude(ArrayList<Wave> waves) {
        waves.sort(Comparator.comparingDouble(Wave::getAmplitude).reversed());
    }

    private void printAmplitudeSumAndWaveCount(ArrayList<Wave> waves) {
        System.out.println("DFT done: " + waves.size() + " waves generated");

        double amplitudeSum =
            waves.stream()
                .mapToDouble(Wave::getAmplitude)
                .sum();

        System.out.println("Amplitude sum: " + String.format("%.3f", amplitudeSum));
    }

    private ArrayList<ComplexNumber> generateSignalTime() {
        ArrayList<ComplexNumber> x = new ArrayList<>();

        final int SAMPLE_COUNT = 200;
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            ComplexNumber complex = new ComplexNumber(
//                    70*sin((2*PI*(float)i)/SAMPLE_COUNT) + 30*cos((3*PI*2*(float)i)/SAMPLE_COUNT),
//                    50*sin((7*PI*(float)i)/SAMPLE_COUNT));
                    50,
                    50*cos((2*PI*(float)i)/SAMPLE_COUNT)+0*cos((20*2*PI*(float)i)/SAMPLE_COUNT));
            x.add(complex);
        }
        System.out.println(x);
        return x;
    }

    private class LinesUpdater extends AnimationTimer{
        @Override
        public void handle(long now) {
            double dt = 2*PI/signalFrequency.size();
            time += TIME_MULTIPLIER*dt;
            time %= 2*PI;

            ArrayList<Point> linesEndpoints = generateEndpointsFromSignalFrequency(signalFrequency);
            drawLines(linesEndpoints);

            Point lastPoint = linesEndpoints.get(linesEndpoints.size()-1);
            updateTrend(lastPoint, signalFrequency.size());
        }
    }

    private ArrayList<Point> generateEndpointsFromSignalFrequency(ArrayList<Wave> signalFrequency) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(ORIGIN);
//        for (Wave wave : signalFrequency) {
//            points.add(
//                new Point(
//                    points.get(points.size() - 1),
//                    wave.getWaveNo() * time + wave.getPhase(),
//                    wave.getAmplitude()));
//        }
        Point lastPoint = new Point (points.get(0), 0, 0);

        signalFrequency.stream()
            .map(wave -> new Point(
                lastPoint,
                wave.getWaveNo() * time + wave.getPhase(),
                wave.getAmplitude()
            ))
            .forEach(point -> {
                points.add(point);
                lastPoint.x = point.x;
                lastPoint.y = point.y;
            });

        return points;
    }

    private ArrayList<Point> generateEndpointsFromConstant() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(ORIGIN);
        points.add(new Point(points.get(points.size()-1), 30+3* time, 150));
        points.add(new Point(points.get(points.size()-1), 150+7* time, 100));
        points.add(new Point(points.get(points.size()-1), 270+9* time, 75));

        return points;
    }

    private void drawLines(ArrayList<Point> points) {
        while (lines.size() > 0) {
            canvas.getChildren().remove(lines.remove(0));
        }

        Point lastPoint = new Point(points.get(0), 0, 0);

        points.stream()
            .skip(1)
            .map(point ->
                new Line(
                    lastPoint.x,
                    lastPoint.y,
                    point.x,
                    point.y))
            .forEach(line -> {
                lastPoint.x = line.getEndX();
                lastPoint.y = line.getEndY();

                line.setStroke(Color.WHITE);
                line.setSmooth(true);
                line.setStrokeWidth(2);

                lines.add(line);
            });

        canvas.getChildren().addAll(lines);
    }

    private void updateTrend(Point new_point, int wave_count) {
        removeLastCircle(wave_count);
        addNewCircle(new_point);
    }

    private void removeLastCircle(int wave_count) {
        while (trendCircles.size() >= wave_count * TRAILING_SAMPLES_COEFFICIENT / TIME_MULTIPLIER) {
            Circle lastCircle = trendCircles.remove(trendCircles.size()-1);
            canvas.getChildren().remove(lastCircle);
        }
    }

    private void addNewCircle(Point point) {
        trendCircles.add(0, new Circle(point.x, point.y, 0.5, Color.LIGHTBLUE));
        canvas.getChildren().add(trendCircles.get(0));
    }

    private void stageHandling(Stage primaryStage){
        primaryStage.setTitle("Good morning Mr. Fourier");
        primaryStage.setScene(new Scene(canvas, SIZE_X, SIZE_Y));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
