package com.raphaelprojetos.Sentinel.socket;

import java.io.IOException;
import java.net.Socket;



public class SocketClient {

    private Socket socket;
    private SocketServer server;

    public void connect() {

        final int MAXIMUM_RECONECTION_ATTEMPTS = 5;
        int reconection = 0;

        while (MAXIMUM_RECONECTION_ATTEMPTS > reconection && socket == null) {

            try {

                socket = new Socket("26.92.48.121", 8080);
                System.out.println("Conectado");
                reconection++;

            } catch (IOException e) {

                System.err.println("Erro na conexão");
                e.printStackTrace();
                reconection++;
                socket = null;
            }

        }
    }
}