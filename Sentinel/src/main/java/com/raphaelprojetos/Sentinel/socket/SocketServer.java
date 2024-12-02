package com.raphaelprojetos.Sentinel.socket;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


@Component
public class SocketServer {

    ServerSocket server;
    private final int port = 8080;

    public void startServer() {
        try {
            server = new ServerSocket(port);
            System.out.println("Servidor iniciado na porta: " + port);
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: ");
            e.printStackTrace();
        }
    }
}