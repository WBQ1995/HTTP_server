import java.util.Arrays;
import java.util.List;

public class httpfs {

    public static void main(String[] args){


        NonBlockServer server = new NonBlockServer();
        setArgs(server, args);
        try {
            server.startServer();
        } catch (Exception ex){
            System.out.println("Server failed to open...");
        }

    }

    private static void setArgs(NonBlockServer server, String[] args){

        List<String> parameters = Arrays.asList(args);
        if(parameters.contains("-v")){
            server.setDebugging(true);
            System.out.println("Prints debugging messages.");
        }
        if(parameters.contains("-p")){
            int index = parameters.indexOf("-p");
            server.setPort(Integer.parseInt(parameters.get(index + 1)));
        }
        if(parameters.contains("-d")) {
            int index = parameters.indexOf("-d");
            server.setPath(parameters.get(index + 1));
        }
    }

}
