import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.Track;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DustMain {
    private static ApiClient apiClient = null;
    private Map<String,String> locationMap = new HashMap<String,String>();
    private String temp;

    public static HashMap<String, String> map;

    public DustMain(){

        locationMap.put("종로구", "도심권");
        locationMap.put("중구", "도심권");
        locationMap.put("용산구", "도심권");

        locationMap.put("광진구", "동북권");
        locationMap.put("성동구", "동북권");
        locationMap.put("중랑구", "동북권");
        locationMap.put("동대문구", "동북권");
        locationMap.put("성북구", "동북권");

        locationMap.put("강남구", "동남권");
        locationMap.put("서초구", "동남권");
        locationMap.put("송파구", "동남권");
        locationMap.put("강동구", "동남권");

        locationMap.put("은평구", "서북권");
        locationMap.put("서대문구", "서북권");
        locationMap.put("마포구", "서북권");

        locationMap.put("강서구", "서남권");
        locationMap.put("구로구", "서남권");
        locationMap.put("영등포구", "서남권");
        locationMap.put("동작구", "서남권");
        locationMap.put("관악구", "서남권");
    }

    public void run(String temp) {
        try {
            apiClient = new ApiClient(locationMap.get(temp), temp);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            map = apiClient.fetchData();
            System.out.println(map.get("PM10"));
        } catch (XPathExpressionException | IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        
    }
}


class ApiClient{
    private static String apiKey = "775a79634b62623139334c41534645";
    private URL apiUrl = null;
    
    ApiClient(String bigLocation, String smallLocation) throws MalformedURLException, UnsupportedEncodingException{
        if(bigLocation == null || smallLocation == null){
            return;
        }
        bigLocation =encodeEucKr(bigLocation);
        smallLocation = encodeEucKr(smallLocation);
        this.apiUrl = new URL("http://openapi.seoul.go.kr:8088/" + apiKey +"/xml/RealtimeCityAir/1/5/"+ bigLocation + "/" +smallLocation);
        System.out.println(apiUrl);
    }

    private String encodeEucKr(String str) throws UnsupportedEncodingException{
        str = URLEncoder.encode(str, "UTF-8");
        return str;
    }


    public  HashMap<String,String> fetchData() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException{
        
        String dataString = apiUrl.toString();
        DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
        Document doc = dBuilder.parse(dataString);

        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList items = (NodeList)xPath.evaluate("//row", doc, XPathConstants.NODESET);
        NodeList MSRRGN_NM = (NodeList)xPath.evaluate("//row/MSRRGN_NM", doc, XPathConstants.NODESET );
        NodeList MSRSTE_NM = (NodeList)xPath.evaluate("//row/MSRSTE_NM", doc, XPathConstants.NODESET );
        NodeList PM10 = (NodeList)xPath.evaluate("//row/PM10", doc, XPathConstants.NODESET );
        NodeList PM25 = (NodeList)xPath.evaluate("//row/PM25", doc, XPathConstants.NODESET );
        NodeList O3 = (NodeList)xPath.evaluate("//row/O3", doc, XPathConstants.NODESET );
        NodeList NO2 = (NodeList)xPath.evaluate("//row/NO2", doc, XPathConstants.NODESET );
        NodeList CO = (NodeList)xPath.evaluate("//row/CO", doc, XPathConstants.NODESET );
        NodeList SO2 = (NodeList)xPath.evaluate("//row/SO2", doc, XPathConstants.NODESET );
        NodeList IDEX_NM = (NodeList)xPath.evaluate("//row/IDEX_NM", doc, XPathConstants.NODESET );
        NodeList IDEX_MVL = (NodeList)xPath.evaluate("//row/IDEX_MVL", doc, XPathConstants.NODESET );
        NodeList APPLT_MAIN = (NodeList)xPath.evaluate("//row/APPLT_MAIN", doc, XPathConstants.NODESET );

        // InitUI.Pm10.setText("asdfasdf");
        // InitUI.Pm2_5.setText("asdfasdf");
        
        HashMap<String,String> map = new HashMap<>();

        for(int i = 0; i < items.getLength(); i++)
        {
            map.put("MSRRGN_NM",MSRRGN_NM.item(i).getTextContent());
            map.put("MSRSTE_NM",MSRSTE_NM.item(i).getTextContent());
            map.put("PM10",PM10.item(i).getTextContent());
            map.put("PM2_5",PM25.item(i).getTextContent());
            map.put("O3",O3.item(i).getTextContent());
            map.put("NO2",NO2.item(i).getTextContent());
            map.put("CO",CO.item(i).getTextContent());
            map.put("SO2",SO2.item(i).getTextContent());
            map.put("IDEX_NM",IDEX_NM.item(i).getTextContent());
            map.put("IDEX_MVL",IDEX_MVL.item(i).getTextContent());
            // map.put("APPLT_MAIN",APPLT_MAIN.item(i).getTextContent());
        }   



        return map;
    }

    
}