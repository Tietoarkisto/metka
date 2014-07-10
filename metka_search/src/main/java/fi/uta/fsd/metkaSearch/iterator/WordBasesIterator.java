package fi.uta.fsd.metkaSearch.iterator;

import org.apache.lucene.util.CharsRef;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class WordBasesIterator implements Iterator<CharsRef> {

    private CharsRef next;
    private Parser parser;
    private boolean advance;

    public WordBasesIterator(String string) {
        this(string.toCharArray());
    }

    public WordBasesIterator(char[] chars) {
        parser = new Parser(chars);
        advance = true;

    }

    @Override
    public boolean hasNext() {
        if (advance){
            advance = false;
            next = parser.next();
        }
        return next != null;
    }

    @Override
    public CharsRef next() {
        if (!hasNext()){
            throw new NoSuchElementException();
        }
        advance = true;
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


    static class Parser {

        private int off;
        private char[] chars;

        public Parser(char[] chars) {
            this.chars = chars;
        }

        public CharsRef next(){

            CharsRef ref = null;
            boolean is_first = true;

            while(off < chars.length) {

                if (chars[off] != '+'){
                    throw new IllegalArgumentException("Wordbase must start with '+' at position " + off + ": " +
                            String.valueOf(chars, off, chars.length - off));
                }

                boolean is_suffix;
                int wstart, wstop;
                int wbstart, wbstop;

                boolean read_wb = false;

                outer:
                for(wstop = wstart = off + 1; wstop < chars.length; wstop++){
                    switch(chars[wstop]){
                        case '+':
                            break outer;

                        case '(':
                            read_wb = true;
                            break outer;

                        case ')':
                            throw new IllegalArgumentException("Invalid character '" + chars[wstop] + " at position "
                                    + wstop + ": " + String.valueOf(chars, wstop, chars.length-wstop));

                        default:
                            break;
                    }
                }

                if (read_wb){

                    if (wstop + 2  >= chars.length){
                        throw new IllegalStateException("Unexpected end of input: " + String.valueOf(chars));
                    }

                    wbstart = wstop + 1;
                    is_suffix = !is_first && chars[wbstart] == '+';

                    if (is_suffix){
                        wbstart ++;
                    }

                    outer:
                    for(wbstop = wbstart; wbstop < chars.length; wbstop++){
                        switch(chars[wbstop]){
                            case '+':
                            case '(':
                                throw new IllegalArgumentException("Invalid character '" + chars[wbstop] + " at position "
                                        + wstop + ": " + String.valueOf(chars, wbstop, chars.length-wbstop));

                            case ')':
                                break outer;

                            default:
                                break;
                        }
                    }

                    if (wbstop == chars.length){
                        throw new IllegalStateException("Unexpected end of input: " + String.valueOf(chars));
                    }

                    off = wbstop + 1;

                } else {
                    is_suffix = false;
                    wbstart = wstart;
                    wbstop = wstop;
                    off = wstop;
                }

                if (is_first){
                    //ref = new CharsRef(chars, wstart, wstop - wstart);
                    ref = new CharsRef(chars, wbstart, wbstop - wbstart);
                    is_first = false;
                    continue;
                }

                if (is_suffix){
                    //ref.append(chars, wstart, wstop - wstart);
                    ref.append(chars, wbstart, wbstop - wbstart);
                    continue;
                }

                // found next word
                off = wstart-1;
                break;
            }

            return ref;
        }
    }
}