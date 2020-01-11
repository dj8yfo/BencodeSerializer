package bencode.types;

import bencode.BencodeFormatViolated;
import bencode.OutputType;
import bencode.ParsedType;
import bencode.PositionedInputStream;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public abstract class AbstractBencodeType<T extends AbstractBencodeType<T>> implements OutputType, ParsedType<T>{


    public static final String OFFSET = "    ";
    private int offset_in_repre = 0;
    protected byte first_byte = -1;

    public void write(OutputStream os) throws IOException{
        writePrefix(os);
        writeValue(os);
        writeSuffix(os);
    }

    protected abstract void writePrefix (OutputStream os) throws IOException;
    protected abstract void writeValue (OutputStream os) throws IOException;
    protected abstract void writeSuffix (OutputStream os) throws IOException;

    public abstract T parse(PositionedInputStream input) throws IOException;

    public String toString(){
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < getOffset_in_repre(); i++){
            builder.append(OFFSET);
        }
        builder.append(this.getRepre());
        return builder.toString();
    }

    public abstract String getRepre();

    public void notExpectEndOfStream(int byte_arg, PositionedInputStream in) throws BencodeFormatViolated {
        if(byte_arg == -1)
            throw new BencodeFormatViolated(in.getPos(), this.getClass(), "Unexpected end of stream");
    }


    public int getOffset_in_repre() {
        return offset_in_repre;
    }

    public void setOffset_in_repre(int offset_in_repre) {
        this.offset_in_repre = offset_in_repre;
    }
}
