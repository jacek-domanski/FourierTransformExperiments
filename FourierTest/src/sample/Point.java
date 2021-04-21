package sample;

public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point p, double angleRads, double length) {
        this.x = p.x + length * Math.cos(angleRads);
        this.y = p.y + length * Math.sin(angleRads);
    }

    public double distance(Point other) {
        double xDistance = this.x - other.x;
        double yDistance = this.y - other.y;

        return Math.hypot(xDistance, yDistance);

    }

    public String toString() {
        return "x: "+ String.format("%.2f", this.x) +" y: "+ String.format("%.2f", this.y);

    }

    public String toString(int precision) {
        return "x: "+ String.format("%."+precision+"f", this.x) +" y: "+ String.format("%."+precision+"f", this.y);
    }
}
