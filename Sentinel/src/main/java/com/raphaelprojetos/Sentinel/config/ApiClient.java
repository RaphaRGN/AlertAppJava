package com.raphaelprojetos.Sentinel.config;


import java.net.URL;

//Todo: TERMINAR A CONFIG D HTTPCONECT
public class ApiClient {

    private static final String URL_BASE = "http://26.92.48.121";
    private static final int PORT = 8080;


       public static String get(String PORT) throws Exception{
           URL url = new URL(URL_BASE + PORT);

            return "TESTE";
    }




}
