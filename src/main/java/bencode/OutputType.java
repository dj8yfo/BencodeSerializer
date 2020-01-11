package bencode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by djunx on 30.06.14.
 */
public interface OutputType {

    public void write(OutputStream os) throws IOException;
}
