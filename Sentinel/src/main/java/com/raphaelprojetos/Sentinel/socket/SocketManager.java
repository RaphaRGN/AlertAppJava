package com.raphaelprojetos.Sentinel.socket;

import java.io.IOException;
import java.net.Socket;

// TODO: Criar a lógica do socket
public class SocketManager {

    private Socket socket;

    public Socket conect() {

        final int MAXIMUM_RECONECTION_ATTEMPTS = 5;
        int reconection = 0;

        while (MAXIMUM_RECONECTION_ATTEMPTS > reconection && socket == null) {

            try {

                socket = new Socket("127.0.0.1", 8080);
                System.out.println("Conectado");
                reconection++;

            } catch (IOException e) {

                System.err.println("Erro na conexão");
                e.printStackTrace();
                reconection++;
                socket = null;
            }

        }
        return socket;
    }
}