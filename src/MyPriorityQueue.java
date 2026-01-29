import java.util.ArrayList;

public class MyPriorityQueue<E extends Comparable<? super E>> {

    // DATA FIELDS
    private ArrayList<E> heap; // Array-based heap
    private MyHashTable<Integer> indexMap; // For freelancers

    // CONSTRUCTORS
    // Default constructor
    public MyPriorityQueue() {
        this.heap = new ArrayList<>();
        this.indexMap = new MyHashTable<>();
    }

    // ---METHODS---
    // Returns true if the priority queue has no elements
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Adds a new element
    public boolean add(E e) {
        if (e == null) {
            throw new NullPointerException("MyPriorityQueue does not permit null elements.");
        }
        heap.add(e);
        int index = heap.size() - 1;

        // Stores heap index for freelancer
        String key = getKey(e);
        if (key != null) {
            indexMap.put(key, index);
        }

        percUp(index); // Keeps heap property valid
        return true;
    }

    // Removes and returns the root
    public E poll() {
        if (heap.isEmpty()) return null;

        E root = heap.get(0); // Stores the root
        // Removes root entry from indexMap
        String rootKey = getKey(root);
        if (rootKey != null) {
            indexMap.remove(rootKey);
        }

        int lastIndex = heap.size() - 1;
        E last = heap.remove(lastIndex);

        // Moves last element to root and updates index map
        if (!heap.isEmpty()) {
            heap.set(0, last);
            String lastKey = getKey(last);
            if (lastKey != null) {
                indexMap.put(lastKey, 0);
            }
            percDown(0); // // Keeps heap property valid
        }

        return root;
    }

    // Removes an object from the heap
    public boolean remove(Object o) {
        if (o == null || heap.isEmpty()) {
            return false;
        }

        // Fast removal for Freelancer using indexMap
        if (o instanceof Freelancer) {

            E e = (E) o;
            String key = getKey(e);
            if (key != null) {
                Integer indexObj = indexMap.get(key);
                if (indexObj != null) {
                    int index = indexObj;
                    int lastIndex = heap.size() - 1;
                    // Removes last element
                    E last = heap.remove(lastIndex);
                    indexMap.remove(key);

                    // Replaces removed wtih last and keeps heap property valid
                    if (index != lastIndex) {
                        heap.set(index, last);
                        String lastKey = getKey(last);
                        if (lastKey != null) {
                            indexMap.put(lastKey, index);
                        }
                        percDown(index);
                        percUp(index);
                    }
                    return true;
                }
            }
        }

        // Linear search for non-Freelancer
        // (here is not used, just for validation)
        int n = heap.size();
        for (int i = 0; i < n; i++) {
            if (heap.get(i).equals(o)) {
                int lastIndex = heap.size() - 1;
                E removed = heap.get(i); // Removes mapping
                String removedKey = getKey(removed);
                if (removedKey != null) {
                    indexMap.remove(removedKey);
                }
                // Removes last and replaces and keeps heap property valid
                E last = heap.remove(lastIndex);
                if (i != lastIndex) {
                    heap.set(i, last);
                    String lastKey = getKey(last);
                    if (lastKey != null) {
                        indexMap.put(lastKey, i);
                    }
                    percDown(i);
                    percUp(i);
                }
                return true;
            }
        }

        return false;
    }

    // ---HELPER METHODS---
    // Helps to keep heap property valid by percolating up
    private void percUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            E current = heap.get(i);
            E parentVal = heap.get(parent);

            // Swaps upward
            if (compare(current, parentVal) < 0) {
                swap(i, parent);
                i = parent;
            } else {
                break;
            }
        }
    }
    // Helps to keep heap property valid by percolating down
    private void percDown(int i) {
        int n = heap.size();
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < n && compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < n && compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            // Swaps downward
            if (smallest != i) {
                swap(i, smallest);
                i = smallest;
            } else {
                break;
            }
        }
    }

    // Compares two elements
    private int compare(E a, E b) {
        return a.compareTo(b);
    }

    // Swaps two elements and updates index map
    private void swap(int i, int j) {
        E elementI = heap.get(i);
        E elementJ = heap.get(j);

        heap.set(i, elementJ);
        heap.set(j, elementI);

        // Updates index map
        String keyI = getKey(elementI);
        if (keyI != null) {
            indexMap.put(keyI, j);
        }
        String keyJ = getKey(elementJ);
        if (keyJ != null) {
            indexMap.put(keyJ, i);
        }
    }

    // Returns the key (freelancerID)
    private String getKey(E e) {
        if (e instanceof Freelancer) {
            return ((Freelancer) e).getFreelancerID();
        }
        return null;
    }

}
