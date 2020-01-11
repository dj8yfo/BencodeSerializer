package bencode.types;

import bencode.PositionedInputStream;
import javafx.geometry.Pos;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeListTest extends Assert{

    private BencodeList list = new BencodeList(null);

    @Before
    public void generate_data() throws IOException{
        List<AbstractBencodeType<?>> data = this.list.get_inner_list();
        BencodeLiteralString string = new BencodeLiteralString("");
        data.add(string);
        for(AbstractBencodeType<?> element: generate_mixed_integer_string(10)){
            data.add(element);
        }
        data.add(new BencodeInteger(0));
        data.add(new BencodeList(generate_mixed_integer_string(5)));

        for(AbstractBencodeType<?> element: generate_mixed_integer_string(4)){
            data.add(element);
        }
    }

    public static List<AbstractBencodeType<?>> generate_mixed_integer_string(int dataPoints) throws IOException{
        List<AbstractBencodeType<?>> result = new LinkedList<AbstractBencodeType<?>>();
        Random rand = new Random();


        for(int i = 0; i < dataPoints/2; i++) {
            result.add(new BencodeInteger(rand.nextLong()/ (rand.nextInt(10000) + 1)));
        }

        for(int i = 0; i < dataPoints/2; i++) {
            RandomString rand_string = new RandomString(rand.nextInt(100) + 1);
            result.add(new BencodeString(rand_string.nextString().getBytes()));
        }
        return result;
    }

    @Test
    public void test_bencode_list_generation() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.list.write(out);
        System.out.println(String.format("Inner representation:\n%s", list.toString()));
        System.out.println(String.format("External representation:\n%s", new String(out.toByteArray(), BencodeObject.CHARSET_TAG)));

        BencodeList parsed_list = new BencodeList(null);

        PositionedInputStream strema = new PositionedInputStream(new ByteArrayInputStream(out.toByteArray()));
        int next_byte = strema.read();
        assertEquals('l', next_byte);
        parsed_list.parse(strema);

        assertEquals(list, parsed_list);

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        parsed_list.write(out1);

        System.out.println(String.format("External representation 1:\n%s", new String(out1.toByteArray(), BencodeObject.CHARSET_TAG)));

        assertTrue(Arrays.equals(out.toByteArray(), out1.toByteArray()) );


    }

}
