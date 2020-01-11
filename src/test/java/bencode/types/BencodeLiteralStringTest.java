package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeLiteralStringTest extends Assert {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    public static final int DATA_SIZE = 140;
    private ByteArrayOutputStream out = new ByteArrayOutputStream(100);
    private final List<BencodeLiteralString> data_set = new ArrayList<BencodeLiteralString>(DATA_SIZE);

    @Before
    public void getDataSet() throws Exception{
        this.data_set.add(new BencodeLiteralString("") );
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
        for(BencodeLiteralString data_element: this.data_set)
            data_element.write(out);

        byte[] actual_representation = out.toByteArray();
        System.out.println( new String(actual_representation));
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        for(int i = 0; i < this.data_set.size(); i++ ){
            int first_byte = inputStream.read();
            assertTrue( '0' <= first_byte && first_byte <= '9');
            BencodeLiteralString string = new BencodeLiteralString(first_byte);
            string.parse(inputStream);
            assertEquals(data_set.get(i).getStringValue(), string.getStringValue());
            assertEquals(data_set.get(i), string);
        }
    }

    @Test
    public void test_bencode_string_write_and_parse_without_first_symbol() throws Exception{
        out.reset();
        for(BencodeLiteralString data_element: this.data_set)
            data_element.write(out);

        byte[] actual_representation = out.toByteArray();
        System.out.println( new String(actual_representation));
        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(actual_representation));
        for(int i = 0; i < this.data_set.size(); i++ ){
            BencodeLiteralString string = new BencodeLiteralString();
            string.parse(inputStream);
            assertEquals(data_set.get(i).getStringValue(), string.getStringValue());
            assertEquals(data_set.get(i), string);
        }
    }


    @Test
    public void testValidStringConstructor() throws Exception{
        BencodeString str = new BencodeLiteralString("hank's here, madafaca");
        BencodeString str1 = new BencodeString("i\u0002ôå¡ø£\u008DÓò÷Aö{cÄ\u007Fë+ïÝ“B\u008Fµ^D‚þ\u0016".getBytes());
    }

    @Test(expected = BencodeFormatViolated.class)
    public void tetsParseInvalidString() throws Exception{
        out.reset();
        BencodeString string = new BencodeString("i\u0002ôå¡ø£\u008DÓò÷Aö{cÄ\u007Fë+ïÝ“B\u008Fµ^D‚þ\u0016".getBytes());
        string.write(out);

        PositionedInputStream inputStream = new PositionedInputStream(new ByteArrayInputStream(out.toByteArray()));
        BencodeLiteralString parsed = new BencodeLiteralString();

        parsed.parse(inputStream);

    }

    @Test
    public void testInvalidStringConstructor() throws Exception{
        thrown.expect(CharacterCodingException.class);
        BencodeString str = new BencodeLiteralString("i\u0002ôå¡ø£\u008DÓò÷Aö{cÄ\u007Fë+ïÝ“B\u008Fµ^D‚þ\u0016");

    }

}
