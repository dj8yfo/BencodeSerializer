package bencode.types;


import bencode.BencodeFormatViolated;
import bencode.PositionedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class BencodeInteger extends AbstractBencodeType<BencodeInteger>{


    public static final char PREFIX = 'i';
    private long value;
    @Override
    public void writePrefix(OutputStream os) throws IOException{
        os.write(PREFIX);
    }


    @Override
    protected void writeValue(OutputStream os) throws IOException {
        os.write(Long.toString(getValue()).getBytes(BencodeObject.CHARSET_TAG));
    }

    @Override
    protected void writeSuffix(OutputStream os) throws IOException{
        os.write(BencodeObject.SUFFIX);
    }

    @Override
    public BencodeInteger parse(PositionedInputStream input) throws IOException{
        int start_pos = input.getPos();

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        int next_byte = input.read();
        notExpectEndOfStream(next_byte, input);

        while(next_byte != BencodeObject.SUFFIX){
            buff.write(next_byte);
            next_byte = input.read();

            notExpectEndOfStream(next_byte, input);
        }
        String repre = new String(buff.toByteArray(), BencodeObject.CHARSET_TAG);
        try{
            this.value = Long.parseLong(repre);
        } catch (NumberFormatException exc){
            throw new BencodeFormatViolated(start_pos, this.getClass(), "Number Format Exception:" + exc.getMessage());
        }
        return this;
    }

    @Override
    public boolean equals(Object o){
        if(o != null) {
            if (o instanceof Number) {
                return this.value == ((Number) o).longValue();
            } else if (o instanceof BencodeInteger){
                return this.value == ((BencodeInteger)o).value;
            } else{
                return false;
            }
        }
        return false;
    }

    public String getRepre() {
        return "" + this.value;
    }

    public BencodeInteger(long value){
        this.setValue(value);
    }

    public BencodeInteger(){ }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
