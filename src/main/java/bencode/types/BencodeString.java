package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeString extends AbstractBencodeType<BencodeString> {

    protected byte[] value = new byte[0];

    protected int length_prefix_value;


    protected void writePrefix(OutputStream os) throws IOException{
        os.write(Integer.toString(value.length).getBytes(BencodeObject.CHARSET_TAG));
        os.write(':');
    }

    protected void writeValue(OutputStream os) throws IOException{
        os.write(value);
    }

    protected void writeSuffix(OutputStream os) throws IOException{
        //empty
    }

    public BencodeString(){ }

    public BencodeString(int firstByte){
      this.first_byte = (byte)firstByte;
    }

    public BencodeString(byte[] value){
        this.value = value;
    }

    public byte[] getValue(){
        return Arrays.copyOf(this.value, this.value.length);
    }


    public String getRepre(){
        String result = new String(value);
        return result;
    }

    public BencodeString parse(PositionedInputStream pos) throws IOException{
        parseLengthPrefix(pos);
        parseValue(pos);
        return this;
    }

    protected void parseLengthPrefix(PositionedInputStream pos) throws IOException{
        ByteArrayOutputStream length_tag_collector = new ByteArrayOutputStream();

        int actual_first_byte = (this.first_byte != -1)? first_byte: pos.read();
        notExpectEndOfStream(actual_first_byte, pos);
        int actual_next_byte = pos.read();
        notExpectEndOfStream(actual_next_byte, pos);


        length_tag_collector.write(actual_first_byte);
        if(actual_first_byte == '0' && actual_next_byte != ':'){
            throw new BencodeFormatViolated(pos.getPos(), this.getClass(), "int prefix in string starts with 0, but not followed by ':' immediately");
        }

        while( actual_next_byte != ':'){
            if (actual_next_byte < '0' || actual_next_byte > '9')
                throw new BencodeFormatViolated(pos.getPos(), this.getClass(), "int prefix in string containt non-digit character: " + (char)actual_next_byte);
            length_tag_collector.write(actual_next_byte);

            actual_next_byte = pos.read();
            notExpectEndOfStream(actual_next_byte, pos);
        }

        this.length_prefix_value = Integer.parseInt(new String(length_tag_collector.toByteArray(), BencodeObject.CHARSET_TAG));
    }

    protected void parseValue(PositionedInputStream pos) throws IOException{
        this.value = retrieveValueFromStream(pos);
    }

    protected byte[] retrieveValueFromStream(PositionedInputStream pos) throws IOException{
        ByteArrayOutputStream value_collector = new ByteArrayOutputStream();

        int next_byte;
        for(int i = 0; i < this.length_prefix_value; i ++){
            next_byte = pos.read();
            notExpectEndOfStream(next_byte, pos);
            value_collector.write(next_byte);
        }

        return value_collector.toByteArray();
    }

    public boolean equals(Object o) {
        if (o != null) {
            if (o instanceof byte[]) {
                return Arrays.equals(this.value, (byte[]) o);
            } else if (o instanceof BencodeString) {
                return Arrays.equals(this.value, ((BencodeString) o).value);
            } else return false;
        } else return false;
    }
}
