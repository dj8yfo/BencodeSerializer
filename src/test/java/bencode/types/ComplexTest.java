package bencode.types;

import bencode.PositionedInputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by djunx on 07.07.14.
 */
public class ComplexTest extends Assert {

    @Test
    public void parseFile() throws IOException{
        FileInputStream file_st = new FileInputStream("sample.torrent");
        PositionedInputStream in = new PositionedInputStream(file_st);
        BencodeObject obj = new BencodeObject();
        obj.parse(in);
        in.close();
        System.out.println(obj);

        ByteArrayOutputStream out = new ByteArrayOutputStream(400), out1 = new ByteArrayOutputStream(400);
        file_st = new FileInputStream("sample.torrent");
        BufferedInputStream in_buf = new BufferedInputStream(file_st, 100);
        byte[] buf = new byte[100];
        int read = in_buf.read(buf);
        while(read == 100){
            out.write(buf);
            read = in_buf.read(buf);
        }

        if(read != -1)
            out.write(buf, 0, read);
        out.close();
        in_buf.close();

        obj.write(out1);
        String delimiter = "-----------------------------------------------------------------------";

        System.out.println("\n" + delimiter + "\n" + out.toString() + "\n" + delimiter);
        System.out.println("\n" + delimiter + "\n" + out1.toString() + "\n" + delimiter);
        assertTrue(Arrays.equals(out.toByteArray(), out1.toByteArray()) );
    }
}
