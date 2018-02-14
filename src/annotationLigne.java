import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;


public class annotationLigne extends annotation{
	
	private String annotation = "annotationLigne";

	private int annot;
	
	
	private float x = 0;
	private float y = 0;

	
	
	public annotationLigne(String SRC, String DST,PDPage page,int pageToAnnote, callback callback,PDGamma couleur) {
		super( SRC,  DST, page, pageToAnnote,  callback,couleur);
		/*
		this.callback = callback;
		this.pageToAnnote = pageToAnnote;
		this.page = page;
		this.SRC = SRC;
		this.DST = DST;
		*/
	}


	public void appliquer(MouseEvent arg0){
		//System.out.println("appliquer : ("+arg0.getX()+","+arg0.getY()+")  "+annotation);
    	float[] position = calculerCoordonnes(arg0.getX(), arg0.getY());
		float z = position[0];
		float w = position[1];
		//manipulatePdf(SRC,DST,pageToAnnote,x,y);
		
		float[] pos ={x, y, z+5, w+10};
		modifierAnnotation(SRC,DST,pos,annot);
		this.callback.setfile();

	}
	

	@Override
	public void commencer(MouseEvent arg0) {
		//System.out.println("commencer : ("+arg0.getX()+","+arg0.getY()+")  "+annotation);
    	float[] position = calculerCoordonnes(arg0.getX(), arg0.getY());
		x = position[0];
		y = position[1];

		float[] pos ={x, y, x+5, y+10};
		ajouterAnnotation(SRC,DST,pos);
		this.callback.setfile();

	}


	@Override
	public void arreter() {
		//System.out.println("arreter");
		x=y=0;
		this.callback.setRefreshToolbar();

	}
    
    
    public float[] calculerCoordonnes(float x, float y){
    	float h = page.getMediaBox().getHeight();
    	float[] r = {x, h-y};    	
    	return r; 
    }
    
	public String getAnnotation() {
		return annotation;
	}
	
	
	
	
	public void modifierAnnotation(String filename, String docResultPath, float[] pos, int annot){
		//System.out.println("modifierAnnotation :  "+annotation);

		try {
			PDDocument doc = PDDocument.loadNonSeq(new File(filename), null);
			
			PDPage page = (PDPage) doc.getDocumentCatalog().getAllPages().get(pageToAnnote);
			
			List<PDAnnotation> annotations = page.getAnnotations();
			//System.out.println("annotations : "+annotations.size());
			
			 PDRectangle position = new PDRectangle();
			 position.setLowerLeftX(pos[0]);
			 position.setLowerLeftY(pos[1]);
			 position.setUpperRightX(pos[2]);
			 position.setUpperRightY(pos[1]+10);
			 //position.setUpperRightY(pos[3]);

			 float[] quads = new float[8];
			 //x1,y1
			 quads[0] = position.getLowerLeftX();
			 quads[1] = position.getUpperRightY()-2;
			 //x2,y2
			 quads[2] = position.getUpperRightX();
			 quads[3] = quads[1];
			 //x3,y3
			 quads[4] = quads[0];
			 quads[5] = position.getLowerLeftY()-2;
			 //x4,y4
			 quads[6] = quads[2]; 
			 quads[7] = quads[5]; 
			 
			 PDAnnotationTextMarkup ann = (PDAnnotationTextMarkup) annotations.get(annot);
			 ann.setRectangle(position);
			 ann.setQuadPoints(quads);

			 doc.save(new File(docResultPath));
			 doc.close();
			
			
			
		}catch(Exception e){
			System.out.println("ERROR : "+e.getMessage());			
		}
		
		
	} 
	
	

	public void ajouterAnnotation(String filename, String docResultPath, float[] pos){
		//System.out.println("ajouterAnnotation :  "+annotation);
		try {
			PDDocument doc = PDDocument.loadNonSeq(new File(filename), null);
			PDPage pages = (PDPage) doc.getDocumentCatalog().getAllPages().get(pageToAnnote);

			
			List<PDAnnotation> annotations = pages.getAnnotations();
			annot = annotations.size();
			//System.out.println("annotations : "+annotations.size());
			//generate instanse for annotation
		    PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_UNDERLINE);

			 PDRectangle position = new PDRectangle();
		      position.setLowerLeftX(pos[0]);
		      position.setLowerLeftY(pos[1]);
		      position.setUpperRightX(pos[2]);
		      position.setUpperRightY(pos[1]+10);
		      //position.setUpperRightY(pos[3]);
		      txtMark.setRectangle(position);

		      //set the quadpoint
		      float[] quads = new float[8];
		      //x1,y1
		      quads[0] = position.getLowerLeftX();
		      quads[1] = position.getUpperRightY()-2;
		      //x2,y2
		      quads[2] = position.getUpperRightX();
		      quads[3] = quads[1];
		      //x3,y3
		      quads[4] = quads[0];
		      quads[5] = position.getLowerLeftY()-2;
		      //x4,y4
		      quads[6] = quads[2]; 
		      quads[7] = quads[5]; 
		      txtMark.setQuadPoints(quads);
		      txtMark.setContents("Highlighted since it's important");
		      
		      if(couleur == null){
		    	  couleur = new PDGamma();
		    	  couleur.setB(1);
		      }
		      txtMark.setColour(couleur);
		      annotations.add(txtMark);
		      
		      try {
				doc.save(docResultPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

				 doc.close();
		      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 

    
    
}












/*
try{
    PdfReader reader = new PdfReader(src);
    PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
    PdfContentByte canvas = stamper.getUnderContent(page+1);
    canvas.saveState();
    canvas.setColorFill(BaseColor.YELLOW);
    float[] position = calculerCoordonnes(x,y);
    canvas.rectangle(position[0],position[1],position[2],position[3]);
    //canvas.rectangle(70, 500, 66, 16);
    canvas.fill();
    canvas.restoreState();
    stamper.close();
    reader.close();
}catch(Exception e){
	System.out.println("ERROR 2 : "+e.getMessage());
}
*/






































































