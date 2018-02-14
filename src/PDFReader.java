
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.apache.pdfbox.Version;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.pdfviewer.ReaderBottomPanel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageable;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.util.ExtensionFileFilter;
import org.apache.pdfbox.util.ImageIOUtil;
//import org.apache.pdfbox.pdfviewer.PageWrapper;











public class PDFReader extends JFrame implements callback {
	
  private File currentDir = new File(".");
  private JMenuItem saveAsImageMenuItem;
  private JMenuItem exitMenuItem;
  private JMenu fileMenu;
  private JMenuBar menuBar;
  private JMenuItem openMenuItem;
  private JMenuItem printMenuItem;
  private JMenu viewMenu;
  private JMenuItem nextPageItem;
  private JMenuItem previousPageItem;
  private JPanel documentPanel = new JPanel();
  private ReaderBottomPanel bottomStatusPanel = new ReaderBottomPanel();
  
  private PDDocument document = null;
  private List<PDPage> pages = null;
  
  private int currentPage = 0;
  private int numberOfPages = 0;
  private String currentFilename = null;
  
  private static final String PASSWORD = "-password";
  private static final String NONSEQ = "-nonSeq";
  private static boolean useNonSeqParser = false;
  
  private static final String VERSION = Version.getVersion();
  private static final String BASETITLE = "UAnnoteur 1.0 ";
  


	private String SRC;
	private String DST;
	private PageWrapper wrapper = null;
	private JMenu outils = new JMenu("Outils");
	private JMenu couleurs = new JMenu("Couleurs");
	private annotation annoter = null;
	private PDPage pageToshow = null;
	private callback call = this;
	private boolean saved = false;
	private JToolBar toolbar = null;
	private JToolBar toolbar2 = null;
	private List<PDAnnotation> annotationsDelete = null;
	private PDGamma couleur = null; 
	private boolean bool = true;
	List<PDAnnotation> annSP = null;
	PDDocument documentRefresh = null;
	
  public PDFReader(String SRC,String DST)
  {

    initComponents();
	this.DST = DST;
	this.SRC = SRC;
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	
  }
  






