import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

//import org.apache.pdfbox.PDFReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdfviewer.PageWrapper;

public class monViewer extends PDFReader implements callback {
	
	private int myCurrentPage;
	private int myNumberOfPages;
	private int selectedTool;
	private String SRC;
	private String DST;
	private PageWrapper wrapper = null;
	
			
	public monViewer(String SRC,String DST) {
		super(SRC,DST);
		selectedTool = 0;
		myCurrentPage = 0;
		myNumberOfPages = 0;
		this.DST = DST;
		this.SRC = SRC;
		
		
		JMenuBar menu = this.getJMenuBar();
		//System.out.println("getComponentCount : "+menu.getComponentCount());
		JMenu helpMenu = (JMenu) menu.getComponent(1);
		//System.out.println("getItemCount : "+helpMenu.getItemCount());
		
		JMenuItem nextPageItem = helpMenu.getItem(0);
		//System.out.println("nextPageItem : "+nextPageItem.getText());
		ActionListener[] listners = nextPageItem.getActionListeners();
		for(ActionListener listner : listners){
			nextPageItem.removeActionListener(listner);
		}
		
		JMenuItem previousPageItem = helpMenu.getItem(1);
		//System.out.println("previousPageItem : "+previousPageItem.getText());
		listners = previousPageItem.getActionListeners();
		for(ActionListener listner : listners){
			previousPageItem.removeActionListener(listner);
		}
		
        nextPageItem.setAccelerator(KeyStroke.getKeyStroke('+'));
        nextPageItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                nextPage();
            }
        });
        
        previousPageItem.setAccelerator(KeyStroke.getKeyStroke('-'));
        previousPageItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                previousPage();
            }
        });
        
	}

	
	
	public void setCurrentFile(String file) {
		try {
			Method m = getClass().getSuperclass().getDeclaredMethod("openPDFFile", new Class<?>[]{String.class, String.class});
			m.setAccessible(true);
			m.invoke(this, file, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			Field pages = getClass().getSuperclass().getDeclaredField("pages");
			pages.setAccessible(true);
			List<PDPage> pagesList = (List<PDPage>) pages.get(this);
			myNumberOfPages = pagesList.size();			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
    private void nextPage(){
        if (myCurrentPage < myNumberOfPages-1){
        	myCurrentPage++;
            showPage(myCurrentPage);
        }
		System.out.println("myCurrentPage : "+myCurrentPage+"/"+myNumberOfPages);
    }
    
    
    private void previousPage(){
        if (myCurrentPage > 0 ){
        	myCurrentPage--;
            showPage(myCurrentPage);
        }
		System.out.println("myCurrentPage : "+myCurrentPage+"/"+myNumberOfPages);
    }
	
	
    
	public void showPage(int i){
		
		myCurrentPage = i;
		
		try {
			Field pages = getClass().getSuperclass().getDeclaredField("pages");
			pages.setAccessible(true);
			List<PDPage> pagesList = (List<PDPage>) pages.get(this);

			Field documentPanel = getClass().getSuperclass().getDeclaredField("documentPanel");
			documentPanel.setAccessible(true);
			JPanel panel = (JPanel) documentPanel.get(this);
			panel.remove(0);
			//GridLayout layout = new GridLayout(0, 1);
			//panel.setLayout(layout);
			PDPage pageToshow = pagesList.get(i);

			selectedTool = 2;
			annotation annoter = null;
			switch (selectedTool) {
			case 1:
				annoter = new annotationSurligner(SRC,DST,pageToshow,i,this,null);
				break;
			case 2:
				annoter = new annotationSquigly(SRC,DST,pageToshow,i,this,null);
				break;


			default:
				break;
			}
			
			wrapper = new PageWrapper(this,annoter);
			wrapper.displayPage(pageToshow);
			panel.add(wrapper.getPanel());
			
			
			
			
			
			
			
			pack();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void setfile() {
		refresh();
	}
	
	public void refresh(){		
		try {
			PDDocument doc = PDDocument.loadNonSeq(new File(DST), null);
			doc.save(SRC);
			
			PDPage pa = (PDPage) doc.getDocumentCatalog().getAllPages().get(myCurrentPage);
			wrapper.getPagePanel().setPage(pa);
			wrapper.getPanel().paint(wrapper.getPanel().getGraphics());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}





















































/*
public void showPage(int i){
	
	myCurrentPage = i;
	
	try {
		Field pages = getClass().getSuperclass().getDeclaredField("pages");
		pages.setAccessible(true);
		List<PDPage> pagesList = (List<PDPage>) pages.get(this);

		Field documentPanel = getClass().getSuperclass().getDeclaredField("documentPanel");
		documentPanel.setAccessible(true);
		JPanel panel = (JPanel) documentPanel.get(this);
		panel.remove(0);
		//GridLayout layout = new GridLayout(0, 1);
		//panel.setLayout(layout);
		PDPage pageToshow = pagesList.get(i);

		
		
		PageWrapper wrapper = new PageWrapper(this);
		
		wrapper.displayPage(pageToshow);
		panel.add(wrapper.getPanel());
		
		pack();

	} catch(Exception e) {
		e.printStackTrace();
	}
}

	*/


/*


MouseListener listener = new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					System.out.println("monViewer");
					
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
					System.out.println("monViewer");
					
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
					System.out.println("monViewer");
					
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
					System.out.println("monViewer");
					
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
					System.out.println("mouseClicked");
					
				}
			};
			wrapper.getPanel().addMouseListener(listener );
			

*/










/*
PDFPagePanel pagePanel = null;
JPanel pageWrapper = new JPanel();
try {
	pagePanel = new PDFPagePanel();
} catch (IOException e) {
	System.out.println("ERROR 1 : "+e.getMessage());
}
pageWrapper.setLayout( null );
pageWrapper.add( pagePanel );
pagePanel.setLocation( 20, 20 );
pageWrapper.setBorder( javax.swing.border.LineBorder.createBlackLineBorder() );
//pagePanel.addMouseMotionListener(this);      #############
pagePanel.setPage( pageToshow );
pagePanel.setPreferredSize( pagePanel.getSize() );
Dimension d = pagePanel.getSize();
d.width+=(20*2);
d.height+=(20*2);
pageWrapper.setPreferredSize( d );
pageWrapper.validate();
panel.add(pageWrapper);
*/
















