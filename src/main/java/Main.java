import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws JSONException, IOException, ParserConfigurationException, SAXException {
        Scanner in = new Scanner(System.in);

        System.out.println("Добро пожаловать в вывод курса валют!");
        while (true) {
            System.out.println();
            System.out.println("Введите номер действия:\n" +
                    "1: Привести курс валют\n" +
                    "2: Выйти");
            System.out.println("\nВаше действие: ");
            String inputAction = in.nextLine().trim();
            if (checkInput(inputAction)) {
                if (inputAction.equals("2")) {
                    System.out.println("Приложение закрывается");
                    System.exit(0);
                }
                while (true) {
                    System.out.println("Введите необходимую дату в формате день/месяц/год:");
                    String date = in.nextLine().trim();
                    if (date.contains("/")) {
                        sendHttpRequest(date);
                        printData();
                        break;
                    } else {
                        System.out.println("Неверный формат даты! Повторите попытку...");
                    }
                }
            } else {
                System.out.println("Некорректный ввод! Повторите попытку...");
            }
        }
    }



    public static boolean checkInput(String inputAction) {
        return inputAction.equals("1") || inputAction.equals("2");
    }

    public static void sendHttpRequest(String date) throws IOException, JSONException {
        String URL = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=";
        Path filePath = Paths.get("src", "main", "resources", "data.xml");
        PrintWriter printWriter = new PrintWriter(filePath.toFile(), StandardCharsets.UTF_8);

        URL url = new URL(URL + date);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept-Charset", "UTF-8");
        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "windows-1251"));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                printWriter.write(inputLine + System.lineSeparator());
            }

            in.close();
            printWriter.close();
        }
    }

    public static void printData() throws ParserConfigurationException, IOException, SAXException {
        Path filePath = Paths.get("src", "main", "resources", "data.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        Document doc = dBuilder.parse(filePath.toFile());

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("Valute");

        for (int i = 0; i < nList.getLength(); i++) {

            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) nNode;
                Node node1 = elem.getElementsByTagName("CharCode").item(0);
                String charCode = node1.getTextContent();

                Node node2 = elem.getElementsByTagName("Name").item(0);
                String name = new String(node2.getTextContent().getBytes(), "windows-1251");

                Node node3 = elem.getElementsByTagName("Value").item(0);
                String value = new String(node3.getTextContent().getBytes(), "windows-1251");

                StringBuilder builder = new StringBuilder();
                builder.append(charCode).append(" (").append(name).append("): ").append(value);
                System.out.println(builder);
            }
        }

    }


}
