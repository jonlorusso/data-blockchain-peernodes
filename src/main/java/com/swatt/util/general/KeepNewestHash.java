package com.swatt.util.general;

import java.util.HashMap;

public class KeepNewestHash {
    private HashMap<String, WrappedObject> wrappedObjects = new HashMap<String, WrappedObject>();
    private long maxSize;
    private WrappedObject first;
    private WrappedObject last;
    private long startTime = System.currentTimeMillis();

    private class WrappedObject {
        private String key;
        private Object value;
        private long lastUsedTime;
        private WrappedObject next = null;
        private WrappedObject prev = null;

        WrappedObject(String key, Object value) {
            this.key = key;
            this.value = value;
            this.lastUsedTime = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return key + ": " + value + " " + (lastUsedTime - startTime) + " " + (next != null);
        }
    }

    public KeepNewestHash(long maxSize) {
        this.maxSize = maxSize;
    }

    public Object get(String key) {
        WrappedObject wrappedObject = wrappedObjects.get(key);

        if (wrappedObject != null) {
            wrappedObject.lastUsedTime = System.currentTimeMillis();

            if (wrappedObject != first) { // Then reposition him to the front
                unLink(wrappedObject);

                if (first != null) {
                    first.prev = wrappedObject;
                    wrappedObject.next = first;
                }

                first = wrappedObject;
            }

            return wrappedObject.value;
        } else
            return null;
    }

    public void remove(String key) {
        WrappedObject wrappedObject = wrappedObjects.remove(key);

        if (wrappedObject != null)
            unLink(wrappedObject);
    }

    public long getAge(String key) {
        WrappedObject wrappedObject = wrappedObjects.get(key);

        if (wrappedObject != null) {
            return System.currentTimeMillis() - wrappedObject.lastUsedTime;
        } else
            return -1;
    }

    public long getYoungestTime() {
        if (first != null)
            return first.lastUsedTime;
        else
            return System.currentTimeMillis();
    }

    public long getOldestTime() {
        if (last != null)
            return last.lastUsedTime;
        else
            return System.currentTimeMillis();
    }

    public void removeOlderThan(long purgeOlderThanTime) {
        long old = System.currentTimeMillis() - purgeOlderThanTime;

        for (WrappedObject wrappedObject = last; wrappedObject != null; wrappedObject = wrappedObject.prev) { // Traverse
                                                                                                              // from
                                                                                                              // last to
                                                                                                              // first
            if (wrappedObject.lastUsedTime < old) { // everyone older than this has to go
                if (wrappedObject == first) { /// then we are removing everything
                    first = null;
                    last = null;
                } else {
                    last = wrappedObject.prev;
                    last.next = null;
                }

                for (; wrappedObject != null; wrappedObject = wrappedObject.next) {
                    String key = wrappedObject.key;
                    wrappedObjects.remove(key);
                }

                break;
            }
        }
    }

    public void put(String key, Object value) {
        remove(key); // in case this is a replace

        WrappedObject wrappedObject = new WrappedObject(key, value);
        wrappedObjects.put(key, wrappedObject);

        if (first != null) { // Make this the new first
            first.prev = wrappedObject;
            wrappedObject.next = first;
            first = wrappedObject;
        } else { // We were empty, ie we are the first item in the collection
            first = wrappedObject;
            last = wrappedObject;
        }

        if (wrappedObjects.size() > maxSize) { // Remove the last entry
            remove(last.key);
        }
    }

    public int size() {
        return wrappedObjects.size();
    }

    private void unLink(WrappedObject wrappedObject) {
        WrappedObject prev = wrappedObject.prev;
        WrappedObject next = wrappedObject.next;

        if (prev != null) {
            prev.next = next;
        } else { // That means that we are removing first
            first = next;
        }

        if (next != null) {
            next.prev = prev;
        } else { // That means we are removing last
            last = prev;
        }
    }
}