package s0542645;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Vector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DriverAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.track.Track;

public class bestAI extends AI {
	/*
	 * Projekt von Nils Sigmund (s0542645) und Florian Wollenzien (s0542646)
	 */

	//	private Track Track;
	boolean obstCrash;
	float x,y;
	Rectangle obst;
	float orientationKombination;
	ArrayList<Point> waypoints;

	public bestAI(Info info) {
		super(info);
		waypoints = new ArrayList<Point>();
		VerticalCellDecomposition();
	}

	public String getName() {
		return "s0542645";
	}

	public DriverAction update(boolean reset) {	

		float angAcceleration;
		float linAcceleration = 1;
		float checkPointX = (float) info.getCurrentCheckpoint().getX();
		float checkPointY = (float) info.getCurrentCheckpoint().getY();

		float carOrientation = info.getOrientation();		
		float angleToCheckpoint = getAngleToCheckpoint(checkPointY, checkPointX);
		float orientationDifference = (float) Math.atan2(Math.sin(angleToCheckpoint-carOrientation), Math.cos(angleToCheckpoint-carOrientation));
		//System.out.println(getDistance(x,y));

		Polygon[] obstacles = info.getTrack().getObstacles();
		for (int i = 0; i < obstacles.length; i++){
			Polygon obstacle = obstacles[i];
			obst = obstacle.getBounds();
			x = (float) obst.getCenterX();
			y = (float) obst.getCenterY();
			float obstacleVec = getAngleToCheckpoint(y, x);
			float orientationDifferenceFlee = (float) Math.atan2(Math.sin(carOrientation - obstacleVec), Math.cos(carOrientation- obstacleVec));		
			//System.out.println(obst);
			float avoidDistance = (float) (obst.getWidth()/2+20);
			//System.out.println("X:  " +info.getX() + "  Y:  " +info.getY());

			if(getDistance(x,y) <= avoidDistance){
				orientationKombination = orientationDifference + 0.1F * orientationDifferenceFlee;	
				//				 System.out.println("crash");
				obstCrash = true;
			}else{
				//System.out.println(info.getVelocity());
				obstCrash = false;

				orientationKombination = orientationDifference;	
			}
		}	

		//		 if(obst.contains(info.getX()+info.getVelocity().x*1.2, info.getY()+info.getVelocity().y*1.2)){
		//			 orientationKombination = orientationDifference + 1F * orientationDifferenceFlee;	
		//			 System.out.println("crash");
		//		 }else{
		//			 System.out.println(info.getVelocity());
		//			 orientationKombination = orientationDifference;	
		//		 }

		//		if(Math.abs(orientationKombination) < (Math.PI / 4)){
		//			angAcceleration = (float) (orientationKombination * info.getMaxAngularAcceleration() / (Math.PI / 4));	//Angular Acceleration
		//			
		//		} else {
		//			angAcceleration = Math.signum(orientationKombination) * info.getMaxAngularAcceleration();	//Angular Acceleration
		//		}

		if(Math.abs(orientationDifference) < (Math.PI / 4)){
			angAcceleration = (float) (orientationDifference * info.getMaxAngularAcceleration() / (Math.PI / 4));	//Angular Acceleration

		} else {
			angAcceleration = Math.signum(orientationDifference) * info.getMaxAngularAcceleration();	//Angular Acceleration
		}

		angAcceleration = angAcceleration - info.getAngularVelocity();

		return new DriverAction(linAcceleration, angAcceleration);
	}

	private void VerticalCellDecomposition(){
		Polygon[] obstacles = info.getTrack().getObstacles();
		boolean addPointBoolean = true;
		for (int i = 2; i < obstacles.length; i++){ // i = 2 weil die ersten beiden sind map, für alle polygone..
			Polygon obstacle = obstacles[i]; // wählen einzelnes polygon
			//System.out.println(obstacle.xpoints.length);
			//nach oben

			for (int z = 0; z < obstacle.npoints; z++){ // für die anzahl an x punkten für dieses polygon
				float pointX = obstacle.xpoints[z];	// x punkt
				float pointY = obstacle.ypoints[z]; // y punkt für jeden punkt
				//System.out.println(pointX + "  " + pointY);
				float pointYtest = obstacle.ypoints[z]; // y punkt zum berechnen

				for (int y = 2; y < obstacles.length; y++){ // für jedes polygon, um den waypoint zu berechnen
					Polygon testObst = obstacles[y];
					//System.out.println("number: " + y + " :" +testObst);
					pointYtest = obstacle.ypoints[z];

					if(!testObst.contains((int)pointX, (int)pointYtest+1)){
						addPointBoolean = true;
						while(true){
							pointYtest ++;
							if(!(pointYtest <= 1000)) break;

						}	
					}else{
						addPointBoolean = false;
					}						
				}
				if(addPointBoolean)waypoints.add(new Point((int)pointX, (int)((pointYtest - pointY)/2 + pointY)));

				//System.out.println(pointX +  "  "+ ((pointYtest - pointY)/2 + pointY));			


			}
		}
	}


	////			//nach unten
	//			for (z = 0; z < obstacle.xpoints.length; z++){
	//			pointX = obstacle.xpoints[z];
	//			pointY = obstacle.ypoints[z];
	//			pointYtest = obstacle.ypoints[z];
	//			
	//				for (int y = 2; y < obstacles.length; y++){
	//					Polygon testObst = obstacles[y];
	//					pointYtest = obstacle.ypoints[z];
	//					while(true){
	//						pointYtest --;
	//						if(testObst.contains((int)pointX, (int)pointYtest) || pointYtest <= 0) break;
	//					}
	//					waypoints.add(new Point((int)pointX, (int)((pointYtest - pointY)/2 - pointY)));
	//				}
	//			}


	//		for(int r = 0; r < waypoints.size(); r ++){
	//			System.out.println(waypoints.get(r));
	//		}


	//	public String getTextureResourceName() {
	//	 return "/s0542645/s0542645.png";
	//	}

	private float getDistanceToCheckpoint() {
		return (float) Math.sqrt(Math.pow(info.getCurrentCheckpoint().x - info.getX(), 2) + Math.pow(info.getCurrentCheckpoint().y - info.getY(), 2));
	}
	private float getDistance(float x, float y) {
		return (float) Math.sqrt(Math.pow(x - info.getX(), 2) + Math.pow(y - info.getY(), 2));
	}

	private float getAngleToCheckpoint(float y, float x) {
		return (float) Math.atan2(y - info.getY(), x - info.getX());
	}

	public boolean isEnabledForRacing() {
		return true;
	}


	public void doDebugStuff(){
		GL11.glBegin(GL11.GL_LINES);
		GL11.glColor3f(1,1,1);
		GL11.glVertex2d(info.getX()+info.getVelocity().x*2, info.getY()+info.getVelocity().y*2);
		GL11.glVertex2d(info.getX(), info.getY());
		if(obstCrash) GL11.glColor3f(1,0,0);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(info.getX(), info.getY());
		for(int i = 0; i < waypoints.size(); i++){
			GL11.glColor3f(0,1,1);
			GL11.glVertex2d(waypoints.get(i).getX()-2, waypoints.get(i).getY());
			GL11.glVertex2d(waypoints.get(i).getX()+2, waypoints.get(i).getY());
		}
		GL11.glColor3f(0.2F,0.5F,0.8F);
		GL11.glVertex2d(info.getX(), info.getY());
		GL11.glVertex2d(0, 0);
		GL11.glEnd();

	}


}