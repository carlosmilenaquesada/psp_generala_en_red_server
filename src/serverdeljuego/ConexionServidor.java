package serverdeljuego;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import modelos.flujo.ObjetoDato;
import vistas.ServidorJFrame;

public class ConexionServidor {

    private static ServidorJFrame servidorJFrame;
    private static ServerSocket servidor;
    private static Socket cliente1;
    private static Socket cliente2;

    public static void main(String[] args) {
        int puertoEscuchaServidor = 10000;

        try {
            servidorJFrame = new ServidorJFrame();
            servidorJFrame.setVisible(true);

            servidor = new ServerSocket(puertoEscuchaServidor);
            servidorJFrame.agregarTextoTextArea(servidor.toString());
            servidorJFrame.agregarTextoTextArea("Esperando a los jugadores...");

            // Espera al primer jugador
            cliente1 = servidor.accept();
            servidorJFrame.agregarTextoTextArea("Jugador 1 conectado.");

            // Espera al segundo cliente
            cliente2 = servidor.accept();
            servidorJFrame.agregarTextoTextArea("Jugador 2 conectado.");

            // Crea un hilo para manejar la comunicación con cada cliente
            new Thread(() -> manejarComunicacion(cliente1, cliente2)).start();
            new Thread(() -> manejarComunicacion(cliente2, cliente1)).start();
            servidorJFrame.agregarTextoTextArea("Partida iniciada.");
        } catch (IOException ex) {
            servidorJFrame.agregarTextoTextArea("Error en la comunicación.");
            servidorJFrame.agregarTextoTextArea(ex.getMessage());
        } finally {
            try {
                if (servidor != null) {
                    servidor.close();
                }
            } catch (IOException ioe) {
                servidorJFrame.agregarTextoTextArea("Problemas al cerrar el servidor.");
                servidorJFrame.agregarTextoTextArea(ioe.getMessage());
            }
        }
    }

    private static void manejarComunicacion(Socket clienteOrigen, Socket clienteDestino) {
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        try {
            //El servidor hace de intermediario entre los dos clientes, haciendo que lo que 
            //reciba de un cliente, sea enviado al otro cliente, y viceversa
            in = new ObjectInputStream(clienteOrigen.getInputStream());
            out = new ObjectOutputStream(clienteDestino.getOutputStream());
            while (true) {
                // Leer objeto del cliente de origen
                ObjetoDato objetoRecibido = (ObjetoDato) in.readObject();
                // Enviar objeto al cliente destino
                out.writeObject(objetoRecibido);
                out.flush();
            }

        } catch (Exception ex) {
            servidorJFrame.agregarTextoTextArea("Se perdió la conexión con el cliente.");
            servidorJFrame.agregarTextoTextArea(ex.getMessage());

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                clienteOrigen.close();
                clienteDestino.close();
            } catch (IOException ioe) {
                servidorJFrame.agregarTextoTextArea("No se pudo cerrar la conexión con el cliente.");
                servidorJFrame.agregarTextoTextArea(ioe.getMessage());
            }
        }
    }

    public static ServidorJFrame getServidorJFrame() {
        return servidorJFrame;
    }

    public static void setServidorJFrame(ServidorJFrame aServidorJFrame) {
        servidorJFrame = aServidorJFrame;
    }

    public static ServerSocket getServidor() {
        return servidor;
    }

    public static void setServidor(ServerSocket aServidor) {
        servidor = aServidor;
    }

    public static Socket getCliente1() {
        return cliente1;
    }

    public static void setCliente1(Socket aCliente1) {
        cliente1 = aCliente1;
    }

    public static Socket getCliente2() {
        return cliente2;
    }

    public static void setCliente2(Socket aCliente2) {
        cliente2 = aCliente2;
    }

}
