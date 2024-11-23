import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final String SERVER_ADDRESS = "192.168.104.234";
    private static final int SERVER_PORT = 8000;

    private String messageUserNow = "";

    private PrintWriter out;

    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            byte[] ip = socket.getLocalAddress().getAddress();
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            LocalDateTime hora_fecha_actual = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String Hora_fecha_formateada = hora_fecha_actual.format(formatter);

            // Hilo para recibir mensajes
            new Thread(() -> {
                try {
                    String mensaje = " ";

                    while ((mensaje = in.readLine()) != null) {
                        if (mensaje.equals(messageUserNow)) {
                            //System.out.println("Ip del usuario: " + Arrays.toString(ip)); // -> IP de nuestra maquina

                        } else {
                            if ( mensaje.contains(Arrays.toString(ip))) {
                                System.out.println("["+Hora_fecha_formateada+"]" + " " + "Mensaje Enviado: " + mensaje);
                            } else {
                                System.out.println("["+Hora_fecha_formateada+"]" + " " + "Mensaje Recibido: " + mensaje);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            //Leer mensajes del usuario y enviarlos al servidor
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String mensaje = scanner.nextLine();
                messageUserNow = mensaje;

               // String messageToSend = Arrays.toString(ip) + mensaje;
               String messageToSend = Arrays.toString(ip) + " --> " + mensaje;

                out.println(messageToSend);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
* @ServerEndpoint("/websocket")
public class MyWebSocketServer {

    @OnOpen
    public void onOpen(Session session) {
        InetSocketAddress remoteAddress = session.getUserProperties().get("javax.websocket.endpoint.remoteAddress");
        String clientIp = remoteAddress.getAddress().getHostAddress();
        System.out.println("Nueva conexión desde IP: " + clientIp);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Mensaje recibido: " + message);
        // Aquí puedes enviar una respuesta al cliente
        try {
            session.getBasicRemote().sendText("Echo: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Conexión cerrada");
    }*/
