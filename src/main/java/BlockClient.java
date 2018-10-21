import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class BlockClient {

    public static void main(String[] args) throws IOException {
        clientStart();
    }

    public boolean testSending(String request) throws IOException{

        String host = "127.0.0.1";
        int port = 8008;

        InetSocketAddress serverAdress = new InetSocketAddress(host,port);

        SocketChannel clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.connect(serverAdress);
        if(clientSocketChannel.finishConnect()){
            System.out.println("Connect to server successfully...");
        }
        System.out.println();

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(request.getBytes());
        buffer.flip();
        clientSocketChannel.write(buffer);
        receive(clientSocketChannel);

        //clientSocketChannel.close();
        return true;
    }

    private static void clientStart() throws IOException {
        String host = "127.0.0.1";
        int port = 8008;

        InetSocketAddress serverAdress = new InetSocketAddress(host,port);

        SocketChannel clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.connect(serverAdress);
        if(clientSocketChannel.finishConnect()){
            System.out.println("Connect to server successfully...");
        }
        sendAndEcho(clientSocketChannel);
    }

    private static void sendAndEcho(SocketChannel clientSocketChannel) throws IOException{
        Scanner in = new Scanner(System.in);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (in.hasNext()){
            String line = in.nextLine();
            buffer.clear();
            buffer.put(line.getBytes());
            buffer.flip();
            clientSocketChannel.write(buffer);

            if(line.startsWith("*")){
                clientSocketChannel.close();
                System.exit(0);
            }
            receive(clientSocketChannel);
        }
    }

    private static void receive(SocketChannel clientSocketChannel) throws IOException{

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long bytesRead;


        while (true){
            bytesRead = clientSocketChannel.read(buffer);
            if (bytesRead > 0){
                buffer.flip();
                while (buffer.hasRemaining()){
                    System.out.print((char)buffer.get());
                }
                System.out.println();
                buffer.clear();
                return;
            }
        }

    }
}
