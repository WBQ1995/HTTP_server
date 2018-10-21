import java.io.File;

public class Processor {

    private String request;
    private String response;
    private String path;

    public Processor(String request, String path){
        this.request = request;
        response = "";
        if(path.equals("/")){
            this.path = System.getProperty("user.dir");
        } else {
            this.path = path;
        }
    }

    public String getResponse(){

        if(request.startsWith("GET")) {
            readFiles();
            return response;
        }
        return "";
    }

    private void processGet(){
        if(request.endsWith("/")){
            readFiles();
        }
    }

    private void readFiles(){
        File file = new File(path);
        File[] filesList = file.listFiles();
        for (File files : filesList){
            response += files.getName() + "\n";
        }
    }
}
