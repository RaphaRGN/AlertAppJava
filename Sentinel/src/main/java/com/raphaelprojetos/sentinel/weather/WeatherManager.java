package com.raphaelprojetos.sentinel.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;

@Service
public class WeatherManager {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://wttr.in/{city}?format=j1";

    public String getactualWeather(String city){

        try{

            return restTemplate.getForObject(API_URL, String.class, city);
        }
        catch (Exception e){

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro obter informações");
            return "Erro ao obter informações";
        }
    }

    public String parseWeather(String jsonResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            String temperature = root.path("current_condition").get(0).path("temp_C").asText();
            String description = root.path("current_condition").get(0).path("weatherDesc").get(0).path("value").asText();
            return "Temperatura: " + temperature + "°C, Condição: " + description;
        } catch (Exception e) {
            return "Erro ao processar dados do clima.";
        }
    }



}
