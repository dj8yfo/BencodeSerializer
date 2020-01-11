package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeObject extends AbstractBencodeType<BencodeObject> {

    public static final char SUFFIX = 'e';
    private AbstractBencodeType<?> value = null;

    public static final String CHARSET_TAG = "US-ASCII";

    public AbstractBencodeType<?> determineType(PositionedInputStream in) throws IOException {
        final int next_byte;
        if (this.first_byte != -1)
            next_byte = first_byte;
        else {
            next_byte = in.read();
        }
        notExpectEndOfStream(next_byte, in);

        return determineType(next_byte, in.getPos());
    }

    public AbstractBencodeType<?> determineType(int next_byte, int position_in_stream) throws IOException {
        char c = (char) next_byte;
        if (c >= '0' && c <= '9') {
            return new BencodeString(next_byte);
        } else if (c == BencodeInteger.PREFIX) {
            return new BencodeInteger();
        } else if (c == BencodeList.PREFIX) {
            return new BencodeList(null);
        } else if (c == BencodeDictionary.PREFIX) {
            return new BencodeDictionary(null);
        } else throw new BencodeFormatViolated(position_in_stream, this.getClass(), "Unexpected symbol in stream:" + c);
    }

    @Override
    public void write(OutputStream os) throws IOException {
        this.value.write(os);
    }

    public BencodeObject() {

    }

    public BencodeObject(byte first_byte) {
        this.first_byte = first_byte;
    }


    @Override
    protected void writePrefix(OutputStream os) throws IOException {
    }

    @Override
    protected void writeValue(OutputStream os) throws IOException {
    }

    @Override
    protected void writeSuffix(OutputStream os) throws IOException {
    }

    @Override
    public String getRepre() {
        return this.value.getRepre();
    }

    @Override
    public BencodeObject parse(PositionedInputStream input) throws IOException {
        value = determineType(input);
        value.parse(input);
        return this;
    }

    public AbstractBencodeType<?> getValue() {
        return value;
    }

    public void setValue(AbstractBencodeType<?> value) {
        this.value = value;
    }


    public static void main(String[] args) throws IOException{
        PositionedInputStream inputStream = new PositionedInputStream(System.in);

        BencodeObject obj = new BencodeObject().parse(inputStream);

        System.out.println( obj.toString());
    }
}
