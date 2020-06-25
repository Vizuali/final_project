import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import gab.opencv.*; 
import processing.video.*; 
import java.awt.Rectangle; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class face_detection extends PApplet {

 
 

 
Capture cam; 
OpenCV opencv; 
Rectangle[] faces;

PShader blur;
PGraphics pass1, pass2;

 
public void setup() { 
   
  background (0, 0, 0); 
  cam = new Capture( this, 640, 480, 30); 
  cam.start(); 
  opencv = new OpenCV(this, cam.width, cam.height); 
  opencv.loadCascade(OpenCV.CASCADE_FRONTALFACE);
  
  blur = loadShader("blur.glsl");
  blur.set("blurSize", 19);
  blur.set("sigma", 7.0f);  
  
  pass1 = createGraphics(width, height, P2D);
  pass1.noSmooth();  
  
  pass2 = createGraphics(width, height, P2D);
  pass2.noSmooth();
}
 
public void draw() { 
  clear();
  background(255);
  opencv.loadImage(cam); 
  faces = opencv.detect(); 
  image(cam, 0, 0); 
 
  if (faces!=null) { 
    for (int i=0; i< faces.length; i++) { 
      /* noFill(); 
      stroke(255, 255, 0); 
      strokeWeight(0); */
      PImage faceImage = get(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
      
      // OpenCV opencv2 = new OpenCV(this, faceImage);

      // opencv2.blur(30);
      // PImage blurImage = opencv2.getSnapshot();
      // image(blurImage, faces[i].x, faces[i].y);
      
      // Applying the blur shader along the vertical direction   
      blur.set("horizontalPass", 0);
      pass1.beginDraw();            
      pass1.shader(blur);  
      pass1.image(faceImage, 0, 0);
      pass1.endDraw();

      // Applying the blur shader along the horizontal direction      
      blur.set("horizontalPass", 1);
      pass2.beginDraw();            
      pass2.shader(blur);  
      pass2.image(pass1, 0, 0);
      pass2.endDraw();

      image(pass2, faces[i].x, faces[i].y);       
      
      // noFill();
      // rect(faces[i].x, faces[i].y, faces[i].width, faces[i].height);
    }
  } 
  if (faces.length<=0) { 
    textAlign(CENTER); 
    fill(255, 0, 0); 
    textSize(56);
    text("UNDETECTED", 200, 100);
  }
}
 
public void captureEvent(Capture cam) { 
  cam.read();
}
  public void settings() {  size(640, 480, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "face_detection" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
