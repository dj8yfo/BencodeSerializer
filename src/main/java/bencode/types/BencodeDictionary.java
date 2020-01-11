package bencode.types;

import bencode.PositionedInputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by djunx on 30.06.14.
 */
public class BencodeDictionary extends AbstractBencodeType<BencodeDictionary> {

    public static final char PREFIX = 'd';
    private SortedSet<BencodeDictEntry> data = new TreeSet<BencodeDictEntry>();

    public SortedSet<BencodeDictEntry> getData() {
        return this.data;
    }

    public BencodeDictionary(List<BencodeDictEntry> data) {
        if (data != null) {
            for (BencodeDictEntry element : data) {
                this.data.add(element);
            }
        }
    }

    @Override
    public boolean equals(Object o){
        if(o !=null && o instanceof BencodeDictionary){
            BencodeDictionary dict = (BencodeDictionary) o;
            if(this.data.size() != dict.data.size())
                return false;

            Iterator<BencodeDictEntry> iter1 = this.data.iterator(), iter2 = dict.data.iterator();
            while(iter1.hasNext()){
                BencodeDictEntry one = iter1.next(), two = iter2.next();
                if(!one.equals(two))
                    return false;
            }
            return true;
        } else return false;
    }

    @Override
    public BencodeDictionary parse(PositionedInputStream in) throws IOException {
        this.data.clear();
        int next_byte = in.read();
        while ((char) next_byte != BencodeObject.SUFFIX) {
            BencodeDictEntry new_entry = new BencodeDictEntry((byte) next_byte);
            new_entry.parse(in);

            this.data.add(new_entry);
            next_byte = in.read();
        }
        return this;
    }

    @Override
    protected void writePrefix(OutputStream os) throws IOException {
        os.write(PREFIX);
    }

    @Override
    protected void writeValue(OutputStream os) throws IOException {
        for (BencodeDictEntry element : data) {
            element.write(os);
        }
    }

    @Override
    protected void writeSuffix(OutputStream os) throws IOException {
        os.write(BencodeObject.SUFFIX);
    }

    @Override
    public String getRepre() {
        int current_offset = this.getOffset_in_repre();
        int children_offset = current_offset + 1;

        StringBuilder result = new StringBuilder();
        result.append("{\n");

        for (BencodeDictEntry dataElement : data) {
            dataElement.setOffset_in_repre(children_offset);
        }
        for (BencodeDictEntry element : data) {
            result.append(element.toString() + ";\n");
        }
        for (int i = 0; i < current_offset; i++) {
            result.append(AbstractBencodeType.OFFSET);
        }
        result.append("}");
        return result.toString();
    }


    public static class BencodeDictEntry extends AbstractBencodeType<BencodeDictEntry> implements Comparable<BencodeDictEntry> {
        private BencodeLiteralString key;
        private AbstractBencodeType<?> value;


        public BencodeDictEntry(BencodeLiteralString key, AbstractBencodeType<?> value) {
            this.key = key;
            this.value = value;
        }

        public BencodeDictEntry(byte first_byte) {
            this.first_byte = first_byte;
        }

        @Override
        public boolean equals(Object another){
            if(another != null && another instanceof BencodeDictEntry){
                BencodeDictEntry o = (BencodeDictEntry) another;
                return (this.key.equals(o.key) && this.value.equals(o.value));
            }else return false;
        }
        @Override
        public int compareTo(BencodeDictEntry another) {
            if (another != null) {
                return this.key.compareTo(another.key);

            } else return 1;
        }

        @Override
        public BencodeDictEntry parse(PositionedInputStream in) throws IOException {
            BencodeLiteralString key = new BencodeLiteralString(this.first_byte);
            key.parse(in);
            BencodeObject object = new BencodeObject();
            object.parse(in);
            this.key = key;
            this.value = object.getValue();
            return this;
        }

        @Override
        public void writePrefix(OutputStream os) {
        }

        @Override
        public void writeValue(OutputStream os) throws IOException {
            key.write(os);
            value.write(os);
        }

        @Override
        public void writeSuffix(OutputStream os) {
        }

        @Override
        public String getRepre() {
            int current_offset = this.getOffset_in_repre();
            int value_offset = current_offset + 1;

            StringBuilder result = new StringBuilder();
            result.append(key.toString() + ":\n");

            value.setOffset_in_repre(value_offset);
            result.append(value.toString() + "");
            return result.toString();
        }

    }
}
