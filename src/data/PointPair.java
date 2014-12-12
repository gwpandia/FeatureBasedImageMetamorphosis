package data;

import java.awt.geom.Point2D;

public class PointPair {
	private Point2D.Double X;
	private Point2D.Double Xp;
	
	public PointPair(Point2D.Double x, Point2D.Double xp){
		this.X = x;
		this.Xp = xp;
	}

	public Point2D.Double getX() {
		return X;
	}

	public Point2D.Double getXp() {
		return Xp;
	}
	
	
}
