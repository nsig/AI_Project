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

public class StaticCar extends AI 
{
	public StaticCar(Info info) 
	{
		super(info);
		// TODO Auto-generated constructor stub
	}

	public String getName() 
	{
		return "Static Car";
	}

	public DriverAction update(boolean reset) 
	{	
		return new DriverAction(0, 0);
	}

	public boolean isEnabledForRacing() 
	{
		return true;
	}

	public void doDebugStuff(){


	}	
}