import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

/**
 * Realization of a map by means of a binary search tree
 *
 * @author Takunari Miyazaki
 * 
 * @author Baile Benedicte
 * version 1.0.0
 * 12/07/2022
 * 
 */
public class BinarySearchTreeMap<K, V> extends LinkedBinaryTree<Entry<K, V>>
        implements Map<K, V> {

    /**
     * comparator for collection of objects
     */
    protected final Comparator<K> comparator; // comparator

    /**
     * Node variable used by get(), put() and remove()
     */
    protected Position<Entry<K, V>> actionPos; // a node variable

    /**
     * Creates a BinarySearchTreeMap with a default comparator.
     */
    public BinarySearchTreeMap() {
        comparator = new DefaultComparator<>();
        addRoot(null);
    }

    public BinarySearchTreeMap(Comparator<K> comparator) {
        this.comparator = comparator;
        addRoot(null);
    }

    /**
     * Extracts the key of the entry at a given node of the tree.
     */
    protected K key(Position<Entry<K, V>> position) {
        return position.getElement().getKey();
    }

    /**
     * Extracts the value of the entry at a given node of the tree.
     */
    protected V value(Position<Entry<K, V>> position) {
        return position.getElement().getValue();
    }

    /**
     * Extracts the entry at a given node of the tree.
     */
    protected Entry<K, V> entry(Position<Entry<K, V>> position) {
        return position.getElement();
    }

    /**
     * Replaces an entry with a new entry (and reset the entry's location)
     */
    protected V replaceEntry(Position<Entry<K, V>> position, Entry<K, V> entry) {
        ((BSTEntry<K, V>) entry).position = position;
        return set(position, entry).getValue();
    }

    /**
     * Checks whether a given key is valid.
     */
    protected void checkKey(K key) throws IllegalArgumentException {
        if (key == null) // just a simple test for now
        {
            throw new IllegalArgumentException("null key");
        }
    }

    /**
     * Checks whether a given entry is valid.
     */
    protected void checkEntry(Entry<K, V> entry) throws IllegalArgumentException {
        if (!(entry instanceof BSTEntry)) {
            throw new IllegalArgumentException("invalid entry");
        }
    }

    /**
     * Auxiliary method for inserting an entry at an external node. Inserts a given
     * entry at a given external position,
     * expanding the external node to be internal with empty external children, and
     * then returns the inserted entry.
     * (page 11.1.2)
     *
     * @param position The external position
     * @param entry    The entry to add to the external
     * @return The entry added
     */
    protected Entry<K, V> insertAtExternal(Position<Entry<K, V>> position, Entry<K, V> entry) {

        set(position, entry); // insert an entry at an external node
        addLeft(position, null); // add left child for the entry
        addRight(position, null); // add right child for the entry

        return entry;

    }

    /**
     * Auxiliary method for removing an external node and its parent. Removes a
     * given external node and its
     * parent, replacing external's parent with external's sibling.
     *
     * @param external The position to remove.
     */
    protected void removeExternal(Position<Entry<K, V>> external) {
        if (!isExternal(external)) { // check if position is not external
            return;
        }
        if (isRoot(external)) { // check if it's the root's position
            remove(external); // remove the root
        } else {
            Position<Entry<K, V>> parent = parent(external); // get the position of the parent
            remove(external); // remove the child at external
            remove(parent); // remove the parent
        }
    }

    /**
     * Auxiliary method used by get, put, and remove.
     */
    protected Position<Entry<K, V>> treeSearch(K key, Position<Entry<K, V>> position) {
        if (isExternal(position)) {
            return position; // key not found; return external node
        }
        K curKey = key(position);
        int comp = comparator.compare(key, curKey);
        if (comp < 0) {
            return treeSearch(key, left(position)); // search left subtree
        } else if (comp > 0) {
            return treeSearch(key, right(position)); // search right subtree
        }
        return position; // return internal node where key is found
    }

    /**
     * Returns a value whose associated key is k.
     */
    public V get(K key) throws IllegalArgumentException {
        checkKey(key); // may throw an InvalidKeyException
        Position<Entry<K, V>> currentPos = treeSearch(key, root());
        actionPos = currentPos; // node where the search ended

        return isInternal(currentPos) ? value(currentPos) : null;
    }

    /**
     * Inserts an entry with a given key k and value v into the map, returning
     * the old value whose associated key is k if it exists.
     */
    public V put(K k, V x) throws IllegalArgumentException {
        checkKey(k); // may throw an IllegalArgumentException
        Position<Entry<K, V>> insPos = treeSearch(k, root());
        BSTEntry<K, V> e = new BSTEntry<>(k, x, insPos);
        actionPos = insPos; // node where the entry is being inserted
        if (isExternal(insPos)) { // we need a new node, key is new
            insertAtExternal(insPos, e).getValue();
            return null;
        }

        return replaceEntry(insPos, e); // key already exists
    }

    /**
     * Removes from the map the entry whose key is k, returning the value of
     * the removed entry.
     */
    public V remove(K k) throws IllegalArgumentException {
        checkKey(k); // may throw an IllegalArgumentException
        Position<Entry<K, V>> remPos = treeSearch(k, root());
        if (isExternal(remPos)) {
            return null; // key not found
        }
        Entry<K, V> toReturn = entry(remPos); // old entry
        if (isExternal(left(remPos))) {
            remPos = left(remPos); // left case
        } else if (isExternal(right(remPos))) {
            remPos = right(remPos); // right case
        } else { // entry is at a node with internal children
            Position<Entry<K, V>> swapPos = remPos; // find node for moving entry
            remPos = left(swapPos);
            do {
                remPos = right(remPos);
            } while (isInternal(remPos));
            replaceEntry(swapPos, parent(remPos).getElement());
        }
        actionPos = sibling(remPos); // sibling of the leaf to be removed
        removeExternal(remPos);

        return toReturn.getValue();
    }

    /**
     * Returns an iterable collection of the keys of all entries stored in the
     * map.
     */
    public Iterable<K> keySet() {
        List<K> keyList = new ArrayList<>(); // create an arrayList
        if (root() == null) { // check if the tree is not empty
            return java.util.Collections.emptySet();
        }
        for (Entry<K, V> entry : entrySet()) { // loop over the entries in the tree
            keyList.add(entry.getKey()); // get key of element and add it to the list
        }
        return keyList;

    }

    /**
     * Returns an iterable collection of the values of all entries stored in
     * the map.
     */
    public Iterable<V> values() {
        List<V> valueList = new ArrayList<>(); // create an arrayList
        if (root() == null) { // check if the tree is not empty
            return java.util.Collections.emptySet();
        }
        for (Entry<K, V> entry : entrySet()) { // loop over the entries in the tree
            valueList.add(entry.getValue()); // get value of element and add it to the list
        }
        return valueList;
    }

    /**
     * Returns an iterable collection of all entries stored in the map. The
     * sentinels are excluded.
     */
    public Iterable<Entry<K, V>> entrySet() {
        List<Entry<K, V>> entryList = new ArrayList<>(size); // create an arrayList
        if (root() == null) { // check if the tree is not empty
            return java.util.Collections.emptySet();
        }
        for (Entry<K, V> entry : inorderElements()) { // loop over the entries in the tree in order
            if (entry != null) // check that the entry is a valid entry
                entryList.add(entry); // add it to the list
        }
        return entryList;

    }

    /**
     * Return the entries of the tree in a form of a string
     */
    @Override
    public String toString() {
        ArrayList<String> list = new ArrayList<>(size); // create an array
        for (Entry<K, V> entry : entrySet()) { // loop over all entries in the tree
            list.add(String.format("(%s, %s)", entry.getKey(), entry.getValue())); // add all entries to the arraylist
                                                                                   // in a form of a string
        }
        return new StringBuilder(200).append("{").append(String.join(",", list)).append("}").toString(); // use string
                                                                                                         // builder to
                                                                                                         // add { } and
                                                                                                         // commas in
                                                                                                         // arrayList
    }

    /**
     * Nested class for location-aware binary search tree entries
     */
    protected static class BSTEntry<K, V> implements Entry<K, V> {
        /**
         * key associated with the value in the map
         */
        protected K key;
        /**
         * value stored in the map
         */
        protected V value;
        /**
         * position of the element in the tree
         */
        protected Position<Entry<K, V>> position;

        /**
         * constructor to create a binary tree with initial values
         */
        BSTEntry(K key, V v, Position<Entry<K, V>> position) {
            this.key = key;
            value = v;
            this.position = position;
        }

        /**
         * method to get the key of an element in the tree
         * 
         * @return the key of an element in the tree
         */
        public K getKey() {
            return key;
        }

        /**
         * method to get the value associated with a key in the tree
         * 
         * @return the value associated with a key in the tree
         */
        public V getValue() {
            return value;
        }

        /**
         * method to get the position of element in the tree
         * 
         * @return the position of element in the tree
         */
        public Position<Entry<K, V>> position() {
            return position;
        }
    }
}
