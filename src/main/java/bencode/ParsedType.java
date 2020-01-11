package bencode;


import java.io.IOException;

public interface ParsedType<T> {

    public T parse(PositionedInputStream input) throws IOException;
}
