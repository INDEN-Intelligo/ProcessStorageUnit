package com.example.demo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication
@RestController
public class DemoApplication {
	String URL= "http://127.0.0.1:5000/Stop/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@GetMapping("/array")
	public ObjectNode hello(@RequestParam(value = "name", defaultValue = "World") String name) throws JsonProcessingException {
		String data = getMachin(name);
		System.out.println("before output");
		ObjectNode returnvalue = getDatas(data);
		return returnvalue;
	}

	public String getMachin(String id){
		String datas = "";
		try {
			URL url = new URL(URL + id);
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("User-Agent", "application/json");
				int responseHTTPCode = connection.getResponseCode();
				if (responseHTTPCode == HttpURLConnection.HTTP_OK) { // en cas de succès
					try {
						// Lecture du contenu
						try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
							String inputLine;
							StringBuffer response = new StringBuffer();
							// Écriture du contenu dans notre buffer
							while ((inputLine = in.readLine()) != null) {
								response.append(inputLine);
							}
							in.close();
							// Écriture de notre chaîne JSON
							datas = response.toString();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}finally {
						connection.disconnect();
					}
				} else {
					System.out.println("ServerQuestioner,GET request not worked");
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println("ServerQuestioner, URL not valid" + e.getCause());
		}

		return datas;
	}


	public ObjectNode getDatas(String data) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode jsonOutput = objectMapper.createObjectNode();
		JsonNode jsonNode = objectMapper.readTree(data);
		int i = 0;
		if (jsonNode.isObject()) {
			System.out.println("In general array");
			Iterator<Map.Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
			while (fieldsIterator.hasNext()) {
				Map.Entry<String, JsonNode> field = fieldsIterator.next();
				String fieldName = field.getKey();
				JsonNode fieldValue = field.getValue();
				System.out.println("Field name: " + fieldName);
				if (fieldName == "records") {
					System.out.println("In records array");
					System.out.println("Array values : " + fieldValue);
					JsonNode jsonArray = objectMapper.readTree(fieldValue.toString());
					//Iterator<Map.Entry<String, JsonNode>> fieldsIterator2 = fieldValue.fields();
					if (jsonArray.isArray()) {
						for (JsonNode node : jsonArray) {
							if (node.get("fields")!=null) {
								JsonNode Node = node.get("fields");
								ObjectNode jsonObject = objectMapper.createObjectNode();
								//HORAIRE ARRIVEE
								String arriveetheorique = Node.get("arriveetheorique").asText();
								//COORDONNEE
								JsonNode coordonnees = Node.get("coordonnees");
								ObjectNode position = objectMapper.createObjectNode();
								if (coordonnees.isArray()) {
									//LATITUDE
									String latitude = coordonnees.get(0).asText();
									//LONGITUDE
									String longitude = coordonnees.get(1).asText();
									position.put("latitude", latitude);
									position.put("longitude", longitude);
								}
								//IDLIGNE
								String idligne = Node.get("idligne").asText();
								// SENS
								String sens = Node.get("sens").asText();

								jsonObject.put("id", idligne);
								jsonObject.put("sens", sens);
								jsonObject.put("position", position);
								jsonObject.put("arriveetheorique", arriveetheorique);

								jsonOutput.put(String.valueOf(i), jsonObject);
								i++;
							}
						}
					}
				}
			}
		}

		return jsonOutput;
	}
}
