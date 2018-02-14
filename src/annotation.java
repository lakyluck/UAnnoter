import java.awt.event.MouseEvent;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;


public class annotation {
	

	protected PDPage page;
	protected int pageToAnnote;
	protected String SRC;
	protected String DST;
	protected callback callback;
	protected PDGamma couleur = null;
	
	public annotation(String SRC, String DST,PDPage page,int pageToAnnote, callback callback,PDGamma couleur) {
		this.couleur = couleur;
		this.page = page;
		this.pageToAnnote = pageToAnnote;
		this.SRC = SRC;
		this.DST = DST;
		this.callback = callback;
	}
	
	
	public void appliquer(MouseEvent arg0){};
	public void commencer(MouseEvent arg0){};
	public void arreter(){}


	public void setCouleur(PDGamma couleur) {
		this.couleur = couleur;
	};
	
}




/*

	
	
	public annotation(String SRC, String DST,PDPage page,int pageToAnnote, callback callback) {
		this.page = page;
		this.pageToAnnote = pageToAnnote;
		SRC = SRC;
		DST = DST;
		this.callback = callback;
	}

*/