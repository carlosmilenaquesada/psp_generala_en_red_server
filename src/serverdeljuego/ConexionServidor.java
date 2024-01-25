package serverdeljuego;

import modelos.flujo.ObjetoDato;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionServidor {

    public static void main(String[] args) {
        int puertoEscuchaServidor = 10000;
        try {
            ServerSocket servidor = new ServerSocket(puertoEscuchaServidor);
            System.out.println("Esperando conexiones...");

            // Espera al primer cliente
            Socket cliente1 = servidor.accept();
            System.out.println("Cliente 1 conectado.");

            // Espera al segundo cliente
            Socket cliente2 = servidor.accept();
            System.out.println("Cliente 2 conectado.");

            // Crea un hilo para manejar la comunicación con cada cliente
            new Thread(() -> manejarComunicacion(cliente1, cliente2)).start();
            new Thread(() -> manejarComunicacion(cliente2, cliente1)).start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void manejarComunicacion(Socket clienteOrigen, Socket clienteDestino) {
        try {
            // Streams de objetos para el cliente de origen
            ObjectInputStream in = new ObjectInputStream(clienteOrigen.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clienteDestino.getOutputStream());

            while (true) {
                try {
                    // Leer objeto del cliente de origen
                    ObjetoDato objetoRecibido = (ObjetoDato) in.readObject();
                    System.out.println("hobjeto");
                    // Enviar objeto al cliente destino
                    out.writeObject(objetoRecibido);
                    out.flush();
                } catch (SocketException se) {
                    // La conexión se cerró inesperadamente, salir del bucle
                    System.out.println("Conexión con cliente cerrada.");
                    break;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                // Cerrar sockets y streams al salir
                clienteOrigen.close();
                clienteDestino.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
