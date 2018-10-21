import java.util.Arrays;
import java.util.List;

public class httpfs {

    public static void main(String[] args){

        try {

            NonBlockServer server = new NonBlockServer();

            setArgs(server, args);

            server.startServer();

        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    private static void setArgs(NonBlockServer server, String[] args){

        List<String> parameters = Arrays.asList(args);
        if(parameters.contains("-v")){
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
