import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectForm;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;


public class anotateBoxPdfApparence {



	public static void ajouterAnnotation(String filename, String docResultPath, float[] pos){
		try {
			PDDocument doc = PDDocument.loadNonSeq(new File(filename), null);
			PDPage pages = (PDPage) doc.getDocumentCatalog().getAllPages().get(0);

			float h = pages.getMediaBox().getHeight();
			float w = pages.getMediaBox().getWidth();
			
			System.out.println(h+","+w);
			
			List<PDAnnotation> annotations = pages.getAnnotations();
			System.out.println("annotations : "+annotations.size());
			//generate instanse for annotation
		      PDAnnotationTextMarkup txtMark = new PDAnnotationTextMarkup(PDAnnotationTextMarkup.SUB_TYPE_HIGHLIGHT);

		      
			 PDRectangle position = new PDRectangle();
		      position.setLowerLeftX(pos[0]);
		      position.setLowerLeftY(h-pos[1]);
		      position.setUpperRightX(pos[2]);
		      position.setUpperRightY(h-pos[3]);
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
		      
		      PDGamma colourBlue = new PDGamma();
		        colourBlue.setR(1);
		        colourBlue.setG(1);
		        txtMark.setColour(colourBlue);
		        
		      //create the Form for the appearance stream
		        PDResources holderFormResources = new PDResources();
		        
		        PDStream holderFormStream = new PDStream(doc);
		        PDXObjectForm holderForm = new PDXObjectForm(holderFormStream);
		        holderForm.setResources(holderFormResources);
		        holderForm.setBBox(position);
		        holderForm.setFormType(1);
		        
		     // trying to set the appreanceStream for the annotation
		        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
		        appearance.getCOSObject().setDirect(true);
		        PDAppearanceStream appearanceStream = new PDAppearanceStream(holderForm.getCOSStream());
		        holderForm.getCOSStream().createFilteredStream();
		        appearance.setNormalAppearance(appearanceStream);
		        //appearance.setDownAppearance(appearanceStream);
		        //appearance.setRolloverAppearance(appearanceStream);
		        
		        txtMark.setAppearance(appearance);
		        
		        annotations.add(txtMark);
		      

		      
		      try {
				doc.save(docResultPath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      
		      
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	} 
	
	
	public static void main(String[] args) {


		String filename = "/home/etudiant/Bureau/pdfBox/test/tst/test.pdf";
		String docResultPath = "/home/etudiant/Bureau/pdfBox/test/tst/test222.pdf";

		//float[] position ={59, 61, 95, 68};
		float[] position ={175, 50, 229, 44};
		ajouterAnnotation(filename, docResultPath,position);
		

	}

}






























