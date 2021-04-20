package ChatUDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Cliente {
    public static void main(String args[]) throws Exception {
        int clientport = 7777;
        String host = "localhost";

        if (args.length < 1) {
            System.out.println("Cliente UDP usando: Host = " + host + ", Porta = " + clientport);
        }
        else {
            clientport = Integer.valueOf(args[0]).intValue();
            System.out.println("Cliente UDP agora usando: Host = " + host + ", Porta = " + clientport);
        }
        InetAddress ia = InetAddress.getByName(host);
        SenderThread sender = new SenderThread(ia, clientport);
        sender.start();
        ReceiverThread receiver = new ReceiverThread(sender.getSocket());
        receiver.start();
    }
}

class SenderThread extends Thread {
    private InetAddress serverIPAddress;
    private DatagramSocket udpClientSocket;
    private boolean stopped = false;
    private int serverport;

    public SenderThread(InetAddress address, int serverport) throws SocketException, SocketException {
        this.serverIPAddress = address;
        this.serverport = serverport;
        this.udpClientSocket = new DatagramSocket();
        this.udpClientSocket.connect(serverIPAddress, serverport);
    }
    public void halt() {
        this.stopped = true;
    }
    public DatagramSocket getSocket() {
        return this.udpClientSocket;
    }

    public void run() {
        try {
            byte[] data = new byte[1024];
            data = "".getBytes();
            DatagramPacket blankPacket = new DatagramPacket(data,data.length , serverIPAddress, serverport);
            udpClientSocket.send(blankPacket);
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            while (true)
            {
                if (stopped)
                    return;
                String clientMessage = inFromUser.readLine();
                if (clientMessage.equals("."))
                    break;
                byte[] sendData = new byte[1024];
                sendData = clientMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIPAddress, serverport);
                System.out.println("Mensagem Enviada: "+clientMessage);
                udpClientSocket.send(sendPacket);
                Thread.yield();
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
    }
}

class ReceiverThread extends Thread {

    private final DatagramSocket udpClientSocket;
    private boolean stopped = false;

    public ReceiverThread(DatagramSocket ds) throws SocketException {
        this.udpClientSocket = ds;
    }

    public void halt() {
        this.stopped = true;
    }

    public void run() {
        byte[] receiveData = new byte[1024];
        while (true) {
            if (stopped)
                return;
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            System.out.println("Mensagem: ");
            try {
                udpClientSocket.receive(receivePacket);
                System.out.println("Mensagem Recebida:");
                String serverReply =  new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(serverReply);
                System.out.println();
                Thread.yield();
            }
            catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}
