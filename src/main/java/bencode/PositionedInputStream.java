package bencode;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PositionedInputStream extends InputStream{

    private int pos = 0;

    private InputStream delegate;

    public PositionedInputStream(InputStream delegate){
        this.delegate = delegate;
    }

    public int getPos(){
        return this.pos;
    }

    public int read() throws IOException{
        int result = this.delegate.read();

        if(result != -1)
            this.pos++;

        return result;
    }

    public static void main(String[] args) throws Exception{
//        System.out.println(new File(".").getCanonicalPath());
        PositionedInputStream input = new PositionedInputStream(new FileInputStream("sample.torrent"));

        byte[] data = new byte[400];

        System.out.println(String.format("Current position of input - [%d]", input.getPos()));

        input.read(data);

        System.out.println(String.format("Current position of input - [%d]", input.getPos()));
        String output = new String(data);
        System.out.println(output);

        input.close();
    }



}