  private void initComponents()
  {
    menuBar = new JMenuBar();
    fileMenu = new JMenu();
    openMenuItem = new JMenuItem();
    saveAsImageMenuItem = new JMenuItem();
    exitMenuItem = new JMenuItem();
    printMenuItem = new JMenuItem();
    viewMenu = new JMenu();
    nextPageItem = new JMenuItem();
    previousPageItem = new JMenuItem();
    toolbar = new JToolBar(JToolBar.VERTICAL);
    toolbar2 = new JToolBar(JToolBar.VERTICAL);

    setTitle(BASETITLE);
    addWindowListener(new WindowAdapter()
    {

      public void windowClosing(WindowEvent evt)
      {
        PDFReader.this.exitApplication();
      }
      

    });
    JScrollPane documentScroller = new JScrollPane();
    documentScroller.setViewportView(documentPanel);
    

    getContentPane().add(documentScroller, "Center");
    getContentPane().add(bottomStatusPanel, "South");
    
    fileMenu.setText("File");
    openMenuItem.setText("Open");
    openMenuItem.setToolTipText("Open PDF file");
    openMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        PDFReader.this.openMenuItemActionPerformed(evt);
      }
      
    });
    fileMenu.add(openMenuItem);
    
    printMenuItem.setText("Print");
    printMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        try
        {
          if (document != null)
          {
            PDPageable pageable = new PDPageable(document);
            PrinterJob job = pageable.getPrinterJob();
            job.setPageable(pageable);
            if (job.printDialog())
            {
              job.print();
            }
          }
        }
        catch (PrinterException e)
        {
          e.printStackTrace();
        }
      }
    });
    fileMenu.add(printMenuItem);
    
    saveAsImageMenuItem.setText("Enregistrer sous");
    saveAsImageMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        if (document != null)
        {
          //PDFReader.this.saveImage();
        	saveFile();
        }
      }
    });
    fileMenu.add(saveAsImageMenuItem);
    
    exitMenuItem.setText("Exit");
    exitMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        PDFReader.this.exitApplication();
      }
      
    });
    fileMenu.add(exitMenuItem);
    
    menuBar.add(fileMenu);
    
    viewMenu.setText("View");
    nextPageItem.setText("Next page");
    nextPageItem.setAccelerator(KeyStroke.getKeyStroke('+'));
    nextPageItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        PDFReader.this.nextPage();
      }
    });
    viewMenu.add(nextPageItem);
    
    previousPageItem.setText("Previous page");
    previousPageItem.setAccelerator(KeyStroke.getKeyStroke('-'));
    previousPageItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        PDFReader.this.previousPage();
      }
    });
    viewMenu.add(previousPageItem);
    
    menuBar.add(viewMenu);
    
    
    ImageIcon img = new ImageIcon("surligneur.png");
    Image imgi = img.getImage();
    Image scaledImage = imgi.getScaledInstance(50,50,Image.SCALE_SMOOTH);
    img.setImage(scaledImage);
    outils.add(new JMenuItem("Surligneur" , img)).addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
			annoter = new annotationSurligner(SRC,DST,pageToshow,currentPage,call,couleur);
			showPage(currentPage);
			setfile();

         }
      });
    
    
    img = new ImageIcon("Squigly.png");
    imgi = img.getImage();
    scaledImage = imgi.getScaledInstance(50,50,Image.SCALE_SMOOTH);
    img.setImage(scaledImage);
    outils.add(new JMenuItem("Squigly",img)).addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
			annoter = new annotationSquigly(SRC,DST,pageToshow,currentPage,call,couleur);
			showPage(currentPage);
			setfile();
         }
      });
    
    img = new ImageIcon("Ligne.png");
    imgi = img.getImage();
    scaledImage = imgi.getScaledInstance(50,50,Image.SCALE_SMOOTH);
    img.setImage(scaledImage);
    outils.add(new JMenuItem("Ligne",img)).addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
			annoter = new annotationLigne(SRC,DST,pageToshow,currentPage,call,couleur);
			showPage(currentPage);
			setfile();
         }
      });
    /*
    img = new ImageIcon("Text.png");
    imgi = img.getImage();
    scaledImage = imgi.getScaledInstance(50,50,Image.SCALE_SMOOTH);
    img.setImage(scaledImage);
    outils.add(new JMenuItem("Text",img)).addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
			annoter = new annotationText(SRC,DST,pageToshow,currentPage,call);
			showPage(currentPage);
			setfile();
         }
      });
    */
    
    menuBar.add(outils);
    

    couleurs.add(new JMenuItem("Bleu")).addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	couleur = new PDGamma();couleur.setB(1);
        	if(annoter != null) annoter.setCouleur(couleur);
        }
     });
    couleurs.add(new JMenuItem("Rouge")).addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	couleur = new PDGamma();couleur.setR(1);
        	if(annoter != null) annoter.setCouleur(couleur);
        }
     });
    couleurs.add(new JMenuItem("Vert")).addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	couleur = new PDGamma();couleur.setG(1);
        	if(annoter != null) annoter.setCouleur(couleur);
        }
     });
    menuBar.add(couleurs);
    setJMenuBar(menuBar);
    
    JPanel pane = new JPanel();
    pane.add(toolbar,BorderLayout.EAST);
    pane.add(toolbar2,BorderLayout.WEST);
    
    
    getContentPane().add(pane, BorderLayout.EAST);
    //getContentPane().add(toolbar, BorderLayout.EAST);
    //getContentPane().add(toolbar2, BorderLayout.EAST);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((screenSize.width - 700) / 2, (screenSize.height - 600) / 2, 700, 600);
    

    
  }
  

  private void updateTitle()
  {
    setTitle(BASETITLE + ": " + currentFilename + " (" + (currentPage + 1) + "/" + numberOfPages + ")");
  }
  
  private void nextPage()
  {
    if (currentPage < numberOfPages - 1)
    {
      currentPage += 1;
      updateTitle();
      showPage(currentPage);
    }
	System.out.println("myCurrentPage : "+currentPage+"/"+numberOfPages);
  }
  
  private void previousPage()
  {
    if (currentPage > 0)
    {
      currentPage -= 1;
      updateTitle();
      showPage(currentPage);
    }
	System.out.println("myCurrentPage : "+currentPage+"/"+numberOfPages);
  }
  
  private void openMenuItemActionPerformed(ActionEvent evt)
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(currentDir);
    
    ExtensionFileFilter pdfFilter = new ExtensionFileFilter(new String[] { "PDF" }, "PDF Files");
    chooser.setFileFilter(pdfFilter);
    int result = chooser.showOpenDialog(this);
    if (result == 0)
    {
      String name = chooser.getSelectedFile().getPath();
      currentDir = new File(name).getParentFile();
      try
      {
        openPDFFile(name, "");
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  private void exitApplication()
  {
		if(saved){
		    try{
			      if (document != null){
			        document.close();
			      }
					File dst = new File(DST);
					File src = new File(SRC);
					dst.delete();
					src.delete();
			    }
			    catch (IOException io) {}
			    setVisible(false);		    
			    dispose();
		}else {
			int n = JOptionPane.showConfirmDialog(this,"Voulez vous enregistrer ?","Message",JOptionPane.YES_NO_OPTION);
			if(n == 1){
			    try{
			      if (document != null){
			        document.close();
			      }

					File dst = new File(DST);
					File src = new File(SRC);
					dst.delete();
					src.delete();
			    }
			    catch (IOException io) {}
			    setVisible(false);		    
			    dispose();
			}else if(n == 0){
				saveFile();			    
			}
		}
		
  }
  
  public void saveFile(){
		 String userDir = System.getProperty("user.home");
		 JFileChooser chooser = new JFileChooser(userDir);
		 chooser.setDialogTitle("UAnnoter");

		 chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		 

		 chooser.setAcceptAllFileFilterUsed(false);
		 int result = chooser.showOpenDialog(this);
		 if (result == 0){
			 String name = chooser.getSelectedFile().getPath();
			 String resultFile = name+"/"+new File(SRC).getName();
			 //System.out.println(resultFile);
			 
			try {
				PDDocument doc = PDDocument.loadNonSeq(new File(SRC), null);
				doc.save(resultFile);
			} catch(Exception e) {
				System.out.println("ERROR 0 : "+e.getMessage());
			}
				
		 }
		 saved = true;
  }
  


  public void openPDFFile(String filename, String password) throws Exception
  {
    if (document != null)
    {
      document.close();
      documentPanel.removeAll();
    }

	String tmpdir = System.getProperty("java.io.tmpdir");
    String[] tab = new File(filename).getName().split("\\.");
	SRC = tmpdir+"/"+tab[0]+"_result."+tab[1];
	DST = tmpdir+"/"+tab[0]+"_tmp."+tab[1];
	try {
		PDDocument doc = PDDocument.load(new File(filename), null);
		doc.save(SRC);
		doc.save(DST);
		doc.close();
	} catch(Exception e) {
		System.out.println("ERROR 0 : "+e.getMessage());
	}
    
    //File file = new File(filename);
	File file = new File(SRC);
    parseDocument(file, password);
    pages = document.getDocumentCatalog().getAllPages();
    numberOfPages = pages.size();
    currentFilename = file.getAbsolutePath();
    currentPage = 0;
    updateTitle();
    showPage(0);
  }
  
  private void showPage(int pageNumber){
	  
	saved = false;
	currentPage = pageNumber;
    try{
    	
    	pageToshow = pages.get(currentPage);
    	annotationsDelete = pageToshow.getAnnotations();
    	wrapper = new PageWrapper(this,annoter);
    	wrapper.displayPage((PDPage)pages.get(pageNumber));
    	if (documentPanel.getComponentCount() > 0){
    		documentPanel.remove(0);
    		//documentPanel.removeAll();
    	}
    	documentPanel.add(wrapper.getPanel());
    	pack();
    	refreshToolbar();
    }
    catch (IOException exception) {
    	System.out.println("ERROR : "+exception.getMessage());
      //exception.printStackTrace();
    }
    
  }
  
  
  
  public void refreshToolbar(){

	  toolbar.removeAll();
	  toolbar2.removeAll();
	  
	  try {
		  	//PDDocument doc = PDDocument.load(SRC);
		  	//PDPage pa = (PDPage) doc.getDocumentCatalog().getAllPages().get(currentPage);
			//annotationsDelete = pa.getAnnotations();
			for( int i = 0 ; i<annotationsDelete.size(); i++ ){
	            final PDAnnotation annot = (PDAnnotation)annotationsDelete.get( i );
	            String image = "";
	            if(annot.getSubtype().equals("Highlight")){
	        	    image = "surligneur.png";            	
	            }else if(annot.getSubtype().equals("Squiggly")){
	            	image = "Squigly.png";
	            }else if(annot.getSubtype().equals("Underline")){
	            	image = "Ligne.png";
	            }else
	            	image = "Inconu.png";
	            
	    	    ImageIcon img = new ImageIcon();
	    	    img.setImage(new ImageIcon(image).getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
	    	    JButton selectb = new JButton(img);
	    	    selectb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0){	
						afficherAnnotation(annot);
					}
				});
	    	    JPanel panel = new JPanel();
	    	    panel.setSize(new Dimension(30,30));
	    	    panel.add(selectb);
	
	    	    toolbar.add(panel);
	    	    
	    	    String deleteImage = "Supprimer.png";
	    	    ImageIcon imgd = new ImageIcon();
	    	    imgd.setImage(new ImageIcon(deleteImage).getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH));
	    	    JButton selec = new JButton(imgd);
	    	    selec.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0){	
						supprimerAnnotation(annot);
					}
				});
	    	    JPanel panel2 = new JPanel();
	    	    panel2.setSize(new Dimension(10,10));
	    	    panel2.add(selec);
	    	    toolbar2.add(panel2);
			}
	
	} catch (Exception e) {
		e.printStackTrace();
	}

  }
  
  private void afficherAnnotation(PDAnnotation ann){
	  wrapper.getPagePanel().afficherAnnotation(ann);
  }
  
  private void supprimerAnnotation(PDAnnotation ann){

		try {
		  	PDDocument doc = PDDocument.load(SRC);
			PDPage pa = (PDPage) doc.getDocumentCatalog().getAllPages().get(currentPage);
		
			//System.out.println(ann.getSubtype());
			//System.out.println(annotationsDelete.contains(ann));
			//System.out.println(annotationsDelete.indexOf(ann));
			annotationsDelete.remove(annotationsDelete.indexOf(ann));
			pa.setAnnotations(annotationsDelete);
			
			doc.save(new File(DST));
			doc.close();
			refresh();
			showPage(currentPage);
		
		}catch(Exception e){
			System.out.println("ERROR : "+e.getMessage());			
		}
  }
  
  
  
  private void saveImage()
  {
    try
    {
      PDPage pageToSave = (PDPage)pages.get(currentPage);
      BufferedImage pageAsImage = pageToSave.convertToImage();
      String imageFilename = currentFilename;
      if (imageFilename.toLowerCase().endsWith(".pdf"))
      {
        imageFilename = imageFilename.substring(0, imageFilename.length() - 4);
      }
      imageFilename = imageFilename + "_" + (currentPage + 1) + ".png";
      ImageIOUtil.writeImage(pageAsImage, imageFilename, 300);
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
    }
  }
  





  private void parseDocument(File file, String password)
    throws IOException
  {
    document = null;
    if (useNonSeqParser)
    {
      document = PDDocument.loadNonSeq(file, null, password);
    }
    else
    {
      document = PDDocument.load(file);
      if (document.isEncrypted())
      {
        try
        {
          document.decrypt(password);
        }
        catch (CryptographyException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  





  public ReaderBottomPanel getBottomStatusPanel()
  {
    return bottomStatusPanel;
  }
  



  private static void usage()
  {
    System.err.println("usage: java -jar pdfbox-app-" + VERSION + ".jar PDFReader [OPTIONS] <input-file>\n" + "  -password <password>      Password to decrypt the document\n" + "  -nonSeq                   Enables the new non-sequential parser\n" + "  <input-file>              The PDF document to be loaded\n");
  }
  
 
	
	@Override
	public void setfile() {
		refresh();
	}
	
	public void refresh(){		
		try {
			//System.out.println("refresh");
			documentRefresh = PDDocument.loadNonSeq(new File(DST), null);
			documentRefresh.save(SRC);
			pages = documentRefresh.getDocumentCatalog().getAllPages();
			PDPage pa = (PDPage) documentRefresh.getDocumentCatalog().getAllPages().get(currentPage);
			wrapper.getPagePanel().setPage(pa);
			wrapper.getPanel().paint(wrapper.getPanel().getGraphics());
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setRefreshToolbar(){		
		showPage(currentPage);
		setfile();
		if(bool){
			JTextArea msg = new JTextArea();
			msg.setLineWrap(true);
			msg.setWrapStyleWord(true);
			msg.setPreferredSize(new Dimension(400, 100));
			JScrollPane scrollPane = new JScrollPane(msg);
			int n = JOptionPane.showConfirmDialog(this,scrollPane,"Text annotation",JOptionPane.YES_NO_OPTION);
		
			if(n == 1){
				
			}else if(n == 0){
				System.out.println(msg.getText());
				insererTextAnnotation(msg.getText());
			}
		}
		
	}
	
	
	public void insererTextAnnotation(String s){
		try {
		  	PDDocument doc = PDDocument.load(SRC);
			PDPage pa = (PDPage) doc.getDocumentCatalog().getAllPages().get(currentPage);
		
			List<PDAnnotation> annts = pa.getAnnotations();
			PDAnnotationTextMarkup ann = (PDAnnotationTextMarkup) annts.get(annts.size()-1);
			ann.setContents(s);
			
			doc.save(new File(DST));
			doc.close();
			refresh();
		
		}catch(Exception e){
			System.out.println("ERROR : "+e.getMessage());			
		}
	}
	
	
}































