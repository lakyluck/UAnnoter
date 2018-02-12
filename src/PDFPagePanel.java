/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JPanel;

//import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

/**
 * This is a simple JPanel that can be used to display a PDF page.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.4 $
 */
public class PDFPagePanel extends JPanel implements MouseMotionListener, MouseListener{

    private static final long serialVersionUID = -4629033339560890669L;
    
    private PDPage page;
    private PageDrawer drawer = null;
    private Dimension pageDimension = null;
    private Dimension drawDimension = null;
    private annotation annoter = null;
    private Graphics graphics = null;
    private PDAnnotation annotation = null;

    /**
     * Constructor.
     *
     * @throws IOException If there is an error creating the Page drawing objects.
     */
    public PDFPagePanel(annotation annoter ) throws IOException
    {
    	this.annoter = annoter;
        drawer = new PageDrawer();
		addMouseMotionListener(this);
		addMouseListener(this);
    }

    /**
     * This will set the page that should be displayed in this panel.
     *
     * @param pdfPage The page to draw.
     */
    public void setPage( PDPage pdfPage )
    {
        page = pdfPage;
        PDRectangle cropBox = page.findCropBox();
        drawDimension = cropBox.createDimension();
        if (page.findRotation() % 180 == 0)
        {
            pageDimension = drawDimension;
        }
        else
        {
            pageDimension = new Dimension(drawDimension.height, drawDimension.width);
        }
        setSize( pageDimension );
        setBackground( java.awt.Color.white );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g )
    {
    	graphics = g;
    	

    	
        try
        {
            g.setColor( getBackground() );
            g.fillRect( 0, 0, getWidth(), getHeight() );

            int rotationAngle = page.findRotation();
            // normalize the rotation angle
            while (rotationAngle < 0)
            {
                rotationAngle += 360;
            }
            while (rotationAngle >= 360)
            {
                rotationAngle -= 360;
            }
            if (rotationAngle != 0)
            {
                Graphics2D g2D = (Graphics2D)g;
                double translateX = 0;
                double translateY = 0;
                switch (rotationAngle)
                {
                    case 90:
                        translateX = pageDimension.getWidth();
                        break;
                    case 270:
                        translateY = pageDimension.getHeight();
                        break;
                    case 180:
                        translateX = pageDimension.getWidth();
                        translateY = pageDimension.getHeight();
                        break;
                }
                g2D.translate(translateX, translateY);
                g2D.rotate(Math.toRadians(rotationAngle));
            }
            drawer.drawPage( g, page, drawDimension );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

    	dessinerAnnotation();
    	
    }
    
    public void dessinerAnnotation(){

    	if(annotation != null){
    		if(annotation.getRectangle() != null){
	    		PDRectangle rect = annotation.getRectangle();
	            int[] pos = calculerCoordonnes(rect);
	            int x = pos[0];int y = pos[1];int w = pos[2];int h = pos[3];
	            //Color c = new Color(annot.getColour().getR(), annot.getColour().getG(), annot.getColour().getB());
	            Color c = new Color(1, 0, 0);
	            graphics.setColor(c);
	            graphics.drawRect(x,y-3,w,h+7);
    		}
    	}
    	
    }
    
    public void afficherAnnotation(PDAnnotation annot){
    	annotation = annot;
    	paint(this.getGraphics());
    }



    public int[] calculerCoordonnes(PDRectangle rect){
    	
		float xh = rect.getUpperRightX();
		float yh = rect.getUpperRightY();
		float xb = rect.getLowerLeftX();
		float yb = rect.getLowerLeftY();
        //System.out.println(xh+","+yh+","+xb+","+yb);
    	float hp = page.getMediaBox().getHeight();
    	float wp = page.getMediaBox().getWidth();

        //System.out.println(wp+","+hp);
    	
    	int lx = (int) (xb);
    	int ly = (int) (hp - yb);
    	int ux = (int) (xh);
    	int uy = (int) (hp - yh);
        //System.out.println(lx+","+ly+","+ux+","+uy);
        int x = lx;int y = ly;int h = ly - uy;int w = ux - lx;
        //System.out.println(x+","+y+","+w+","+h);
	

    	int[] r = {x,y-h,w,h};    	
    	return r;    	
    }
    
    
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (annoter != null)
			annoter.appliquer(arg0);
		
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		if (annoter != null)
			annoter.commencer(arg0);
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (annoter != null)
			annoter.arreter();
			
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0){
		//if (annoter != null)
			//annoter.appliquer(arg0);
			
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
    
}
