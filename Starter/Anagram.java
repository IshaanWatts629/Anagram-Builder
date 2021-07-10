import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;


public class Anagram{

    static int SIZE = 37;
    public static void main(String[] args) {

        //long start = System.currentTimeMillis();

        //Constructing a trie for the vocabulary
        Trie trie = new Trie();

        try {
            File fileVocab = new File(args[0]);
            BufferedReader sVocab = new BufferedReader(new FileReader(fileVocab));
            int numVocab = Integer.parseInt(sVocab.readLine());

            while (numVocab > 0) {
                trie.insert(sVocab.readLine());
                numVocab--;
            }
            sVocab.close();
            
            // Taking an input word
            
            File fileInput = new File(args[1]);
            BufferedReader sInput = new BufferedReader(new FileReader(fileInput));
            int numInput = Integer.parseInt(sInput.readLine());

            while (numInput > 0) {
                String test = sInput.readLine();

                int[] keys = new int[SIZE];
                int N = test.length();

                // DS for storing all possible words from input
                HashMap hash = new HashMap();
                Vector<String> words = new Vector<>();
                char[] letters = new char[N];
                Vector<String> anagrams = new Vector<>();
                int sum = 0;
                int sumSq = 0;

                // Hashing all letters of input
                for (int i = 0; i < N; i++) {
                    char c = test.charAt(i);
                    int temp = index(c);
                    keys[temp]++;
                    letters[i] = c;
                    sum += temp;
                    sumSq += temp * temp;
                }
                Arrays.sort(letters);
                hash.setHashMap(N);

                // Getting all possible words
                getAllWords(keys, test, trie, hash, words);
                if (N > 8) {
                    build3WordHash(hash, words, N);
                }
                searchAnagrams(hash, words, letters, sum, N, sumSq, anagrams);
                Collections.sort(anagrams);
                for (String anagram : anagrams)
                    System.out.println(anagram);

                System.out.println(-1);
                numInput--;
            }
            sInput.close();
            //long end = System.currentTimeMillis();
            //System.out.println(end-start);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int index(char c){
        int val;
        if(c == 39)
            val = 0;
        else if(c>47 && c<58)
            val = c-47;
        else
            val = c-86;
        return val;
    }

    static void getAllWords(int[] keys, String test, Trie trie, HashMap hash, Vector<String> words) {

        Trie.TrieNode current = trie.root;
        StringBuilder str = new StringBuilder();

        for (int i = 0 ; i < SIZE ; i++) {
            if (keys[i] > 0 && current.Child[i] != null ) {
                str.append(current.Child[i].value);
                keys[i]--;
                searchWord(keys, str.toString(), test, current.Child[i], hash, words);
                keys[i]++;
                str = new StringBuilder();
            }
        }
    }

    static void searchWord(int[] keys,String str, String test, Trie.TrieNode root, HashMap hash, Vector<String> words){
        while(root.leaf>0) {
            if(str.length()==test.length()) {
                hash.insert2(str);
                words.add(str);
            }
            if(str.length()<test.length()-2) {
                hash.insert2(str);
                words.add(str);
            }
            root.leaf--;
        }
        root.leaf= root.counter;

        for (int K =0; K < SIZE; K++) {
            if (keys[K]>0 && root.Child[K] != null ) {
                char c = root.Child[K].value;
                keys[K]--;
                searchWord(keys, str+c, test, root.Child[K], hash, words);
                keys[K]++;
            }
        }
    }

    static void build3WordHash(HashMap hash, Vector<String> words,int N){
        for (String word1 : words) {
            for (String word2 : words) {
                String temp = word1 + word2;
                if (temp.length() < N - 2)
                    hash.insert3(word1, word2);
            }
        }
    }

    static void searchAnagrams(HashMap hash12, Vector<String> words, char[] letters, int sum, int N, int sumSq, Vector<String> anagrams){
        for (String temp : words) {
            if (temp.length() == N) {
                anagrams.add(temp);
                continue;
            }
            if (temp.length() < N - 5) {
                hash12.checkCompliment3(temp, sumSq, letters, anagrams);
            }
            hash12.checkCompliment2(temp, sum, letters, anagrams);
        }
    }
}

class HashMap{
    Vector<String>[] v;
    Vector<Vector<String>>[] v3;

