package XMLparser;

/**
 * 
 * adjusted from online resource: https://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
 * 
 */
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.HashMap;

public class XMLReader {
	private  String imageURL;
	private String backgroundColor;
	private String language;
	private String turtleCount;
	private String penColor;
	private String penDown;
	private String lineStyle;
	private String penThickness;


	public XMLReader(String file) {

		try {
			File fXmlFile = new File(file);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("type");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					imageURL = eElement.getElementsByTagName("imageURL").item(0).getTextContent();
					backgroundColor= eElement.getElementsByTagName("backgroundcolor").item(0).getTextContent();
					language = eElement.getElementsByTagName("language").item(0).getTextContent();
					turtleCount = eElement.getElementsByTagName("turtlecount").item(0).getTextContent();
					penColor = eElement.getElementsByTagName("pencolor").item(0).getTextContent();
					penThickness = eElement.getElementsByTagName("penthickness").item(0).getTextContent();
					penDown = eElement.getElementsByTagName("pendown").item(0).getTextContent();
					lineStyle = eElement.getElementsByTagName("linestyle").item(0).getTextContent();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getImageURL() {
		return imageURL;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public String getLanguage() {
		return language;
	}

	public String getTurtleCount() {
		return turtleCount;
	}

	public String getPenColor() {
		return penColor;
	}

	public String getPenDown() {
		return penDown;
	}

	public String getLineStyle() {
		return lineStyle;
	}
	public String getPenThickness() {
		return penThickness;
	}
}
