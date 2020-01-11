package bencode.types;

import bencode.PositionedInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by djunx on 01.07.14.
 */
public class BencodeDictTest extends Assert {
    private BencodeDictionary list = new BencodeDictionary(null);

    @Before
    public void generate_data() throws IOException{
        SortedSet<BencodeDictionary.BencodeDictEntry> entries = list.getData();

        for(BencodeDictionary.BencodeDictEntry entry: generatedListDictEntries(10)){
            entries.add(entry            );
        }

        BencodeDictionary subDict = new BencodeDictionary(generatedListDictEntries(4));

        entries.add(new BencodeDictionary.BencodeDictEntry(new BencodeLiteralString("subdict"), subDict));

    }


    public static List<BencodeDictionary.BencodeDictEntry> generatedListDictEntries(int dataPoints) throws IOException {
        List<BencodeDictionary.BencodeDictEntry> result = new LinkedList<BencodeDictionary.BencodeDictEntry>();
        Random rand = new Random();


        for(int i = 0; i < dataPoints; i++) {
            BencodeInteger integer = new BencodeInteger(rand.nextLong()/ (rand.nextInt(1000000) + 1));
            RandomString rand_string = new RandomString(rand.nextInt(22) + 1);
            BencodeLiteralString key = new BencodeLiteralString(rand_string.nextString());
            result.add(new BencodeDictionary.BencodeDictEntry(key, integer));
        }

        return result;
    }

    @Test
    public void test_bencode_dict_generation() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        this.list.write(out);
        System.out.println(String.format("Inner representation:\n%s", list.toString()));
        System.out.println(String.format("External representation:\n%s", new String(out.toByteArray(), BencodeObject.CHARSET_TAG)));

        BencodeDictionary parsed_dict = new BencodeDictionary(null);

        PositionedInputStream strema = new PositionedInputStream(new ByteArrayInputStream(out.toByteArray()));
        int next_byte = strema.read();
        assertEquals('d', next_byte);
        parsed_dict.parse(strema);

        assertEquals(list, parsed_dict);

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        parsed_dict.write(out1);

        System.out.println(String.format("External representation 1:\n%s", new String(out1.toByteArray(), BencodeObject.CHARSET_TAG)));

        assertTrue(Arrays.equals(out.toByteArray(), out1.toByteArray()) );
    }
}
