import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class BlockServer {

    public static void main(String[] args) throws IOException {
        serverStart();
    }

    private static void serverStart() throws IOException {
        int port = 8008;
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        System.out.println("Server ready...");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
        SocketChannel clientSocketChanel = serverSocketChannel.accept();

        while (true){
            long bytesRead = clientSocketChanel.read(buffer);

            if(bytesRead == -1)
                break;

            if(bytesRead > 0){

                buffer.flip();
                while (buffer.hasRemaining()){
                    byte b = buffer.get();

                    if(b == (byte)'0'){
                        serverSocketChannel.close();
                        System.exit(0);
                    }
                    System.out.print((char)b);
                    sendBuffer.put(b);
                }
                System.out.println();
                buffer.clear();

                sendBuffer.put(".BYE".getBytes());

                sendBuffer.flip();
                clientSocketChanel.write(sendBuffer);
                sendBuffer.clear();
            }
        }
    }

}
