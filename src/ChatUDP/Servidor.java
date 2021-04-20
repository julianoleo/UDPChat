package ChatUDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;

public class Servidor {
    private static final HashSet<Integer> portSet = new HashSet<Integer>();
    public static void main(String args[]) throws Exception {
        int serverport = 7777;
        if (args.length < 1) {
            System.out.println("UDPServer Chat utilizando porta = " + serverport);
        }
        else {
            serverport = Integer.valueOf(args[0]).intValue();
            System.out.println("UDPServer Chat agora utilizando porta = " + serverport);
        }
        DatagramSocket udpServerSocket = new DatagramSocket(serverport);
        System.out.println("Servidor online...\n");

        while(true)
        {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            udpServerSocket.receive(receivePacket);
            String clientMessage = (new String(receivePacket.getData())).trim();
            System.out.println("Cliente Conectado - Socket Endereco: " + receivePacket.getSocketAddress());
            System.out.println("Mensagem do cliente: \"" + clientMessage + "\"");
            InetAddress clientIP = receivePacket.getAddress();
            System.out.println("IP do cliente + HostName: " + clientIP + ", " + clientIP.getHostName() + "\n");
            int clientport = receivePacket.getPort();
            System.out.println("Adicionando "+clientport);
            portSet.add(clientport);
            String returnMessage = clientMessage.toUpperCase();
            System.out.println(returnMessage);
            byte[] sendData  = new byte[1024];
            sendData = returnMessage.getBytes();
            for(Integer port : portSet)
            {
                System.out.println(port != clientport);
                if(port != clientport)
                {
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
                    System.out.println("Enviando...");
                    udpServerSocket.send(sendPacket);
                }
            }
        }
    }
}