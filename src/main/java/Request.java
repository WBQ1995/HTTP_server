import java.util.HashMap;

public class Request {

    private boolean isGet;
    private String filePath;
    private HashMap<String,String> headers;
    private String body;
    private String fromClient;

    private boolean validRequest;

    public Request(String fromClient) {
        headers = new HashMap<>();
        this.fromClient = fromClient;
        try {
            parse();
        } catch (Exception ex){
            validRequest = false;
        }
    }

    private void parse() throws Exception{

        String[] headerAndBody = fromClient.split("\n\n");
        if(headerAndBody.length > 2){
            validRequest = false;
            return;
        } else if(headerAndBody.length == 2){
            body = headerAndBody[1];
        }

        String[] requestLineAndHeaders = headerAndBody[0].split("\r\n");
        String requestLine = requestLineAndHeaders[0];
        String[] elements = requestLine.split(" ");
        if(elements.length != 3 || !elements[2].equals("HTTP/1.0")){
            validRequest = false;
            return;
        }

        if(elements[0].equals("GET")){
            isGet = true;
        } else if (elements[0].equals("POST")){
            isGet = false;
        } else {
            validRequest = false;
            return;
        }

        filePath = elements[1];

        if(requestLineAndHeaders.length > 1){
            for(int i = 1; i < requestLineAndHeaders.length; i ++){
                int index = requestLineAndHeaders[i].indexOf(":");
                String front = requestLineAndHeaders[i].substring(0,index);
                String end = requestLineAndHeaders[i].substring(index + 2, requestLineAndHeaders[i].length());
                headers.put(front,end);
            }
        }
    }

    public void toSTring(){
        System.out.println("method " + filePath + " HTTP/1.0");
        for (String key : headers.keySet()) {
            System.out.println(key + ":" + headers.get(key));
        }
        System.out.println();
        System.out.println(body);
    }

    public boolean isValidRequest(){
        return validRequest;
    }


    public boolean isGet() {
        return isGet;
    }

    public String getFilePath() {
        return filePath;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
