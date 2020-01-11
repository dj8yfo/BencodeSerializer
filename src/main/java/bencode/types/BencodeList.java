package bencode.types;

import bencode.PositionedInputStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
* Created by djunx on 30.06.14.
*/
public class BencodeList extends AbstractBencodeType<BencodeList>{

    private List<AbstractBencodeType<?>> data = new ArrayList<AbstractBencodeType<?>>();
    public static char PREFIX = 'l';
    @Override
    protected void writePrefix(OutputStream os) throws IOException{
        os.write(PREFIX);
    }

    @Override
    protected  void writeValue(OutputStream os) throws IOException{
        for(AbstractBencodeType<?> data_element : data){
            data_element.write(os);
        }
    }

    @Override
    protected void writeSuffix(OutputStream os) throws IOException{
        os.write(BencodeObject.SUFFIX);
    }

    public List<AbstractBencodeType<?>> get_inner_list(){
        return this.data;
    }

    public BencodeList(List<AbstractBencodeType<?>> list){
        if(list != null)
            this.data = list;
    }

    @Override
    public String getRepre(){
        int current_offset = this.getOffset_in_repre();
        int children_offset = current_offset + 1;
        for(AbstractBencodeType<?> dataElement : data){
            dataElement.setOffset_in_repre(children_offset);
        }
        StringBuilder result = new StringBuilder();
        result.append("[\n");
        for(AbstractBencodeType<?> dataElement : data){
            result.append(dataElement.toString() + ";\n");
        }
        for(int i = 0; i < current_offset; i++){
            result.append(AbstractBencodeType.OFFSET);
        }
        result.append("]");
        return result.toString();
    }

    @Override
    public BencodeList parse(PositionedInputStream input) throws IOException{
        this.data.clear();
        int next_byte = input.read();
        while((char)next_byte != BencodeObject.SUFFIX){
            BencodeObject object = new BencodeObject((byte)next_byte);
            AbstractBencodeType<?> next_val = object.parse(input).getValue();

            this.data.add(next_val);
            next_byte = input.read();
        }
        return this;
    }

    @Override
    public boolean equals(Object o){
        if(o !=null && o instanceof BencodeList){
            BencodeList other = (BencodeList) o;

            int thisSize = this.data.size(), otherSize = other.data.size();
            if (thisSize != otherSize) return false;

            for(int i = 0; i < thisSize; i++){
                if(!this.data.get(i).equals( other.data.get(i)))
                    return false;
            }
            return true;
        } else return false;
    }

}
