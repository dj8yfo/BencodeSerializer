package bencode;

import bencode.types.AbstractBencodeType;

import java.io.FileInputStream;
import java.io.IOException;


public class BencodeFormatViolated extends IOException {

    public final static String errorMsg = "bencode format violated, position - [%s], token type - [%s], descriptor - [%s]";

    public BencodeFormatViolated(String message) throws Exception {
        super(message);
    }

    public BencodeFormatViolated(int position, Class<? extends AbstractBencodeType> type, String submessage) {
        super(String.format(errorMsg, position + "", type.getSimpleName(), submessage));
    }

}
