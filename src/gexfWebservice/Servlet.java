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
	private static String lastFileContent = "";
	private static String lastHashValue = "";
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		} 
		
		initParameter();
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
    public String hashCodeSHA256(String path){
    	String content = getContent(path);
    	
    	if(content.equals(lastFileContent)){ // shortcut, maybe we know the output already ;-)
    		return lastHashValue;
    	}else{
    		/* else -> hash the content
    		 * note: the actual hashing code was taken from
    		 * http://www.mkyong.com/java/java-sha-hashing-example/
    		 */
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

    		// save shortcut values
    		lastFileContent = content;
    		lastHashValue = sb.toString();

    		// return hash value
    		return sb.toString();
    	}
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
     * @param chandler the needed content handler to parse the XML file
     * @return html table
     */
    public String extractMetric(String xml, int rank, SaxContentHandler chandler){
    	XMLReader xmlReader = null;
    	
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    InputSource inputSource = new InputSource(new StringReader(xml));
	    xmlReader.setContentHandler(chandler);
	    try {
			xmlReader.parse(inputSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
    	return chandler.printContent(rank);
    }
    
	/**
	 * This method is called every time a user send a request via HTTP 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServerInterface server = null;
		
		try {
			Registry reg = LocateRegistry.getRegistry();
			if(reg != null){
				server = (ServerInterface) reg.lookup("Server");
			}else{
				System.out.println("Error: cant locate registry ...");
				return;
			}
		} catch (NotBoundException e) {
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
		
		boolean circos = false;
		if(request.getParameter("circos") != null)
			circos	= Boolean.parseBoolean(request.getParameter("circos"));
		
		boolean project = false;
		if(request.getParameter("getproject") != null)
			project	= Boolean.parseBoolean(request.getParameter("getproject"));
		
		boolean getBCEdges = false;
		if(request.getParameter("bcedges") != null)
			getBCEdges	= Boolean.parseBoolean(request.getParameter("bcedges"));
		
		String eventseriesid = request.getParameter("eventseriesid");
		String graphtype	= request.getParameter("graphtype");
		
		String item	= request.getParameter("item");
		
		boolean getNodesAndEdges = false;
		if(request.getParameter("getnodesandedges") != null)
			getNodesAndEdges	= Boolean.parseBoolean(request.getParameter("getnodesandedges"));
		
		boolean preview = false;
		if(request.getParameter("preview") != null)
			preview	= Boolean.parseBoolean(request.getParameter("preview"));
		
		boolean getDensity = false;
		if(request.getParameter("getdensity") != null)
			getDensity	= Boolean.parseBoolean(request.getParameter("getdensity"));
		
		boolean getSHA = false;
		if(request.getParameter("getsha") != null)
			getSHA	= Boolean.parseBoolean(request.getParameter("getsha"));
		
		String hashPath = "";
		String hashName = "";
		
		/* execute a method, if it is a correct request */
		/* user is going to create a circos file */
		if(request.getParameter("url") != null && metric != null && rank != -1 && circos != false){
			
			// now, let's start
			String filepath = server.getCircosPath(filename, metric, rank, preview);
			out.println(filepath);
		}
		
		/* user is going to create a gexf file */
		else if(eventseriesid != null && graphtype != null && circos == false){
			// load the other parameter 
			String syear	= request.getParameter("syear");
			String eyear	= request.getParameter("eyear");
			
			// now, let's start
			String filepath = server.getGraphPath(graphtype, eventseriesid, syear, eyear);
			out.println(filepath);
		}
		/* user opened a file */
		else if(request.getParameter("url") != null && request.getParameter("id") == null){		
			hashName = hashCodeSHA256(filename);
			hashPath = APACHE_PATH + "hash/"+hashName+".gexf";
			doesFileExist(hashPath, filename); 
		}else{
			hashPath = APACHE_PATH + "hash/"+request.getParameter("id")+".gexf"; 
		}
		
		if(project != false){
			String result = server.getPathToProject(hashPath);
			out.println(result);
		}
		if(metric != null && item != null){
			response.setContentType("text/html");
			String result = server.getLocalCircos(hashPath, item, metric);
			String html = "<html><head></head><body><img src=\""+Settings.ServerURL+result+
					"\" width=\"800\" height=\"800\"><br><h4>Download the circos configuration files:</h4>" +
					"<a href=\""+ Settings.ServerURL +"circos/data/"+ request.getParameter("id") +".zip\">download</a></body></html>";
			out.println(html);
		}
		if(eventseriesid != null && getBCEdges && rank != -1){
			String syear	= request.getParameter("syear");
			String eyear	= request.getParameter("eyear");
			String result = server.getBCEdges(eventseriesid, syear, eyear, rank);	// ask the server for the needed XML code
			out.println(result);
		}
		else if(rank != -1 && metric != null && circos == false && 
				(request.getParameter("url") != null || request.getParameter("id") != null)){
			String result = server.getMetrics(hashPath);	// ask the server for the needed XML code

			/* there was a metric and a rank in the request */
			// check max values first
			String[] nodesAndEdges = server.getNodesAndEdges(hashPath).split("#");
			int maxRank = Integer.parseInt(nodesAndEdges[0]);
			if(maxRank < rank){
				rank = maxRank;
			}
			// now, it's safe to proceed ...
			switch(metric){
			case "all":
				out.println(result); // return the whole XML-file
				break;
			case "cc":
				ClosenessCentralityContentHandler ccc = new ClosenessCentralityContentHandler();
				out.println(extractMetric(result, rank, ccc));
				break;
			case "bc":
				BetweennessCentralityContentHandler bcc = new BetweennessCentralityContentHandler();
				out.println(extractMetric(result, rank, bcc));
				break;
			case "dc":
				DegreeCentralityContentHandler dcc = new DegreeCentralityContentHandler();
				out.println(extractMetric(result, rank, dcc));
				break;
			}
		}else if(getDensity == false && getNodesAndEdges == true && 
				(request.getParameter("url") != null || request.getParameter("id") != null)){
			// get the #nodes and the #edges of the graph
			String nodesAndEdges = server.getNodesAndEdges(hashPath);
			out.println(nodesAndEdges);
		}else if(getDensity == true && getNodesAndEdges == false && 
				(request.getParameter("url") != null || request.getParameter("id") != null)){
			// get the density of the graph
			Double density = server.getDensity(hashPath);
			out.println(density);
		}else if(getDensity == false && getNodesAndEdges == false && getSHA == true && 
				(request.getParameter("url") != null || request.getParameter("id") != null)){
			// get the SHA hash of the graph-file
			out.println(hashName);
		}else if(eventseriesid == null && circos == false && project == false && item == null){
			// ERROR, this was not a valid request ...
			response.setContentType("text/html");
			out.println("<html><head></head><body><h2>GEXFVizz error:</h2> please specify an url...<br>" +
					"try: /GEXFServer/Servlet?url=data/small.gexf&metric=cc&rank=3 <br>" +
					"or use some of these variables:getnodesandedges, getdensity, getsha..." +
					"</body></html>");
		}
		
		/* cleanup */
		out.close();
	}
	
	/**
	 * This method initializes the global methods of the
	 * application with the help of the settings.txt file
	 */
	private void initParameter(){
    	File tempfile = new File(Settings.CFG_FILE);

    	try {
    		BufferedReader input =  new BufferedReader(new FileReader(tempfile));
    		try {
    			String line = null; 
    			while (( line = input.readLine()) != null){
    				String token[] =  line.split("=");
    				
    				switch(token[0].replaceAll("\t", "").trim()){
    				case "TomcatURLToServlet":
    					Settings.TomcatURLToServlet = token[1];
    					break;
    				case "ServerURL":
    					Settings.ServerURL = token[1];
    					break;		
    				}
    			}
    		}finally {
    			input.close();
    		}
    	}catch (Exception e){
    		e.printStackTrace();
    	}
	}
}
