import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

public class TestServer {

    private static BlockClient client;


    @BeforeClass
    public static void beforeClass() throws IOException {
        client = new BlockClient();
    }

    @Test
    public void testServer() throws IOException{
        assertTrue(client.testSending("GET /test.txt HTTP/1.0"));
    }
}
