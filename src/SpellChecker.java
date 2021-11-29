import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class SpellChecker {


    //All Final Global variables
    public final static int DICTIONARY_SIZE = 94321;
    public final static int ERRORS_SIZE =2797;


    public static void main(String[] args) {


        //String objects for the file names: (text = dictionary) , (passage = passage)
        String text = "",passage = "";

        while (true) {
            System.out.println("Type EXIT to end");                         //loop will only break if user inputs EXIT
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the file name for the dictionary (use 'dictionary.txt' if you dont have one):");
            text = scanner.nextLine();
            if(text.equals("EXIT")){
                break;
            }
            if (text.length() <= 0) {                                        //if a file name is not entered
                System.out.println("Invalid Input");
            } else {
                System.out.println("Enter the file name for the passage to be checked (use 'passage.txt' if you dont have one):");
                passage = scanner.nextLine();
                if(passage.equals("EXIT")){
                    break;
                }
                if (passage.length() <= 0) {                                    //if a file name is not entered
                    System.out.println("Invalid Input");
                } else {
                    System.out.println("");
                    System.out.println(readPassage(text, passage).toString());
                }
            }
        }
        System.out.println("EXITED");
    }


    public static HashTable readPassage(String text, String passage){

        HashTable dictionary = readDictionary(text);
        HashTable errors = new HashTable(ERRORS_SIZE);
        String line;              //this will be the one whole line in the passage
        String[] words;           //this will be an array that will hold all the words removing the spaces in the line
        Scanner sc;               //scanner object
        String[] arrayWords;      //this will have the punctuation free words from the words array
        int lineNum = 0;          //this will keep track of the number of lines

        try {
            sc = new Scanner(new BufferedReader(new FileReader(passage)));

            while (sc.hasNextLine()) {
                line = sc.nextLine();
                lineNum++;

                if (line.length() > 0) {    //if its not an empty line
                    words = line.split(" ");//get the words in one line

                    for (int w = 0; w < words.length; w++) {
                        arrayWords = clear(words[w]);
                        for(int a=0; a<arrayWords.length; a++){

                            if(arrayWords[a].length() > 1) { //if the word is of length more than 1 that is not a letter neither an empty space

                                if (!dictionary.contains(arrayWords[a])) {     //if the word is not in the dictionary
                                    if (!errors.contains(arrayWords[a])) {     //if the error hashtable does not already have the word
                                        errors.insert(arrayWords[a]);          //add the word
                                        errors.insert(arrayWords[a], lineNum); //and enqueue its first line
                                    } else {
                                        errors.insert(arrayWords[a], lineNum); //else enqueue its consecutive lines
                                    }
                                }
                            }
                        }
                    }
                }
            }
            sc.close();

        } catch (Exception ioe) {
            System.out.println(ioe.getMessage());
        }
        return errors;
    }

    //this method will read the Dictionary and create a hashtable containing the words of the Dictionary
    public static HashTable readDictionary (String fileName){

        HashTable dictionary = new HashTable(DICTIONARY_SIZE);
        String line;
        Scanner sc;

        try {
            sc = new Scanner(new BufferedReader(new FileReader(fileName)));

            while (sc.hasNextLine()) {
                line = sc.nextLine();

                if (line.length() > 0) {              //if its not an empty line
                    dictionary.insert(line);
                }
            }
            sc.close();

        } catch (Exception ioe) {
            System.out.println(ioe.getMessage());
        }
        return dictionary;
    }

    //this method will clear up all the punctuations in the word excluding apostrophe
    public static String[] clear(String word) {
        String[] arrayWords;
        arrayWords = word.trim().split("[^a-zA-Z']+");
        return arrayWords;
    }
}

//HashTable-------------------------------------------------------------------------------------------------------------
class HashTable {

    //The HashTable class will contain an array ofLinkedLists.
    // The LinkedList class will contain nodes comprising of String words and their respective queues.
    // The Queue class will contain Nodes that hold the line numbers.
    // The Node will be a simple node containg an int and a pointer to a next node

    private class LinkedList {

        private class ListNode {

            private class Queue {
                //QueueNode---------------------------------------------------------------------------------------------
                //
                //this class will be the building blocks for the Queue
                // and will contain an int which will be the line number
                // and the next Node
                private class QueueNode {

                    private int line;
                    private QueueNode next;

                    private QueueNode(int line, QueueNode next) {
                        this.line = line;
                        this.next = next;
                    }
                }
                //QueueNode---------------------------------------------------------------------------------------------

                //Queue-------------------------------------------------------------------------------------------------
                //
                //This class will implement a Queue that will contain the line numbers
                private QueueNode front;
                private QueueNode end;

                private Queue() {
                    this.front = this.end = null;
                }

                //this method will check whether the Queue already has the line number input
                private boolean contains(int line) {
                    QueueNode curr = front;

                    while (curr.next != null && curr.line != line) {
                        curr = curr.next;
                    }
                    return curr.line == line;
                }

                //this method will add a line number at the end as long as the Queue
                // does not already contain that line number
                private void enqueue(int line) {

                    QueueNode curr = front;
                    QueueNode newNode = new QueueNode(line, null);

                    if (front == null) {
                        front = newNode;
                        end = front;
                    } else {
                        if (!contains(line)) {
                            while (curr.next != null) {
                                curr = curr.next;
                            }
                            curr.next = newNode;
                            end = curr.next;
                        }
                    }
                }

                //this method will remove the front node that is the first line number added to the Queue
                public int dequeue(){
                    int remove = Integer.MIN_VALUE;
                    QueueNode temp = front;
                    if(temp != null){
                        remove = temp.line;
                        front = front.next;
                        temp.next = null;
                    }
                    return remove;
                }

