package data;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.Serializable;

public class AdjustableLine implements Serializable{
	private static final long serialVersionUID = -3751460398995754360L;
	private Line2D.Double line;
	private Ellipse2D.Double circledirection;
	private Ellipse2D.Double circle1;
	private Ellipse2D.Double circle2;
	private double ellipseRadius;
	transient public boolean isSelect;
	
	public AdjustableLine(Line2D.Double line){
		this.line = line;
		ellipseRadius = 2;
		isSelect = false;
		circle1 = new Ellipse2D.Double(line.x1-ellipseRadius, line.y1-ellipseRadius, 
				2*ellipseRadius, 2*ellipseRadius);
		circle2 = new Ellipse2D.Double(line.x2-ellipseRadius, line.y2-ellipseRadius, 
				2*ellipseRadius, 2*ellipseRadius);
		circledirection = new Ellipse2D.Double(line.x2-2*ellipseRadius, line.y2-2*ellipseRadius, 
				4*ellipseRadius, 4*ellipseRadius);
	}
	
	public void setLinePoint1(double x, double y){
		this.line.x1 = x;
		this.line.y1 = y;
		this.circle1.x = this.line.x1 - ellipseRadius;
		this.circle1.y = this.line.y1 - ellipseRadius;
	}
	
	public void setLinePoint2(double x, double y){
		this.line.x2 = x;
		this.line.y2 = y;
		this.circle2.x = x - ellipseRadius;
		this.circle2.y = y - ellipseRadius;
		this.circledirection.x = x - 2*ellipseRadius;
		this.circledirection.y = y - 2*ellipseRadius;
	}
	
	public void setLine(double x1, double y1, double x2, double y2){
		setLinePoint1(x1, y1);
		setLinePoint2(x2, y2);
	}

	public Line2D.Double getLine() {
		return line;
	}

	public Ellipse2D.Double getDirectionCircle() {
		return circledirection;
	}
	
	public Ellipse2D.Double getCircle1() {
		return circle1;
	}

	public Ellipse2D.Double getCircle2() {
		return circle2;
	}
}
