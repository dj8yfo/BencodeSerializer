package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeLiteralString extends BencodeString implements Comparable<BencodeLiteralString>{

    private String string_value;


    public BencodeLiteralString(String s) throws CharacterCodingException{
        this.setValue(s);
    }

    public BencodeLiteralString(){
        super();
    }

    public BencodeLiteralString(int firstbyte){
        super(firstbyte);
    }

    public String getStringValue(){
        return this.string_value;
    }

    public void setValue(String s) throws CharacterCodingException{

        Charset valid_char_set = Charset.forName(BencodeObject.CHARSET_TAG);

        CharBuffer buf = CharBuffer.wrap(s);

        ByteBuffer result = valid_char_set.newEncoder().
                onMalformedInput(CodingErrorAction.REPORT).
                onUnmappableCharacter(CodingErrorAction.REPORT).encode(buf);

        this.string_value = s;
        byte[] b = new byte[result.remaining()];
        result.get(b, 0, b.length);
        this.value = b;
    }

    @Override
    protected void parseValue(PositionedInputStream pos) throws IOException{
        int start_pos = pos.getPos(), end_pos;
        byte[] value_candidate = this.retrieveValueFromStream(pos);
        end_pos = pos.getPos();

        Charset valid_char_set = Charset.forName(BencodeObject.CHARSET_TAG);

        ByteBuffer buf = ByteBuffer.wrap(value_candidate);

        CharBuffer result = null;
        try{
            result = valid_char_set.newDecoder().
                    onMalformedInput(CodingErrorAction.REPORT).
                    onUnmappableCharacter(CodingErrorAction.REPORT).decode(buf);
        }catch(CharacterCodingException exc){
            throw new BencodeFormatViolated(start_pos, this.getClass(), new String(value_candidate) + " byte[] cannot be decoded using " + BencodeObject.CHARSET_TAG);
        }

        this.string_value = result.toString();
        this.value = value_candidate;
    }

    @Override
    public boolean equals(Object o){
        if(o != null){
            if (o instanceof byte[]) {
                return Arrays.equals(this.value, (byte[]) o);
            } else if(o instanceof BencodeLiteralString){
                return this.string_value.equals(((BencodeLiteralString)o).string_value);
            }else if (o instanceof BencodeString) {
                return Arrays.equals(this.value, ((BencodeString) o).value);
            } else return false;
        }else return false;
    }


    @Override
    public int compareTo(BencodeLiteralString other){
        if(other !=null){
            return this.string_value.compareTo(other.string_value);
        } else return 1;
    }


}
