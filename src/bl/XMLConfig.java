package bl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.spark.SparkConf;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfig {
	//note: need to check if there is a problem to use this static class 
	//when multiple worker are active
	
	private static Document docConfig;
	
	public static SparkConf getSparkConfigFromFile(String confFilePath){
		SparkConf conf = new SparkConf();
		
		docConfig = generateDocFromFile(getConfigFilePath(confFilePath));
		
		Element tmpElement = (Element)docConfig.getElementsByTagName("Application").item(0);
		
		conf.setAppName(tmpElement.getElementsByTagName("spark.app.name").item(0).getTextContent());
		conf.setMaster(tmpElement.getElementsByTagName("spark.master").item(0).getTextContent());
		
		tmpElement = (Element)docConfig.getElementsByTagName("UI").item(0);
		conf.set("spark.eventLog.enabled",
				tmpElement.getElementsByTagName("spark.eventLog.enabled").item(0).getTextContent());
		conf.set("spark.eventLog.dir",
				tmpElement.getElementsByTagName("spark.eventLog.dir").item(0).getTextContent());
		
		return conf;
	}
	
	public static ArrayList<String> getDatasetPaths(String confFilePath){
		ArrayList<String> paths = new ArrayList<>();
		
		docConfig = generateDocFromFile(getConfigFilePath(confFilePath));
		NodeList nList = docConfig.getElementsByTagName("Dataset");
		for (int i = 0; i < nList.getLength(); i++) {
			paths.add(nList.item(i).getAttributes().getNamedItem("path").getNodeValue());
		}
	
		return paths;
	}
	
	public static HashMap<String,String> getIdentifierConfig(String confFilePath, String identifierClassName){
		HashMap<String,String> hMap = new HashMap<>();
		
		docConfig = generateDocFromFile(getConfigFilePath(confFilePath));
		
		Element tmpElement = (Element)docConfig.getElementsByTagName(identifierClassName).item(0);
		NodeList nList = tmpElement.getChildNodes();
		for (int i = 0; i < nList.getLength(); i++) {
			Node n = nList.item(i);
			hMap.put(n.getNodeName(),n.getTextContent());
		}
		
		return hMap;
	}
	
	public static HashMap<String,String> getAttackdetectorConfig(String confFilePath){
		HashMap<String,String> hMap = new HashMap<>();
		
		docConfig = generateDocFromFile(getConfigFilePath(confFilePath));
		
		Element tmpElement = (Element)docConfig.getElementsByTagName("ParameterConfig").item(0);
		
		hMap.put("PACKET_RATE_THRESHOLD", tmpElement.getElementsByTagName("PACKET_RATE_THRESHOLD").item(0).getTextContent());
		hMap.put("LOAD_THRESHOLD", tmpElement.getElementsByTagName("LOAD_THRESHOLD").item(0).getTextContent());
		hMap.put("MEASURING_WINDOW", tmpElement.getElementsByTagName("MEASURING_WINDOW").item(0).getTextContent());
		hMap.put("ATTACK_MIN_LENGTH", tmpElement.getElementsByTagName("ATTACK_MIN_LENGTH").item(0).getTextContent());
		return hMap;
	}
	
	private static Document generateDocFromFile(InputStream filePath){
		Document doc;
		//create objects for parsing the XML
		try {
		
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filePath);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static InputStream getConfigFilePath(String localPath){
		return XMLConfig.class.getResourceAsStream(localPath);
	}

}
