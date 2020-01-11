package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;
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
public class BencodeIntegerTest extends Assert{


    private long[] test_data = new long[DATA_POINTS];

    public static final int DATA_POINTS = 100;

    @Before
    public void getRandomTestData(){
        Random rand = new Random();
        test_data[0] = 0;
        for(int i = 1; i < test_data.length; i++){
            test_data[i] = rand.nextLong();
        }
    }

    public static void value_correspondence_contract(Long value, byte[] repre) throws Exception{
        String representation = new String(repre, BencodeObject.CHARSET_TAG);
        String expectedRepre =  "i" + Long.toString(value) + "e";


        String msg = String.format("Expected representation: [%s] , actual representation [%s] " , expectedRepre, representation);
        System.out.println(msg);
        assertTrue("ERROR: " + msg, representation.equals(expectedRepre));
    }

    @Test
    public void checkWriteOut() throws Exception{

        ByteArrayOutputStream out = new ByteArrayOutputStream(100);

        for (long value : test_data){
            BencodeInteger be_in = new BencodeInteger(value);
            be_in.write(out);
        }
        out.close();

        List<BencodeInteger> ints = new ArrayList<BencodeInteger>();
        ByteArrayInputStream inputStream = new ByteArrayInputStream( out.toByteArray());
        PositionedInputStream pos = new PositionedInputStream(inputStream);
        for(int i = 0; i < test_data.length; i++){
            int next = inputStream.read();
            assertEquals( (char)next, 'i');
            BencodeInteger integer = new BencodeInteger().parse(pos);
            ints.add(integer);
        }

        System.out.println(out.toString());
        for(int i = 0; i < test_data.length; i++)
            assertEquals(ints.get(i), test_data[i] );
    }

    @Test(expected= BencodeFormatViolated.class)
    public void checkInvalidOUt() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream(100);

        out.write( "i2783435".getBytes(BencodeObject.CHARSET_TAG));
        out.close();

        PositionedInputStream in_pos = new PositionedInputStream(new ByteArrayInputStream(out.toByteArray()));
        in_pos.read();
        BencodeInteger integer = new BencodeInteger().parse(in_pos);


    }

    @Test(expected= BencodeFormatViolated.class)
    public void checkInvalidOUt1() throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream(100);

        out.write( "ie".getBytes(BencodeObject.CHARSET_TAG));
        out.close();

        PositionedInputStream in_pos = new PositionedInputStream(new ByteArrayInputStream(out.toByteArray()));
        in_pos.read();
        BencodeInteger integer = new BencodeInteger().parse(in_pos);
    }



}
