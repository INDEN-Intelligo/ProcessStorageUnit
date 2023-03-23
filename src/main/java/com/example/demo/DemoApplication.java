package com.example.demo;
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

@SpringBootApplication
@RestController
public class DemoApplication {
	String URL= "http://127.0.0.1:5000/Stop/";
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		//return String.format("Hello %s!", name);
		return getMachin("102");
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


}
