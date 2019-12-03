import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class ass1 {
    private ArrayList <String> names;
    private Hashtable<Character,ArrayList<Character>> charToNextChar;
    private ArrayList<Character> frequentFirstChar;
    private Random r;


    /**
     * This class handles:
     *  - fetching the HTML document from the internet
     *  - parsing the HTML to scrape relevant data
     *  - compile the data to a LinkedList to the user
     */
    public class NameFetcher {

        public String url;

        /**
         * Constructor for a new NameFetcher instance.
         * @param url - the url as a String
         */
        public NameFetcher(String url){
            this.url = url;
        }

        /**
         * Returns all the names in the site
         * @return a sorted linked list with all the names from all the pages
         */
        public LinkedList<String> getNames(){

            LinkedList<String> res = new LinkedList<String>(getNamesFromPage(this.url));//first page is not a sub-route
            for(int i = 2 ; i < 15 ; i++){
                res.addAll(getNamesFromPage(this.url + '/' + i));
            }

            Collections.sort(res);
            return res;
        }

        /**
         * Retrieves names from a single sub-route.
         * @param url - the specific url (including the page sub-route)
         * @return all of the names in the page as a set.
         */
        public HashSet<String> getNamesFromPage(String url){

            //Linked > Array because we are focused on insertion and not on random access
            HashSet<String> res = new HashSet<String>();
            String name = "";//temp for lowercase operations

            try
            {
                Document document = Jsoup.connect(url).get();
                for(Element element: document.select("span[class='listname']"))//for every <span> tag in the document
                {
                    name = element.child(0).text().split(" ")[0];//extract inner text, deal with "Abe (1)" case
                    name = allCaps2Normal(name);
                    if(isStringOnlyAlphabet(name))
                        res.add(name);
                }
            }
            catch(IOException e)
            {
                System.out.println("Error: problem connecting to url:" + url);
            }
            return res;
        }

        /**
         * Checks if string does is comprised of ASCII alphabet exclusively. Basically excludes diacritics like é.
         * @param str - string to check
         * @return true iff string contains only alphabets, false if not
         */
        public boolean isStringOnlyAlphabet(String str)
        {
            return ((str != null)
                    && (!str.equals(""))
                    && (str.matches("^[a-zA-Z]*$")));
        }

        /**
         * Correctly capitalises names.
         * @param name - a string
         * @return the original string with the first letter capitalised and the rest non-capitalised
         */
        private String allCaps2Normal(String name) {
            return name.charAt(0) + name.substring(1).toLowerCase();
        }
    }

    /**
     * Main class of the assignment.
     * Uses NameFetcher to scrape names from behindthename.com and do string operations on them.
     * @param url - the url to scrape from
     */
    public ass1 (String url)
    {
        NameFetcher fetcher = new NameFetcher(url);
        names = new ArrayList<String>(fetcher.getNames());
        r = new Random();
    }


    /**
     * CountSpecificString
     * this function need to get the given parameter (subString) and check if it is
     * a substring of a name in the website.
     * if it is a substring of many names, he will return and print the amount of the names.
     * @param subString - the string we check with
     * @return the times the gives subName came up in the website
     */
    public int CountSpecificString(String subString)
    {
        int counter=0;
        for (String name:names)
        {
            if (name.contains(subString))
                counter++;
        }
        System.out.println(counter);
        return counter;
    }


    /**
     *This function get an int n and make all substring from all the names in the storage in size n.
     * make it, return it and print it.
     * @param n - the size of the wanted substring.
     * @return all substring from all the names in the storage in size n in dictionary
     */
    public Hashtable<String,Integer> CountAllStrings(int n)
    {
        Hashtable<String,Integer> subNameCounter;
        subNameCounter=createSubStringsDict(n);
        printDict(subNameCounter);
        return subNameCounter;
    }


    /**
     *This function get an int n and make all substring from all the names in the storage in size n.
     *after it , it return the substring that came up the most.
     * @param n - the size of the wanted substring.
     * @return the most common subNames in the websites
     */
    public List <String> CountMaxString(int n) {
        Hashtable<String,Integer> subNameCounter;
        subNameCounter=createSubStringsDict(n);
        sortByValue(subNameCounter);
        List <String> mostCommons=getMostCommons(subNameCounter);
        printList(mostCommons);
        return mostCommons;
    }


    /**
     * Function 4
     * Receives a base string and returns every name that can be found inside of it.
     * @param gibberish - the string we want to search for names in
     * @return an array list containing all the names we can find inside
     */
    public ArrayList<String> AllIncludesString (String gibberish){
        ArrayList<String> stringsIncluded = new ArrayList<String>();
        for(String name: names){
            if(gibberish.contains(name.toLowerCase()))
                stringsIncluded.add(name);
        }
        return stringsIncluded;
    }


    /**
     * This function handles the name generation at the highest level.
     * It prints a string between 3 and 10 chars long (randomised)
     * Every letter in the string is followed by the letter most likely to follow it in the entire namespace
     * The first letter will be the most frequent letter among all *first* letters.
     * When multiple letters have the highest frequency, the method will roll one at a uniform distribution.
     * @return a 3-10 characters long string
     */
    public String GenerateName(){
        int n = r.nextInt((10 - 3) + 1) + 3;//min length for name: 3. max length: 10. why? because.
        calculateFrequencies();
        String gibberishButMyGibberish = ("" + pullRandom(frequentFirstChar));
        for(int i = 1 ; i < n ; i++){
            ArrayList<Character> nextCharOptions = charToNextChar.get(gibberishButMyGibberish.charAt(i-1));
            char chosenChar = pullRandom(nextCharOptions);
            gibberishButMyGibberish += chosenChar;
        }
        gibberishButMyGibberish = gibberishButMyGibberish.substring(0,1).toUpperCase() +gibberishButMyGibberish.substring(1);
        return gibberishButMyGibberish;
    }

    /**
     * an helper function that print a given list, one string each line
     * @param mostCommons - a list of strings we want to print
     */
    private void printList(List<String> mostCommons) {
        for(int i=0;i<mostCommons.size();i++){
            System.out.println(mostCommons.get(i));
        }
    }

    /**
     * when we have many subNames with the same freq, the function return the ones that same up the most.
     * @param subNameCounter - the subString from the names and they freq
     * @return the most commons subNames.
     */
    private List<String> getMostCommons(Hashtable<String, Integer> subNameCounter) {
        ArrayList <String> commons=new ArrayList<String>();
        int maxFreq=0;
        for( String key:subNameCounter.keySet())
        {
            if(maxFreq<=subNameCounter.get(key))
                maxFreq=subNameCounter.get(key);
        }
        for( String key:subNameCounter.keySet())
        {
            if(maxFreq==subNameCounter.get(key))
                commons.add(key);
        }
        return commons;
    }

    /**
     * function that sort dictionary(?,int) by it value
     * @param t - the dictionary
     */
    private static void sortByValue(Hashtable<?, Integer> t){

        //Transfer as List and sort it
        ArrayList<Map.Entry<?, Integer>> l = new ArrayList(t.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

            public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }});
    }

    /**
     * function which cut a string to all it possible subStrings in size n
     * @param name - the full string
     * @param n - the size of possible subString
     * @return all string's possible subStrings in size n
     */
    private ArrayList<String> cut2nSize(String name,int n) {
        ArrayList<String> subStrings=new ArrayList<String>();
        for (int i =0; i<=name.length()-n;i++)
        {
            subStrings.add(name.substring(i,i+n));
        }
        return subStrings;
    }

    /**
     *bya given dictionary of string and it freq AND  a list of strings,
     * it will add the strings in the list to the dict and update it frq
     * @param subNameCounter - the dictionary of the string and it freq
     * @param subNames - the lsit of the strings we need to add to the dict
     */
    private void add2dic(Hashtable<String, Integer> subNameCounter, ArrayList<String> subNames) {
        int temp;
        for(String subName:subNames)
        {
            if (!subNameCounter.containsKey(subName))
                subNameCounter.put(subName,1);
            else{
                temp=subNameCounter.get(subName)+1;
                subNameCounter.remove(subName);
                subNameCounter.put(subName,temp);
            }

        }
    }

    /**
     * print a dictionary of string and int line by line :
     * dvir:23
     * raz:8
     * joni:0
     * @param subNameCounter - the dictionary we need to print
     */
    private void printDict(Hashtable<String, Integer> subNameCounter) {
        for (Map.Entry<String, Integer> entry : subNameCounter.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    /**
     * by a given int n, we will make a dictionary that of all substring in names array
     * each name
     * @param n - wanted size
     * @return - dict of subNames and it frq
     */
    private Hashtable<String, Integer> createSubStringsDict(int n) {
        ArrayList<String> subNames;
        Hashtable<String,Integer> subNameCounter= new Hashtable();
        for (String name:names)
        {
            subNames=cut2nSize(name,n);
            add2dic(subNameCounter,subNames);
        }
        return subNameCounter;
    }

    /**
     * Called whenever we want to generate a new string.
     * This method maintains a of frequencies of every following letter for every letter.
     * It later calls other functions to divvy up the workload.
     */
    private void calculateFrequencies(){
        char[] lettersArray;//temp value
        int currFreq;//temp value


        //instantiate a new mapping of characters to mappings of the following characters to their frequencies
        Hashtable<Character,Hashtable<Character,Integer>> charToCharFreqs = new Hashtable<Character, Hashtable<Character, Integer>>();

        //instantiate a new mapping of first letters to their frequencies
        Hashtable<Character,Integer> firstCharsToFreqs;

        //instantiate a new empty mapping with a frequency of 0 for each letter
        Hashtable<Character,Integer> subDict = new Hashtable<Character, Integer>();//
        for(char c = 'a' ; c <= 'z' ; c++){
            subDict.put(c,0);
        }

        //use a copy constructor to put an empty dictionary in each of the letters' value
        for(char c = 'a' ; c <= 'z' ; c++){
            charToCharFreqs.put(c,new Hashtable<Character, Integer>(subDict));
        }

        firstCharsToFreqs = new Hashtable<Character, Integer>(subDict);

        for(String name: names){
            lettersArray = name.toLowerCase().toCharArray();

            //count first letter occurrence
            firstCharsToFreqs.put(lettersArray[0],firstCharsToFreqs.get(lettersArray[0]) + 1);

            //count the next letter's occurrence in the current letter's sub-dictionary
            for(int i = 0 ; i < lettersArray.length - 1 ; i++){
                currFreq = charToCharFreqs.get(lettersArray[i]).get(lettersArray[i+1]);
                charToCharFreqs.get(lettersArray[i]).put(lettersArray[i+1] , currFreq + 1);
            }
        }

        this.charToNextChar = compileComplexHashTable(charToCharFreqs);
        this.frequentFirstChar = compileHashTable(firstCharsToFreqs);
    }

    /**
     * Called after calculateSimilarities.
     * Receives a mapping of mappings. returns a mapping of characters to a list of characters with the highest values in its sub-mapping.
     * @param complexMap - a mapping of mappings.
     * @return a mapping of characters to a list of characters
     */
    private Hashtable<Character, ArrayList<Character>> compileComplexHashTable(Hashtable<Character, Hashtable<Character, Integer>> complexMap) {
        Hashtable<Character,ArrayList<Character>> res = new Hashtable<Character, ArrayList<Character>>();
        for(char c: complexMap.keySet()){
            res.put(c,compileHashTable(complexMap.get(c)));
        }
        return res;
    }

    /**
     * Called after calculateSimilarities.
     * Receives a mapping of characters to integers, returns a list of keys with th highest values
     * @param map - the map we want to aggregate
     * @return ArrayList of most frequent characters
     */
    public ArrayList<Character> compileHashTable(Hashtable<Character,Integer> map){
        ArrayList<Character> res = new ArrayList<Character>();
        int vMax = 0;
        for(char key: map.keySet()){
            if(map.get(key) > vMax){
                res.clear();
                res.add(key);
                vMax = map.get(key);
            }
            else if(map.get(key) == vMax){
                res.add(key);
            }
        }
        return res;
    }

    /**
     * This function rolls a random character uniformly from a list.
     * @param chars - the list of characters
     * @return a random character from the list
     */
    private char pullRandom(ArrayList<Character> chars) {
        int randomIndex = this.r.nextInt(chars.size());
        return chars.get(randomIndex);
    }

    public static void main(String[] args) {
        ass1 as1 = new ass1("https://www.behindthename.com/names/usage/english");

        if(args[0].equals("CountSpecificString")){
            System.out.println(as1.CountSpecificString(args[1]));
        }
        else if(args[0].equals("CountAllStrings")){
            as1.printDict(as1.CountAllStrings(Integer.parseInt(args[1])));
        }
        else if(args[0].equals("CountMaxString")){
            as1.printList(as1.CountMaxString(Integer.parseInt(args[1])));
        }
        else if(args[0].equals("AllIncludeString")){
            as1.printList(as1.AllIncludesString(args[1]));
        }
        else if(args[0].equals("GenerateName")){

            System.out.println(as1.GenerateName());
        }
        else{
            System.out.println("Error: Function not recognised");
        }
    }

}