package sample;

import javafx.scene.shape.Line;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public Point(Point p, double angleRads, double distance) {
        this.x = p.x + distance * Math.cos(angleRads);
        this.y = p.y + distance * Math.sin(angleRads);
    }

    public double distance(Point other) {
        double xDistance = this.x - other.x;
        double yDistance = this.y - other.y;

        return Math.hypot(xDistance, yDistance);
    }

    public Point copy() {
        return new Point(this);
    }

    public Point translatedInPolar(double angle, double distance) {
        return new Point(this, angle, distance);
    }

    public void set(Point other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void setToLineEnd(Line line) {
        this.x = line.getEndX();
        this.y = line.getEndY();
    }

    public Line makeLine(Point end) {
        return new Line(this.x, this.y, end.x, end.y);
    }

    public String toString() {
        return "x: "+ String.format("%.2f", this.x) +" y: "+ String.format("%.2f", this.y);

    }

    public String toString(int precision) {
        return "x: "+ String.format("%."+precision+"f", this.x) +" y: "+ String.format("%."+precision+"f", this.y);
    }
}
