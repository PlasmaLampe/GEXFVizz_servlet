package gexfWebservice;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class SaxNode{
	String id;
	String label;
	double value;
	int place;
	
	SaxNode(){
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

public class SaxContentHandler implements ContentHandler {
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
	
	public String printContent(int upToRank){
		String output = "<table>\n\t<tr><th>name</th><th>value</th></tr>\n";
		
		for(int i = 0; i < upToRank; i++){
			output += "\t<tr><td>"+list.get(i).label+"</td><td>"+list.get(i).value+"</td></tr>\n";
		}
		output += "</table>";
		
		return output;
	}
}

class ClosenessCentralityContentHandler extends SaxContentHandler{
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
	    
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}

class BetweennessCentralityContentHandler extends SaxContentHandler{
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
	    
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}

class DegreeCentralityContentHandler extends SaxContentHandler{
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
	    
	    if (localName.equals("ranks") && parseHere == true) {
	    	list.add(tempNode);
	    }
	}
}