    public void insert2(String s){
        int index = val2(s);
        if(v[index]==null){
            v[index] = new Vector<>();
        }
        v[index].add(s);
    }

    public void setHashMap(int N){
        v = new Vector[37*(N/3)];
        if(N>8) {
            int size = 36*36*(N-3)+1;
            v3 = new Vector[size];
        }
    }

    public void insert3(String s1, String s2){
        int index = val3(s1+s2);

        if(v3[index]==null){
            v3[index] = new Vector<>();
            Vector<String> temp = new Vector<>();
            temp.add(s1);
            temp.add(s2);
            v3[index].add(temp);
        }
        else{
            Vector<String> temp = new Vector<>();
            temp.add(s1);
            temp.add(s2);
            v3[index].add(temp);
        }
    }

    public void checkCompliment2(String s, int sum, char[] arr, Vector<String> anagrams){
        int val = 0;
        for(int i = 0; i<s.length(); i++)
            val = val+index(s.charAt(i));

        int compliment = sum-val;
        Vector<String> temp = getElement(compliment);

        if(temp!=null) {
            for (String value : temp) {
                String test = s + value;
                char[] tempArray = test.toCharArray();
                Arrays.sort(tempArray);

                if (Arrays.equals(tempArray, arr))
                    anagrams.add(s + " " + value);
            }
        }
    }

    public void checkCompliment3(String s, int sum, char[] arr, Vector<String> anagrams){
        int val = 0;
        for(int i = 0; i<s.length(); i++)
            val = val+index(s.charAt(i))*index(s.charAt(i));

        int compliment = sum-val;
        Vector<Vector<String>> temp = getElement2(compliment);

        if(temp!=null) {
            for (Vector<String> str : temp) {
                String test = s + str.get(0) + str.get(1);
                char[] tempArray = test.toCharArray();
                Arrays.sort(tempArray);

                if (Arrays.equals(tempArray, arr)) {
                    anagrams.add(s + " " + str.get(0) + " " + str.get(1));
                }
            }
        }
    }

    public Vector<String> getElement(int i){
        i = i % v.length;
        return v[i];
    }

    public Vector<Vector<String>> getElement2(int i){
        return v3[i];
    }

    public int index(char c){
        int val;
        if(c == 39)
            val = 0;
        else if(c>47 && c<58)
            val = c-47;
        else
            val = c-86;
        return val;
    }

    private int val2(String s){
        int r = 0;
        for(int i = 0; i<s.length(); i++)
            r = r+index(s.charAt(i));

        return r % v.length;
    }

    private int val3(String s){
        int r = 0;
        for(int i = 0; i<s.length(); i++)
            r = r+index(s.charAt(i))*index(s.charAt(i));

        return r;
    }
}

class Trie{

    static final int SIZE = 37;

    public static class TrieNode {
        public TrieNode[] Child = new TrieNode[SIZE];
        public int leaf;
        public int counter;
        public char value;

        public TrieNode(char value) {
            this.value = value;
        }
    }

    public TrieNode root = new TrieNode(' ');

    public void insert(String Key)
    {
        int n = Key.length();
        TrieNode current = root;

        for (int i=0; i<n; i++)
        {
            int index = index(Key.charAt(i));

            if (current.Child[index] == null) {
                current.Child[index] = new TrieNode(Key.charAt(i));
            }
            current = current.Child[index];
        }
        current.leaf++;
        current.counter++;
    }

    public int index(char c){
        int val;
        if(c == 39)
            val = 0;
        else if(c>47 && c<58)
            val = c-47;
        else
            val = c-86;
        return val;
    }
}


