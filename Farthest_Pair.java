import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Farthest_Pair_Assignment extends PApplet {

int pointSize = 12;
int numPoints = 50;
     
Point2D[] S = new Point2D[ numPoints ]; //the set S
Point2D[] farthestPair = new Point2D[ 2 ]; //the two points of the farthest pair
     
ArrayList<Point2D> convexHull = new ArrayList(); //the vertices of the convex hull of S
     
int convexHullColour = color(255);
int genericColour = color(255,255,0);


public void setup( ) {    
    background(0);
    
    
    makeRandomPoints();
    findConvexHull();        
    findFarthestPair_BruteForceWay();        
}

    
//fills S with random points
public void makeRandomPoints() {
    for (int i = 0; i < numPoints; i++) {
        int x = 50 + (int) random(0,500);
        int y = 50 + (int) random(0,500);
        S[i] = new Point2D( x, y );            
    }        
}

public void draw() {        
    //draw the points in S
    noStroke();
    for(int i=0; i<S.length; i++){
      ellipse(S[i].x,S[i].y, 10, 10);
      
    }
    //draw the points in the convex hull
    stroke(255);
    for (int i=0; i<convexHull.size()-1; i++) {
      line(convexHull.get(i).x, convexHull.get(i).y, convexHull.get(i+1).x, convexHull.get(i+1).y);
    }
    //draw a red line connecting the farthest pair
    stroke(255,0,0);
    line(farthestPair[0].x,farthestPair[0].y,farthestPair[1].x,farthestPair[1].y);
}
    
    
public void findConvexHull() {
    
    float currLow = S[0].y; //creates a variable for the current lowest point, starts by setting it to the y value of the first point in array
    int currLowIndex = 0; //index of points array for current lowest point
    for(int i=0; i<S.length; i++){ //goes through array of points to find lowest point
      if(currLow> S[i].y){
        currLow = S[i].y;
        currLowIndex = i;
      }
      
    }
    convexHull.add(S[currLowIndex]); //adds lowest point to convex hull
    S[currLowIndex].visited = true; //changes boolean of visited for lowest point 
    Vector currVec = new Vector(width,0); //creates a horizontal vector to be the first vector 
    int currIndex = 0; //current index for convex hull array
    
    boolean visitedSame = false; //used for while loop, turns true when the program reaches a points it has visited which will stop the loop
    
    while(visitedSame == false){ 
      float smallAngle = 2*PI; //sets smallest angle to be 2pi which is the greatest angle so there will always be a angle lower
      int currSmallIndex = 0; //index for the point with the current smallest angle
      
      for(int i=0; i<S.length ; i++){ //for loop to find which point has the smallest angle between current convex hull point
      
        if(convexHull.get(currIndex) != S[i]){ //checks to see if the convex hull point that is being compared doesn't check with itself
        Vector nextVec = convexHull.get(currIndex).subtract(S[i]); //creates a vector between current convex hull point and point that is being checked
        float currAngle = currVec.getAngle(nextVec); //finds angle between
        
          if(currAngle<smallAngle){ //if the current angle is less than the smallest angle updates smallAngle and currSmallIndex
            smallAngle = currAngle;
            currSmallIndex = i;
          }
      
        }
      }
      convexHull.add(S[currSmallIndex]); //adds point with smallest angle to convex hull
      visitedSame = S[currSmallIndex].visited; //makes visited same whatever the last point that was added to the convex hull so if it is true (came back to start) the loop will stop
      S[currSmallIndex].visited = true; 
      currVec = convexHull.get(currIndex).subtract(convexHull.get(currIndex+1)); //sets the next currVec to be the vector between the current convex hull point and the point that was just added
      currIndex++; //updates index for convex hull array
    
     
    }
    
    
}
    
public void findFarthestPair_BruteForceWay() {
    float currMaxDistance = 0; //create variable for farthest distance
    float currDistance;
    int index1 = 0; //indexes for the points that will be the farthest pair
    int index2 = 0;
    
    for(int i = 0; i <convexHull.size(); i++){ //goes through convexHull array and compares distance between every point
      for(int k = 0; k < convexHull.size(); k++){
        currDistance = abs(sqrt(pow((convexHull.get(i).x - convexHull.get(k).x),2) + pow((convexHull.get(i).y - convexHull.get(k).y),2)));
        
        if(currDistance>currMaxDistance){ //when a new biggest distance is found updates currMaxDistance and indexes
          currMaxDistance = currDistance;
          index1 = i;
          index2 = k;
          
        }
        
      }
      
    }
    
    Point2D a = new Point2D(convexHull.get(index1).x,convexHull.get(index1).y);
    Point2D b = new Point2D(convexHull.get(index2).x,convexHull.get(index2).y);
    
    farthestPair[0] = a;
    farthestPair[1] = b;
        
}
    
   
   
class Point2D {
    float x, y;
    boolean visited; 
    int col;
    
   Point2D(float x, float y) {
        this.x = x;
        this.y = y;
        this.visited = false;
        this.col = color(255,255,0);
    }
    
    //Returns the vector that stretches between this and other.
    public Vector subtract( Point2D other ) {
        return new Vector( this.x - other.x, this.y - other.y);
    }
}
class Vector {
    
    float xComponent, yComponent;
    
    Vector(float x, float y) {
        this.xComponent = x;
        this.yComponent = y;
    }
    
    //needed as part of the convex hull algorithm and for finding the farthest pair within the vertices of the convex hull
    public Vector subtract( Vector other ) {
        return new Vector( this.xComponent - other.xComponent, this.yComponent - other.yComponent);
    }
    
    //needed as part of the convex hull algorithm and for finding the farthest pair within the vertices of the convex hull
    public float getAngle( Vector other ) {  
        float vDotw = this.dotProduct( other );
        float magV = this.magnitude();
        float magW = other.magnitude();
        
        return acos( vDotw / (magV*magW) );
    }
    
    //only used inside getAngle()
    public float dotProduct( Vector other ) {
        return this.xComponent*other.xComponent + this.yComponent*other.yComponent;
    }
    
    //only used inside getAngle()
    public float magnitude() {
        return sqrt( this.xComponent*this.xComponent + this.yComponent*this.yComponent );
    }
}
  public void settings() {  size(800, 800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Farthest_Pair_Assignment" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
