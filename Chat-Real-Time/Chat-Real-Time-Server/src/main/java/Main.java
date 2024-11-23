import java.io.*;
import java.net.*;
import java.util.*;

/*
* [25, 44, -105, 80]Oviedo Deberia ser mi IP
* Expresiones Regulares o Regex
*
* [25, 44, -105, 80] -> Oviedo Deberia ser mi IP
*
* ->> Deberia ejecutarse en el cliente
*/
public class Main {
    private static final int Puerto = 8000; // Puerto del servidor
    private static final Set<PrintWriter> clienteWriter = new HashSet<>();

    public static void main(String[] args) {

        System.out.println("El servidor está escuchando en el puerto: " + Puerto);

        try (ServerSocket serverSocket = new ServerSocket(Puerto)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");

        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clienteWriter) {
                    clienteWriter.add(out);
                }
                String mensaje;
                while ((mensaje = in.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + mensaje);
                    synchronized (clienteWriter) {
                        for (PrintWriter writer : clienteWriter) { // Enviar mensaje a todos los clientes
                            writer.println(mensaje);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clienteWriter) {
                    clienteWriter.remove(out);
                }
            }
        }
    }
}
