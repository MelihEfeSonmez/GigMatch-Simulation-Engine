public class MyHashTable<V> {

    // DATA FIELDS
    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Node<V>[] table;
    private int size;
    private int capacity;

    // Node class for chaining in hash table
    private static class Node<V> {
        String key;
        V value;
        Node<V> next;

        Node(String key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    // CONSTRUCTORS
    // Default constructor
    public MyHashTable() {
        this.capacity = DEFAULT_CAPACITY;
        this.table = (Node<V>[]) new Node[capacity];
        this.size = 0;
    }

    // ---HELPER METHODS
    // Computes hash index for a key
    private int hash(String key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        h ^= (h >>> 16);
        return Math.abs(h % capacity);
    }

    // ---METHODS---
    // Puts a key-value pair into the hash table
    public void put(String key, V value) {
        if (key == null) {
            return;
        }

        // Checks if resize is needed
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Node<V> current = table[index];

        // Checks if key already exists and updates value
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // Adds new node at the beginning of the chain
        Node<V> newNode = new Node<>(key, value);
        newNode.next = table[index];
        table[index] = newNode;
        size++;
    }

    // Gets the value associated with a key
    public V get(String key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);
        Node<V> current = table[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    // Checks if the hash table contains a key
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    // Removes a key-value pair from the hash table
    public V remove(String key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);
        Node<V> current = table[index];
        Node<V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                V value = current.value;

                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    // Size of the hash table
    public int size() {
        return size;
    }

    // Removes all key-value pairs
    public void clear() {
        table = (Node<V>[]) new Node[capacity];
        size = 0;
    }

    // Resizes the hash table when load factor is exceeded
    private void resize() {
        int newCapacity = capacity * 2;
        Node<V>[] oldTable = table;

        table = (Node<V>[]) new Node[newCapacity];
        capacity = newCapacity;
        size = 0;

        // Rehash all elements
        for (int i = 0; i < oldTable.length; i++) {
            Node<V> current = oldTable[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    // Returns an array of all values
    public Object[] values() {
        Object[] result = new Object[size];
        int index = 0;

        for (int i = 0; i < capacity; i++) {
            Node<V> current = table[i];
            while (current != null) {
                result[index++] = current.value;
                current = current.next;
            }
        }

        return result;
    }

}
