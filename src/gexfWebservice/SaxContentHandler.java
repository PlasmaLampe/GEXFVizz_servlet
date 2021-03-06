package gexfWebservice;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * This class represents help nodes that are needed
 * by the sax parser to parse the "all metrics" xml string
 * 
 * @author joerg
 *
 */
class SaxNode{
	private String id;
	private String label;
	private double value;
	private double svalue;
	private int place;
	
	SaxNode(){
	}

	/**
	 * @return the svalue
	 */
	public double getSvalue() {
		return svalue;
	}

	/**
	 * @param svalue the svalue to set
	 */
	public void setSvalue(double svalue) {
		this.svalue = svalue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}
	
}

/**
 * This class is needed to extract the needed information from
 * the "allmetrics" XML file
 * 
 * @author joerg
 *
 */
public class SaxContentHandler implements ContentHandler {
	protected String snatype;
	protected String filename;
	protected ArrayList<SaxNode> list = new ArrayList<SaxNode>();
	protected SaxNode tempNode;
	protected String currentValue;
	protected boolean parseHere = false;
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		currentValue = new String(ch, start, length);
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endPrefixMapping(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDocumentLocator(Locator arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void skippedEntity(String arg0) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public void startElement(String uri, String localName, String qName,
		      Attributes atts) throws SAXException {
		

	}

	@Override
	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This methods rounds a given double value 
	 * @param value
	 * @return rounded double value
	 */
	protected double roundTwoD(double value) {
		double result = value * 100;
		result = Math.round(result);
		result = result / 100;
		return result;
	}
	
	/**
	 * This method uses the sax nodes to print a html table 
	 * 
	 * @param upToRank the number of nodes that should be printed within the table
	 * @return a string that contains the html code
	 */
	public String printContent(int upToRank){
		//String id = "" + Math.round((Math.random() * 100) * (Math.random() * 100)); id=\"sortabletable"+id+
		String output = "<table class=\"sortable\">\n\t<tr><th>name</th><th>standardized value</th><th>value</th></tr>\n";
		
		for(int i = 0; i < upToRank; i++){
			double roundedValue = roundTwoD(list.get(i).getValue());
			String labellink = "<a href=\""+ Settings.TomcatURLToServlet +"id=" + filename + "&item=" +
					"" + list.get(i).getId() +"&metric="+ snatype +"\">"+list.get(i).getLabel()+"</a>";
			output += "\t<tr><td>"+ labellink +"</td>" +
					"<td>"+list.get(i).getSvalue()+"</td><td>"+ roundedValue +"</td></tr>\n";
		}
		output += "</table>";
		
		return output;
	}
}

/**
 *  This class is needed to extract the "closeness centrality" information from
 * the "allmetrics" XML file
 * 
 * @author joerg
 *
 */
class ClosenessCentralityContentHandler extends SaxContentHandler{
	
	public ClosenessCentralityContentHandler(){
		snatype = "cc";
	}
	
	/* startElement */
	public void startElement(String uri, String localName, String qName,
		      Attributes atts) throws SAXException {
		
	    if (localName.equals("closenessCentrality")) {
	    	parseHere = true;
	    }
	    
	    if(localName.equals("ranks") && parseHere == true){  	
	    	tempNode = new SaxNode();
	    }
	}
	
	/* endElement */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
	    if (localName.equals("closenessCentrality")) {
	    	parseHere = false;
	    }
	    if (localName.equals("filename")) {
	    	String clean = currentValue.substring(currentValue.lastIndexOf("/")+1,currentValue.lastIndexOf("."));
	    	filename = clean;
	    }
	    if (localName.equals("id") && parseHere == true) {
	    	tempNode.setId(currentValue);
	    }
	    if (localName.equals("place") && parseHere == true) {
	    	tempNode.setPlace(Integer.parseInt(currentValue));
	    }
	    if (localName.equals("label") && parseHere == true) {
	    	tempNode.setLabel(currentValue);
	    }
	    if (localName.equals("value") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setValue(myDouble);
	    }
	    if (localName.equals("svalue") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setSvalue(myDouble);
	    }
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}

/**
 *  This class is needed to extract the "betweenness centrality" information from
 * the "allmetrics" XML file
 * 
 * @author joerg
 *
 */
class BetweennessCentralityContentHandler extends SaxContentHandler{
	
	public BetweennessCentralityContentHandler(){
		snatype = "bc";
	}
	
	/* startElement */
	public void startElement(String uri, String localName, String qName,
		      Attributes atts) throws SAXException {
		
	    if (localName.equals("betweennessCentrality")) {
	    	parseHere = true;
	    }
	    if(localName.equals("ranks") && parseHere == true){
	    	tempNode = new SaxNode();
	    }
	}
	
	/* endElement */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
	    if (localName.equals("betweennessCentrality")) {
	    	parseHere = false;
	    }
	    if (localName.equals("filename")) {
	    	String clean = currentValue.substring(currentValue.lastIndexOf("/")+1,currentValue.lastIndexOf("."));
	    	filename = clean;
	    }
	    if (localName.equals("id") && parseHere == true) {
	    	tempNode.setId(currentValue);
	    }
	    if (localName.equals("place") && parseHere == true) {
	    	tempNode.setPlace(Integer.parseInt(currentValue));
	    }
	    if (localName.equals("label") && parseHere == true) {
	    	tempNode.setLabel(currentValue);
	    }
	    if (localName.equals("value") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setValue(myDouble);
	    }
	    if (localName.equals("svalue") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setSvalue(myDouble);
	    }
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}

/**
 * This class is needed to extract the "degree centrality" information from
 * the "allmetrics" XML file
 * 
 * @author joerg
 *
 */
class DegreeCentralityContentHandler extends SaxContentHandler{
	
	public DegreeCentralityContentHandler() {
		snatype = "dc";
	}
	
	/* startElement */
	public void startElement(String uri, String localName, String qName,
		      Attributes atts) throws SAXException {
		
	    if (localName.equals("degreeCentrality")) {
	    	parseHere = true;
	    }
	    
	    if(localName.equals("ranks") && parseHere == true){
	    	tempNode = new SaxNode();
	    }
	}
	
	/* endElement */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
	    if (localName.equals("degreeCentrality")) {
	    	parseHere = false;
	    }
	    if (localName.equals("filename")) {
	    	String clean = currentValue.substring(currentValue.lastIndexOf("/")+1,currentValue.lastIndexOf("."));
	    	filename = clean;
	    }
	    if (localName.equals("id") && parseHere == true) {
	    	tempNode.setId(currentValue);
	    }
	    if (localName.equals("place") && parseHere == true) {
	    	tempNode.setPlace(Integer.parseInt(currentValue));
	    }
	    if (localName.equals("label") && parseHere == true) {
	    	tempNode.setLabel(currentValue);
	    }
	    if (localName.equals("value") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setValue(myDouble);
	    }  
	    if (localName.equals("svalue") && parseHere == true) {
	    	double myDouble = Double.parseDouble(currentValue);
	    	tempNode.setSvalue(myDouble);
	    }
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}