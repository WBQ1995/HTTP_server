import java.io.*;

public class Processor {

    private String request;
    private String response;
    private String path;

    public Processor(String request, String path){
        this.request = request;
        response = " ";
        if(path.equals("/")){
            this.path = System.getProperty("user.dir");
        } else {
            this.path = path;
        }
    }

    public String getResponse(){
        if(request.startsWith("GET")) {
            processGet();

        }
        return response;
    }

    private void processGet(){

        String[] bodies = request.split("\r\n");
        String[] firstLineElements = bodies[0].split(" ");

        if(!isValidRequestLine(bodies[0])){
            return;
        }

        if(firstLineElements[1].equals("/")){
            showAllFiles();
            return;
        }

        path += firstLineElements[1];

        try {
            readFile();
        } catch (FileNotFoundException ex ){
            response = "HTTP/1.0 404 Not Found";
        } catch (IOException ex){
            response = "HTTP/1.0 400 Bad Request";
        }
    }

    private void showAllFiles(){
        response = "";
        File file = new File(path);
        File[] filesList = file.listFiles();
        if (filesList.length == 0){
            response = "Empty Directory";
            return;
        }
        for (File files : filesList){

            if(files.isDirectory())
                response += files.getName() + "/" + "\n";
            else
                response += files.getName() + "\n";
        }
    }

    private void readFile() throws FileNotFoundException, IOException {
        File file = new File(path);
        FileReader reader = new FileReader(file);
        BufferedReader bReader = new BufferedReader(reader);
        StringBuilder sb = new StringBuilder();
        String data = "";
        while ((data =bReader.readLine()) != null) {
            sb.append(data + "\r\n");
        }
        bReader.close();
        response = sb.toString();
        if(response.equals(""))
            response = "Empty File";
    }

    private boolean isValidRequestLine(String requestLine){
        String[] firstLineElements = requestLine.split(" ");

        if(firstLineElements.length != 3){
            response = "HTTP/1.0 400 Bad Request";
            return false;
        }

        if(!firstLineElements[2].equals("HTTP/1.0")){
            response = "HTTP1.0 505 HTTP Version Not Supported";
            return false;
        }
        return true;
    }
}
