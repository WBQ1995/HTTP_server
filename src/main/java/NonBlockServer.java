import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NonBlockServer {

    private static final int BUF_SIZE = 1024;
    private static final int PORT = 8008;
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException{

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

        System.out.println("Server ready...");

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            if(selector.select(TIMEOUT) == 0)
                continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    System.out.println("NEW CLIENT\n");
                    handleAccept(key);
                } else if(key.isReadable()){
                    System.out.println("NEW MESSAGE:\n");
                    handleRead(key);
                } else if(key.isWritable()){
                    handleWrite(key);
                } else if(key.isConnectable()){
                    System.out.println("isConnectable = true");
                }
                iterator.remove();
            }
        }
    }

    private static void handleAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(key.selector(),SelectionKey.OP_READ, clientSocketChannel);

    }

    private static void handleRead(SelectionKey key) throws IOException{
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            long bytesRead = clientSocketChannel.read(buffer);

            if(bytesRead <= 0)
                return;

            buffer.flip();
            while (buffer.hasRemaining()){
                char c = (char)buffer.get();
                if(c == '0'){
                    clientSocketChannel.close();
                    return;
                }
                System.out.print(c);
            }
            System.out.println("\n");
            buffer.clear();
        }

    }

    private static void handleWrite(SelectionKey key){

    }

}