                //getter for front QueueNode
                private QueueNode front() {
                    return front;
                }


                public String toString() {

                    String string = "";
                    while (front != null) {
                        string += " " + dequeue();
                    }
                    return string;
                }

            }
            //Queue-----------------------------------------------------------------------------------------------------

            //ListNode--------------------------------------------------------------------------------------------------
            //
            //this class will be the building block for the LinkedList
            // and will contain the word and its respective Queue if needed
            private String word;
            private  Queue queue;
            private  ListNode next;

            //The constructor will only take in the word and the next node and will instantiate an empty Queue
            private ListNode(String word, ListNode next) {
                this.word = word;
                this.queue = new Queue();
                this.next = next;
            }

            //this method will add a line number to the empty queue in the list node
            public void queueLine(int line) {
                this.queue.enqueue(line);
            }

            //A method to convert the LinkedList contents into a String
            public String toString() {

                String s ="Invalid word '"+ word + "' found on lines ";
                if (queue.front() != null) {
                    s += queue.toString();
                } else {
                    s += "0";
                }
                return s;
            }
        }
        //ListNode------------------------------------------------------------------------------------------------------

        //LinkedList-----------------------------------------------------------------------------------------------------
        //
        //this class will create the LinkedList that will implement separate chaining
        // for the words that will have the same index in the HashTable.


        private int size = 0; //will keep track of the size of the LinkedList
        private ListNode head;//pointer to head of LinkedList

        private LinkedList() {
            this.head = null;
        }

        //this method will check whether the word already exists in the LinkedList
        private boolean contains(String word) {
            ListNode curr = head;

            while (curr.next != null && !curr.word.equals(word)) {
                curr = curr.next;
            }
            return curr.word.equals(word);
        }

        //This method will add words to the linked list as long as the word does not already exists in the list
        private void addWord(String word) {
            if (head == null) {
                head = new ListNode(word, head);
            } else {
                if (!contains(word)) {
                    head = new ListNode(word, head);
                }
            }
            size++;
        }

        //a getter to get a specific node by its "word" signature
        private ListNode getListNode(String word) {
            ListNode curr = head;

            while (curr.next != null && !curr.word.equals(word)) {
                curr = curr.next;
            }
            return curr;
        }

        //this method will enqueue the line number to the specific node of the specific word
        private void addLine(String word, int line) {
            ListNode curr = getListNode(word);
            curr.queueLine(line);
        }

        //this method will convert the contents of the LinkedList to a String
        //we will go up till the node before the last uzing the size
        // in order to print the spaces between the numbers appropriately
        public String toString() {

            String string = "";
            ListNode curr = head;

            if (size != 1) {
                for (int i = 0; i < size - 1 && curr != null; i++) {
                    string += curr.toString() + "\n";
                    curr = curr.next;
                }
            }
            string += curr.toString();

            return string;
        }
    }
    //LinkedList--------------------------------------------------------------------------------------------------------

    //HashTable---------------------------------------------------------------------------------------------------------
    //
    //This class will create a hashtable using arrays of type LinkedList

    private final int V = 13; //This will be the prime number used to hash the given word using Horner's Method
    private int tableLength; //this will be the length of the array
    private int size;
    private LinkedList[] table;

    //constructor that will intialize the array for the table each having a new empty LinkedList
    public HashTable(int tableLength) {
        table = new LinkedList[tableLength];
        for(int i=0; i<table.length; i++){
            table[i] = new LinkedList();
        }
    }

    //hash - method that will use Horners method to convert each String into an int using its characters int values
    private int hash( String word ) {
        int hashIndex = 0;

        for ( int i = 0; i < word.length(); i++ ) {
            hashIndex = (hashIndex * V ) % table.length + (int) word.charAt(i);
            hashIndex = hashIndex % table.length;

        }
        return hashIndex;
    }

    private int hash( int[] array, int constant) {
        int hashIndex = 0;

        for ( int i = 0; i < array.length; i++ ) {
            System.out.println(array[i] +" + (("+hashIndex+" * "+constant+") % "+table.length+")");
            hashIndex = (hashIndex * constant ) % table.length + array[i];
            System.out.println("= "+hashIndex+" % 7");
            hashIndex = hashIndex % table.length;
            System.out.println("= "+hashIndex+"\n");
        }
        System.out.println("= "+hashIndex+"\n");
        return hashIndex;
    }

    //this method will insert a word only if the words length is more than 1
    // therefore excluding all empty spaces and letters and will keep track of the amount of the insertions
    public void insert(String word){

        if(word.length()>1) {
            word = word.toLowerCase();
            int key = hash(word);
            table[key].addWord(word);
        }
        size++;
    }

    //this method will only enqueue the line for the specific word provided
    public void insert(String word, int line){

        word = word.toLowerCase();
        int key = hash(word);
        table[key].addLine(word,line);
    }

    //this method will check whether the index of the table therefore its LinkedList have a certain word or not
    public Boolean contains (String word){
        word = word.toLowerCase();
        boolean inTable = false;

        int index = hash(word);
        if(table[index].head !=null){
            if(table[index].contains(word)){
                inTable = true;
            }
        }
        return inTable;
    }

    //this method will print out the contents of the hashTable
    public String toString(){

        String string = "There are a total of "+size+" invalid words:\n";
        for (int i=0; i<table.length; i++){
            if(table[i].head != null) {
                string += table[i].toString() + "\n";
            }
        }
        return string;
    }

    public static void main(String[] args) {
        HashTable ht = new HashTable(11);

        int[] array = {2,14,12,16,20,19,4,17};
        int num = ht.hash(array,7);

    }
}
//HashTable-------------------------------------------------------------------------------------------------------------