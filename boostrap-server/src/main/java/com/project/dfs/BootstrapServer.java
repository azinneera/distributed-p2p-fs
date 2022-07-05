package com.project.dfs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

@SpringBootApplication
public class BootstrapServer {

    public static String portNumber;

    @Value("${server.port}")
    public void setPortNumber(String portNumber) {
        BootstrapServer.portNumber = portNumber;
    }

    public static void main(String[] args) {
        SpringApplication.run(BootstrapServer.class, args);

        DatagramSocket sock;
        String s;
        List<Neighbour> nodes = new ArrayList<>();

        try {
            sock = new DatagramSocket(Integer.parseInt(portNumber));
            System.out.println("Bootstrap Server created at " + portNumber + ". Waiting for nodes to join...");

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                // Print the details of incoming data - client ip : client port - client message
                System.out.println(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);

                StringTokenizer st = new StringTokenizer(s, " ");

                String length = st.nextToken();
                String command = st.nextToken();

                switch (command) {
                    case "REG": {
                        StringBuilder reply = new StringBuilder("REGOK ");

                        String ip = st.nextToken();
                        int port = Integer.parseInt(st.nextToken());
                        String username = st.nextToken();
                        if (nodes.size() == 0) {
                            reply.append("0");
                            nodes.add(new Neighbour(ip, port, username));
                        } else {
                            boolean isOkay = true;
                            for (Neighbour node : nodes) {
                                if (node.getPort() == port) {
                                    if (node.getUsername().equals(username)) {
                                        reply.append("9998");
                                    } else {
                                        reply.append("9997");
                                    }
                                    isOkay = false;
                                }
                            }
                            if (isOkay) {
                                if (nodes.size() == 1) {
                                    reply.append("1 ").append(nodes.get(0).getIp()).append(" ").append(nodes.get(0).getPort());
                                } else if (nodes.size() == 2) {
                                    reply.append("2 ").append(nodes.get(0).getIp()).append(" ")
                                            .append(nodes.get(0).getPort()).append(" ").append(nodes.get(1).getIp())
                                            .append(" ").append(nodes.get(1).getPort());
                                } else {
                                    Random r = new Random();
                                    int Low = 0;
                                    int High = nodes.size();
                                    int random_1 = r.nextInt(High - Low) + Low;
                                    int random_2 = r.nextInt(High - Low) + Low;
                                    while (random_1 == random_2) {
                                        random_2 = r.nextInt(High - Low) + Low;
                                    }
                                    System.out.println(random_1 + " " + random_2);
                                    reply.append("2 ").append(nodes.get(random_1).getIp()).append(" ")
                                            .append(nodes.get(random_1).getPort()).append(" ")
                                            .append(nodes.get(random_2).getIp()).append(" ")
                                            .append(nodes.get(random_2).getPort());
                                }
                                nodes.add(new Neighbour(ip, port, username));
                            }
                        }

                        reply.insert(0, String.format("%04d", reply.length() + 5) + " ");

                        DatagramPacket dpReply = new DatagramPacket(reply.toString().getBytes(),
                                reply.toString().getBytes().length, incoming.getAddress(), incoming.getPort());
                        sock.send(dpReply);
                        break;
                    }
                    case "UNREG": {
                        String ip = st.nextToken();
                        int port = Integer.parseInt(st.nextToken());
                        String username = st.nextToken();
                        for (int i = 0; i < nodes.size(); i++) {
                            if (nodes.get(i).getPort() == port) {
                                nodes.remove(i);
                                String reply = "0012 UNROK 0";
                                DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                                        incoming.getAddress(), incoming.getPort());
                                sock.send(dpReply);
                            }
                        }
                        break;
                    }
                    case "ECHO": {
                        for (Neighbour node : nodes) {
                            System.out.println(node.getIp() +
                                    " " + node.getPort() +
                                    " " + node.getUsername());
                        }
                        String reply = "0012 ECHOK 0";
                        DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length,
                                incoming.getAddress(), incoming.getPort());
                        sock.send(dpReply);
                        break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
