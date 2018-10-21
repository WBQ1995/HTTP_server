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
    private static final int TIMEOUT = 3000;
    private int port;
    private String path;


    public NonBlockServer() throws IOException {
        port = 8008;
        path = "/";
    }

    public void startServer() throws IOException{

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

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
                } else if(key.isConnectable()){
                    System.out.println("isConnectable = true");
                }
                iterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(key.selector(),SelectionKey.OP_READ, clientSocketChannel);

    }

    private void handleRead(SelectionKey key) throws IOException{
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String request = "";
        String response;

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
                request += c;
            }
            System.out.println(request + "\n");
            buffer.clear();
            //response according to the message received
            Processor processor = new Processor(request,path);

            response = processor.getResponse();

            buffer.put(response.getBytes());
            buffer.flip();
            clientSocketChannel.write(buffer);
            buffer.clear();
        }
    }




    public void setPort(int port){
        this.port = port;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getPath(){
        return this.path;
    }

}
