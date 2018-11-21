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
    private Boolean debugging = false;

    public NonBlockServer() {
        port = 8008;
        path = "/";
    }

    public void startServer() throws IOException{

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        if(debugging) {
            System.out.println("Server ready...");
        }

        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true){
            if(selector.select(TIMEOUT) == 0)
                continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while(iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    handleAccept(key);
                    if(debugging) {
                        System.out.println("NEW CLIENT\n");
                    }
                } else if(key.isReadable()){
                    //System.out.println("NEW MESSAGE:\n");
                    handleRead(key);
                } else if(key.isConnectable()){
                    if(debugging) {
                        System.out.println("isConnectable = true");
                    }
                }
                iterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(),SelectionKey.OP_READ, socketChannel);

    }

    private void handleRead(SelectionKey key) throws IOException{
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String receiveData = "";

        while (true) {
            long bytesRead = socketChannel.read(buffer);

            if(bytesRead <= 0) {
                key.cancel();
                return;
            }

            buffer.flip();

            while (buffer.hasRemaining()){
                char c = (char)buffer.get();
                receiveData += c;
            }
            if(debugging) {
                System.out.println(receiveData + "\n");
            }
            buffer.clear();
            Request request = new Request(receiveData);
            if(request.isValidRequest()) {
                //response according to the message received
                Processor processor = new Processor(request, path);

                String output = processor.getResponse().toString();
                if(debugging){
                    System.out.println(output);
                }
                buffer.put(output.getBytes());
            } else {
                if(debugging) {
                    System.out.println("HTTP/1.0 400 Bad Request");
                }
                buffer.put("HTTP/1.0 400 Bad Request".getBytes());
            }
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
        }
    }

    public void setDebugging(Boolean debugging){
        this.debugging = debugging;
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
