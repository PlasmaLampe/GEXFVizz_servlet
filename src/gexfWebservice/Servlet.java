package gexfWebservice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private final String APACHE_PATH = "/var/www/";
	
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
     * This method reads the content of a given file
     * @param path the path to the file
     * @return the content of the file as a String
     */
    private String getContent(String path){
    	File tempfile = new File(path);
    	String contentOfFile ="";

    	try {
    		BufferedReader input =  new BufferedReader(new FileReader(tempfile));
    		try {
    			String line = null; 
    			while (( line = input.readLine()) != null){
    				contentOfFile += line;
    			}
    		}finally {
    			input.close();
    		}
    	}catch (Exception e){
    		e.printStackTrace();
    	}

    	return contentOfFile.toString();
    }
    
    /**
     * The method hashes the content of the given file and 
     * returns the SHA256 hash as a return value
     * @param path of the file that should be checked
     * @return the SHA256 hash
     */
    private String hashCodeSHA256(String path){
    	/* note: the actual hashing code was taken from
    	 * http://www.mkyong.com/java/java-sha-hashing-example/
    	 */
    	String content = getContent(path);
    	 
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        md.update(content.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        //System.out.println("Hex format : " + sb.toString());
    	
    	return sb.toString();
    }
    
    /**
     * The methods checks if a given file exists. 
     * If the file does not exist, the method creates it and
     * inserts the content of the given file
     *  
     * @param path the file that has to be checked
     * @param content if the file has to be created, use
     * this content
     */
    private void doesFileExist(String path, String content){
    	File f = new File(path);
    	if(f.exists()) {
    		/* do nothing, the file has already been hashed and saved */ 
    	}else{
    		createFile(path,getContent(content));
    	}
    }
    
    /**
     * The method creates the specified file with the given content
     * @param path to the file, that is going to be created
     * @param content the content of the file
     */
    private void createFile(String path, String content){
		try{
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
    }
    
    /** 
     * this method returns a html table with the top $rank values for the closeness centrality metric
     * 
     * @param xml the whole XML metrics content, which is provided by server.getMetrics(...)
     * @param rank
     * @return html table
     */
    public String extractCC(String xml, int rank){
    	/* get values */
    	ClosenessCentralityContentHandler ccc = new ClosenessCentralityContentHandler();
    	
    	XMLReader xmlReader = null;
    	
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    InputSource inputSource = new InputSource(new StringReader(xml));
	    xmlReader.setContentHandler(ccc);
	    
	    try {
			xmlReader.parse(inputSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
    	return ccc.printContent(rank);
    }
    
    /** 
     * this method returns a html table with the top $rank values for the betweenness centrality metric
     * 
     * @param xml the whole XML metrics content, which is provided by server.getMetrics(...)
     * @param rank
     * @return html table
     */
    public String extractBC(String xml, int rank){
    	/* get values */
    	BetweennessCentralityContentHandler bcc = new BetweennessCentralityContentHandler();
    	XMLReader xmlReader = null;
    	
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    InputSource inputSource = new InputSource(new StringReader(xml));
	    xmlReader.setContentHandler(bcc);
	    
	    try {
			xmlReader.parse(inputSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
    	return bcc.printContent(rank);
    }
    
    /** 
     * this method returns a html table with the top $rank values for degree centrality metric
     * 
     * @param xml the whole XML metrics content, which is provided by server.getMetrics(...)
     * @param rank
     * @return html table
     */
    public String extractDC(String xml, int rank){
    	
    	return "TO DO";
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServerInterface server = null;
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		} 
		
		try {
			Registry reg = LocateRegistry.getRegistry();
			if(reg != null){
				server = (ServerInterface) reg.lookup("Server");
			}else{
				System.out.println("Error: cant locate registry ...");
				return;
			}
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* init response and parse parameter */
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String filename = APACHE_PATH + request.getParameter("url");
		
		int rank = -1;
		if(request.getParameter("rank") != null)
			rank	= Integer.parseInt(request.getParameter("rank"));

		String metric	= request.getParameter("metric");
		
		int getNodesAndEdges = -1;
		if(request.getParameter("getnodesandedges") != null)
			getNodesAndEdges	= Integer.parseInt(request.getParameter("getnodesandedges"));
		
		int getDensity= -1;
		if(request.getParameter("getdensity") != null)
			getDensity	= Integer.parseInt(request.getParameter("getdensity"));
		
		int getSHA= -1;
		if(request.getParameter("getsha") != null)
			getSHA	= Integer.parseInt(request.getParameter("getsha"));
		
		/* execute some stuff, if it is a correct request */
		if(request.getParameter("url") != null && request.getParameter("id") == null){
			String hashName = hashCodeSHA256(filename);
			String hashPath	= APACHE_PATH + "hash/"+hashName+".gexf";
			doesFileExist(hashPath, filename); 
			
			if(rank != -1 && metric != null){
				String result = server.getMetrics(hashPath);	// ask the server for the needed XML code
				
				/* there was a metric and a rank in the request */
				switch(metric){
				case "all":
					out.println(result); // return the whole XML-file
					break;
				case "cc":
					out.println(extractCC(result, rank));
					break;
				case "bc":
					out.println(extractBC(result, rank));
					break;
				case "dc":
					out.println(extractDC(result, rank));
					break;
				}
			}else if(getDensity == -1 && getNodesAndEdges != -1){
				// get the #nodes and the #edges of the graph
				String nodesAndEdges = server.getNodesAndEdges(hashPath);
				out.println(nodesAndEdges);
			}else if(getDensity != -1 && getNodesAndEdges == -1){
				// get the density of the graph
				Double density = server.getDensity(hashPath);
				out.println(density);
			}else if(getDensity == -1 && getNodesAndEdges == -1 && getSHA != -1){
				// get the SHA hash of the graph-file
				out.println(hashName);
			}
		}else if(request.getParameter("url") == null && request.getParameter("id") != null){
			String hashPath	= APACHE_PATH + "hash/"+request.getParameter("id")+".gexf"; 
			
			if(rank != -1 && metric != null){
				String result = server.getMetrics(hashPath);	// ask the server for the needed XML code
				
				/* there was a metric and a rank in the request */
				switch(metric){
				case "all":
					out.println(result); // return the whole XML-file
					break;
				case "cc":
					out.println(extractCC(result, rank));
					break;
				case "bc":
					out.println(extractBC(result, rank));
					break;
				case "dc":
					out.println(extractDC(result, rank));
					break;
				}
			}else if(getDensity == -1 && getNodesAndEdges != -1){
				// get the #nodes and the #edges of the graph
				String nodesAndEdges = server.getNodesAndEdges(hashPath);
				out.println(nodesAndEdges);
			}else if(getDensity != -1 && getNodesAndEdges == -1){
				// get the density of the graph
				Double density = server.getDensity(hashPath);
				out.println(density);
			}else if(getDensity == -1 && getNodesAndEdges == -1 && getSHA != -1){
				// get the SHA hash of the graph-file
				out.println(request.getParameter("id"));
			}
		}else{
			response.setContentType("text/html");
			out.println("<html><head></head><body><h2>GEXFVizz error:</h2> please specify an url...<br>" +
					"try: /GEXFServer/Servlet?url=data/small.gexf&metric=cc&rank=3 <br>" +
					"or use some of these variables:getnodesandedges, getdensity, getsha..." +
					"</body></html>");
		}
	}
}
