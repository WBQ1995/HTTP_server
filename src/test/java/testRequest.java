import org.junit.Test;

import static org.junit.Assert.*;

public class testRequest {


    @Test
    public void testRequest(){
        Request request = new Request("GET /get?course=networking&assignment=1 HTTP/1.0\r\n" +
                "Host: httpbin.org]r\n" +
                "User-Agent:Concordia-HTTP/1.0");
        request.toSTring();
        assertTrue(request.isGet());
    }
}
