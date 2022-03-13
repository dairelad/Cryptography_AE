import java.util.*;
class WordSetElement implements Comparable<WordSetElement> {
    String key;
    int freq;
    WordSetElement(String key) {
        this.key = key;
        freq = 0;
    }
    String getKey(){return this.key;}
    int getFreq(){return this.freq;}
    void update() { freq++; }
    public int compareTo(WordSetElement that)  {
        return -1*Integer.compare(freq, that.freq);
    }
    public String toString() { return "(" +  this.key + "," + this.freq + ")\n"; }
}

class MultiSet {
    Map<String, WordSetElement> map;
    MultiSet(String strSequence) {
        map = new HashMap<>();
        String[] tokens = strSequence.split("\\s+");
        for (String token: tokens) {
            WordSetElement x = map.get(token);
            if (x==null) {
                x = new WordSetElement(token);
                map.put(x.key, x); // push only once
            }
            x.update(); // increment freq
        }
    }
    public void print(){
        for(Map.Entry<String, WordSetElement> entry: map.entrySet()){
            System.out.print("(" + entry.getKey() + "," + entry.getValue().freq + ")");
        }
    }
    List<WordSetElement> getFreq() {
        List<WordSetElement> values = new ArrayList<>(map.values());
        Collections.sort(values);
        return values;
    }
    public String toString(){
        return map.toString();
    }
}