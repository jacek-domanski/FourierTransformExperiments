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
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.*;

public class Main extends Application {
    private Pane canvas;
    private final int SIZE_X = 1280;
    private final int SIZE_Y = 768;

    private final Point ORIGIN = new Point((float) SIZE_X /2, (float) SIZE_Y /2);
    private final double TIME_MULTIPLIER = 10.0;
    private final double TRAILING_SAMPLES_COEFFICIENT = 0.95;
    private double t;

    private ArrayList<Line> lines = new ArrayList<>();
    private ArrayList<Circle> trendCircles = new ArrayList<>();

    private ArrayList<ComplexNumber> signalFrequency;

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

        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#000428")),
                new Stop(1, Color.web("#004e92"))};
        LinearGradient backgroundLinearGradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        canvas.setBackground(new Background(
                new BackgroundFill(backgroundLinearGradient,
                        CornerRadii.EMPTY,
                        Insets.EMPTY)));
    }

    private ArrayList<ComplexNumber> discreteFourierTransform(ArrayList<ComplexNumber> x) {
        ArrayList<ComplexNumber> X = new ArrayList<>();

        final int WAVE_COUNT = x.size();
        for (int k = 0; k < WAVE_COUNT; k++) {
            X.add(generateWave(x, k));
        }

        sortWavesByAmplitude(X);
        printAmplitudeSumAndWaveCount(X);
        return X;
    }

    private ComplexNumber generateWave(ArrayList<ComplexNumber> x, int k) {
        ComplexNumber complex = new ComplexNumber(0,0);
        final int N = x.size();

        for (int n = 0; n < N; n++) {
            double phi = (2 * PI * k * n) / N;
            ComplexNumber signalTime = x.get(n);
            ComplexNumber sinusoidalComponent = new ComplexNumber(cos(phi), -sin(phi));
            complex.add(ComplexNumber.multiply(signalTime, sinusoidalComponent));
        }
        complex.setRe(complex.getRe()/N);
        complex.setIm(complex.getIm()/N);

        complex.setWaveNo(k);
        return complex;
    }

    private void sortWavesByAmplitude(ArrayList<ComplexNumber> x) {
        x.sort(Comparator.comparingDouble(ComplexNumber::getAmplitude).reversed());
    }

    private void printAmplitudeSumAndWaveCount(ArrayList<ComplexNumber> X) {
        System.out.println("DFT done: " + X.size() + " waves generated");
        double amplitude_sum = 0;

        for (int i = 0; i < X.size(); i++) {
            amplitude_sum += X.get(i).getAmplitude();
        }
        System.out.println("Amplitude sum: " + String.format("%.3f", amplitude_sum));
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
            t += TIME_MULTIPLIER*dt;
            t %= 2*PI;

            ArrayList<Point> linesEndpoints = generateEndpointsFromSignalFrequency(signalFrequency);
            drawLines(linesEndpoints);

            Point lastPoint = linesEndpoints.get(linesEndpoints.size()-1);
            updateTrend(lastPoint, signalFrequency.size());
        }
    }

    private ArrayList<Point> generateEndpointsFromSignalFrequency(ArrayList<ComplexNumber> signalFrequency) {
        ArrayList<Point> points = new ArrayList<>();
        points.add(ORIGIN);
        for (int i = 0; i < signalFrequency.size(); i++) {
            ComplexNumber complex = signalFrequency.get(i);
            points.add(
                new Point(points.get(points.size() - 1),
                (complex.getWaveNo()*t) + complex.getPhase(),
                complex.getAmplitude()));
        }

        return points;
    }

    private ArrayList<Point> generateEndpointsFromConstant() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(ORIGIN);
        points.add(new Point(points.get(points.size()-1), 30+3*t, 150));
        points.add(new Point(points.get(points.size()-1), 150+7*t, 100));
        points.add(new Point(points.get(points.size()-1), 270+9*t, 75));

        return points;
    }

    private void drawLines(ArrayList<Point> points) {
        while (lines.size() > 0) {
            canvas.getChildren().remove(lines.remove(0));
        }

        for (int i = 0; i < points.size() - 1; i++) {
            Line line = new Line();
            line.setStartX(points.get(i).x);
            line.setStartY(points.get(i).y);
            line.setEndX(points.get(i+1).x);
            line.setEndY(points.get(i+1).y);
            line.setStroke(Color.WHITE);
            line.setSmooth(true);
            line.setStrokeWidth(2);
            lines.add(line);
        }
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
