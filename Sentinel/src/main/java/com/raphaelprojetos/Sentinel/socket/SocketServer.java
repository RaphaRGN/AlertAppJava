package com.raphaelprojetos.Sentinel.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    ServerSocket server;

    public void startServer() {
        try {
            server = new ServerSocket(8080);
            System.out.println("Servidor iniciado na porta 8080");
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor:");
            e.printStackTrace();
        }
    }
}



