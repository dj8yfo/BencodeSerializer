package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by djunx on 30.06.14.
 */


public class BencodeStringTest extends Assert {

    public static final int DATA_SIZE = 140;
    private ByteArrayOutputStream out = new ByteArrayOutputStream(100);
    private final List<BencodeString> data_set = new ArrayList<BencodeString>(DATA_SIZE);

    @Before
    public void getDataSet() throws Exception{
        this.data_set.add(new BencodeLiteralString("") );
        this.data_set.add(new BencodeString("i\u0002ôå¡ø£\u008DÓò÷Aö{cÄ\u007Fë+ïÝ“B\u008Fµ^D‚þ\u0016".getBytes()) );
        this.data_set.add(new BencodeString("\u0007ýR$3\u0002Fl´Ôödƒõ÷íBÇë2-[O=\u001Cå§Ð’\u0019\u0019\u0003X\u000Eíï;\\f".getBytes()));
        this.data_set.add(new BencodeString("i\u0002ôå¡ø£\u008DÓò÷Aö{cÄ\u007Fë+ïÝ“B\u008Fµ^D‚þ\u0016".getBytes()));
        this.data_set.add(new BencodeString("G9’±f½núUMdËüýê÷7Q*uJ()\u007FÑˆýkj(Q’ÁËsf\u0013w7ˆ\u000F¸m".getBytes()));
        this.data_set.add(new BencodeLiteralString("very expensive long sTring"));


        Random rand = new Random();
        for(int i = 0; i< DATA_SIZE; i++){
            RandomString generator = new RandomString(rand.nextInt(20) + 1);
            this.data_set.add(new BencodeLiteralString(generator.nextString()));

        }
    }


    @Test
    public void test_bencode_string_write_and_parse_with_first_symbol() throws Exception{
        out.reset();
        for(BencodeString data_element: this.data_set)
            data_element.write(out);

        byte[] actual_representation = out.toByteArray();
        System.out.println( new String(actual_representation));
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        for(int i = 0; i < this.data_set.size(); i++ ){
            int first_byte = inputStream.read();
            assertTrue( '0' <= first_byte && first_byte <= '9');
            BencodeString string = new BencodeString(first_byte);
            string.parse(inputStream);
            assertEquals(data_set.get(i), string);
        }
    }

    @Test(expected = BencodeFormatViolated.class)
    public void testWronFormat() throws Exception{
        this.out.reset();
        byte[] repre = new String("02:camel").getBytes(BencodeObject.CHARSET_TAG);

        out.write(repre);
        byte[] actual_representation = out.toByteArray();
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        BencodeString string = new BencodeString();
        string.parse(inputStream);
    }

    @Test(expected = BencodeFormatViolated.class)
    public void testWronFormat1() throws Exception{
        this.out.reset();
        byte[] repre = new String("7:camel").getBytes(BencodeObject.CHARSET_TAG);

        out.write(repre);
        byte[] actual_representation = out.toByteArray();
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        BencodeString string = new BencodeString();
        string.parse(inputStream);
    }

    @Test
    public void test_bencode_string_write_and_parse_without_first_symbol() throws Exception{
        out.reset();
        for(BencodeString data_element: this.data_set)
            data_element.write(out);

        byte[] actual_representation = out.toByteArray();
        System.out.println( new String(actual_representation));
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        for(int i = 0; i < this.data_set.size(); i++ ){
            BencodeString string = new BencodeString();
            string.parse(inputStream);
            assertEquals(data_set.get(i), string);
        }
    }
}
