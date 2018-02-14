import java.io.File;

import javax.swing.JFrame;

import org.apache.pdfbox.pdmodel.PDDocument;


public class pdfAnnot {


	public static void main(String[] args) {
		System.out.println("monViewer");
		
		String filename = "/home/etudiant/Bureau/pdfBox/test/tst/test222.pdf";
		String tmpdir = System.getProperty("java.io.tmpdir");		
		String[] tab = new File(filename).getName().split("\\.");
		String SRC = tmpdir+"/"+tab[0]+"_result."+tab[1];
		String DST = tmpdir+"/"+tab[0]+"_tmp."+tab[1];
		
		try {
			PDDocument doc = PDDocument.loadNonSeq(new File(filename), null);
			doc.save(SRC);
			doc.save(DST);
			doc.close();
		} catch(Exception e) {
			System.out.println("ERROR 0 : "+e.getMessage());
		}
		
		//monViewer reader = new monViewer(SRC,DST);
		PDFReader reader = new PDFReader(SRC,DST);
		/*
		try {
			reader.openPDFFile(filename, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		//reader.setCurrentFile(filename);
		//reader.showPage(0);
		reader.setVisible(true);
		

		
		
	}

}


























