package com.swatt.util;

import java.io.FileInputStream;
import java.io.IOException;

/* Copyright 2005 Gerry Seidman, Inc. All rights reserved.  GERRY SEIDMAN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. */

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class CollectionsUtilities {
	public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();
	public static final EmptyEnumeration EMPTY_ENUMERATION = new EmptyEnumeration();
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	public static Properties loadProperties(String propertiesFileName) throws IOException {
		Properties properties = new Properties();
		FileInputStream in = new FileInputStream(propertiesFileName);
		properties.load(in);
		in.close();
		
		return properties;
	}

	
	public static class EmptyIterator implements Iterator {
		public boolean hasNext() { return false; }
		public Object next() { throw new NoSuchElementException("EmptyIterator object are empty."); }
		public void remove() { throw new UnsupportedOperationException("You cannot remove anything from an empty iterator"); }
	}
	
	public static class EmptyEnumeration implements Enumeration {
		public boolean hasMoreElements() { return false; }
		public Object nextElement() { throw new NoSuchElementException("EmptyEnumation object are empty."); }
	}

	public static Enumeration getEnumerator(Vector vector) {
		return (vector != null) ? vector.elements() : EMPTY_ENUMERATION;
	}
	
	public static Iterator getIterator(Collection collection) {
		return (collection != null) ? collection.iterator() : EMPTY_ITERATOR;
	}

	public static Vector copyElements(Vector vector, Enumeration enumeration) {
		for (;enumeration.hasMoreElements(); ) {
			vector.addElement(enumeration.nextElement());
		}	
		
		return vector;
	}	
	
	public static Iterator createIterator(Object array[]) {
		return createList(array).iterator();
	}
	
	public static List createList(Object array[]) {
		return (List) add(new ArrayList(array.length), array);
	}

	/** @deprecate see add(Collection collection, Iterator iterator) **/  // GS Fix-Me: Deprecate this
	public static List copyElements(List list, Iterator iterator) {
		return (List) add(list, iterator);
	}
	
	/** @deprecate see add(Collection collection, Iterator iterator) **/  // GS Fix-Me: Deprecate this
	public static Collection copyElements(Collection collection, Iterator iterator) {
		return add(collection, iterator);
	}
	
	public static Collection add(Collection to, Collection from) {
		return add(to, from.iterator());
	}

	public static Collection add(Collection collection, Iterator iterator) {
		for (;iterator.hasNext(); ) {
			collection.add(iterator.next());
		}
		
		return collection;
	}
	
	public static Collection add(Collection collection, Enumeration enumeration) {
		for (;enumeration.hasMoreElements(); ) {
			collection.add(enumeration.nextElement());
		}
		
		return collection;
	}
	
	public static List add(List list, Iterator iterator) {
		for (;iterator.hasNext(); ) {
			list.add(iterator.next());
		}
		
		return list;
	}
	
	public static List addAll(List list, Collection collection) {
		list.addAll(collection);
		
		return list;
	}
	
	
	public static List createList(Iterator iterator) {
		return add(new ArrayList(), iterator);
	}

	public static Collection add(Collection collection, Object array[]) {
		if (array == null)
			return collection;
		
		for (int i=0; i < array.length; i++)
			collection.add(array[i]);
		
		return collection;
	}
	
	public static int getNumberOfElements(Iterator i) {
		int count = 0;
		
		for(; i.hasNext(); ) 
			count++;
		
		return count;
	}
	
	public static Iterator createSortedIterator(Iterator iterator, Comparator comparator) {
	    return createSortedList(iterator, comparator).iterator();
	}
	
	public static Iterator createSortedIterator(Hashtable hashtable) {
		ArrayList keys = new ArrayList();
		
		for (Enumeration e=hashtable.keys(); e.hasMoreElements(); ) {
			Comparable key = (Comparable) e.nextElement();
			keys.add(key);
		}
		
		Collections.sort(keys);
		
		ArrayList sortedElements = new ArrayList();
		
		for(Iterator i=keys.iterator(); i.hasNext(); ) {
			Comparable key = (Comparable) i.next();
			
			Object value = hashtable.get(key);
			
			sortedElements.add(value);
		}
		
		return sortedElements.iterator();
	}

	public static List createSortedList(Iterator iterator, Comparator comparator) {
	    List arrayList = copyElements(new ArrayList(), iterator);
	    Collections.sort(arrayList, comparator);
	    return arrayList;
	}
	
	public static final byte[] ensureCapacity(byte source[], int size) {
		if (size > source.length) 
			return resize(source, 0, source.length, size);
		else
			return source;
	}
	
	public static final byte[] increaseSize(byte source[]) { return resize(source, 0, source.length, source.length + 1); }
	public static final byte[] increaseSize(byte source[], int increaseSizeBy) { return resize(source, 0, source.length, source.length + increaseSizeBy); }
	public static final byte[] resize(byte source[], int newSize) { return resize(source, 0, source.length, newSize); }

	public static final byte[] resize(byte source[], int sourcePos, int sourceLength, int newSize) {
		byte result[] = new byte[newSize];
		
		int copyLength = Math.min(newSize, sourceLength);
		System.arraycopy(source, sourcePos, result, 0, copyLength);
		return result;
	}
	
	
	public static final int[] ensureCapacity(int source[], int size) {
		if (size > source.length) 
			return resize(source, 0, source.length, size);
		else
			return source;
	}
	
	public static final int[] increaseSize(int source[]) { return resize(source, 0, source.length, source.length + 1); }
	public static final int[] increaseSize(int source[], int increaseSizeBy) { return resize(source, 0, source.length, source.length + increaseSizeBy); }
	public static final int[] resize(int source[], int newSize) { return resize(source, 0, source.length, newSize); }

	public static final int[] resize(int source[], int sourcePos, int sourceLength, int newSize) {
		int result[] = new int[newSize];
		
		int copyLength = Math.min(newSize, sourceLength);
		System.arraycopy(source, sourcePos, result, 0, copyLength);
		return result;
	}
	
	public static final long[] ensureCapacity(long source[], int size) {
		if (size > source.length) 
			return resize(source, 0, source.length, size);
		else
			return source;
	}
	
	public static final long[] increaseSize(long source[]) { return resize(source, 0, source.length, source.length + 1); }
	public static final long[] increaseSize(long source[], int increaseSizeBy) { return resize(source, 0, source.length, source.length + increaseSizeBy); }
	public static final long[] resize(long source[], int newSize) { return resize(source, 0, source.length, newSize); }

	public static final long[] resize(long source[], int sourcePos, int sourceLength, int newSize) {
		long result[] = new long[newSize];
		
		int copyLength = Math.min(newSize, sourceLength);
		System.arraycopy(source, sourcePos, result, 0, copyLength);
		return result;
	}
	
	
	
	public static final Object[] ensureCapacity(Object source[], int size) {
		if (size > source.length) 
			return resize(source, 0, source.length, size);
		else
			return source;
	}
	
	public static Object[] resizingSet(int pos, Object value, Object[] arr) {
		if (pos >= arr.length) 
			arr = resize(arr, pos+1);
		
		arr[pos] = value;
		return arr;
	}
	
	public static final Object[] increaseSize(Object source[]) { return resize(source, 0, source.length, source.length + 1); }
	public static final Object[] increaseSize(Object source[], int increaseSizeBy) { return resize(source, 0, source.length, source.length + increaseSizeBy); }
	public static final Object[] resize(Object source[], int newSize) { return resize(source, 0, source.length, newSize); }
	
	public static final Object[] concatenate(Object array1[], Object array2[]) {
		if (array1 == null) 
			array1 = EMPTY_OBJECT_ARRAY;
		
		if (array2 == null)
			array2 = EMPTY_OBJECT_ARRAY;
		
		int newSize = array1.length + array2.length;
		Object concat[] = (Object[]) Array.newInstance(array1.getClass().getComponentType(), newSize);
		
		System.arraycopy(array1, 0, concat, 0, array1.length);
		System.arraycopy(array2, 0, concat, array1.length, array2.length);
		
		return concat;
	}

	public static final int[] concatenate(int array1[], int array2[]) {
		if (array1 == null) 
			array1 = EMPTY_INT_ARRAY;
		
		if (array2 == null)
			array2 = EMPTY_INT_ARRAY;
		
		int concat[] = new int[array1.length + array2.length];
		
		System.arraycopy(array1, 0, concat, 0, array1.length);
		System.arraycopy(array2, 0, concat, array1.length, array2.length);
		
		return concat;
	}

	public static final byte[] concatenate(byte array1[], byte array2[]) {
		if (array1 == null) 
			array1 = EMPTY_BYTE_ARRAY;
		
		if (array2 == null)
			array2 = EMPTY_BYTE_ARRAY;
		
		byte concat[] = new byte[array1.length + array2.length];
		
		System.arraycopy(array1, 0, concat, 0, array1.length);
		System.arraycopy(array2, 0, concat, array1.length, array2.length);
		
		return concat;
	}

	public static final Object[] resize(Object source[], int sourcePos, int sourceLength, int newSize) {
		Object result[] = (Object[]) Array.newInstance(source.getClass().getComponentType(), newSize);
		
		int copyLength = Math.min(newSize, sourceLength);
		System.arraycopy(source, sourcePos, result, 0, copyLength);
		return result;
	}
	
	public static final Iterator getNonDeletingIterator(final Collection collection) {
		return new Iterator() {
			Iterator iterator = getIterator(collection);

			public void remove() {
				throw new UnsupportedOperationException("remove() is not available for this iterator");
			}

			public boolean hasNext() {
				return iterator.hasNext();
			}

			public Object next() {
				return iterator.next();
			}
			
		};
	}
	
	public static final byte[] copy(byte values[]) {
		byte temp[] = new byte[values.length];
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	
	public static final boolean[] copy(boolean values[]) {
		boolean temp[] = new boolean[values.length];
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	
	public static final int[] copy(int values[]) {
		int temp[] = new int[values.length];
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	
	public static final long[] copy(long values[]) {
		long temp[] = new long[values.length];
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	
	public static final double[] copy(double values[]) {
		double temp[] = new double[values.length];
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	

	public static final Object[] copy(Object values[]) {
		Object temp[] = (Object[]) Array.newInstance(values.getClass().getComponentType(), values.length);
		System.arraycopy(values, 0, temp, 0, values.length);
		return temp;
	}
	
	public static final void removeFrom(Collection elements, Collection unwantedElements) {
		for (Iterator i = elements.iterator(); i.hasNext();) {
			Object element = (Object) i.next();
			
			elements.remove(element);
		}
	}
	
	public static final boolean contains(Object objects[], Object value) {
		for (int i=0; i < objects.length; i++) {
			Object object = objects[i];
			
			if (object != null) {
				if ((value != null) && object.equals(value))
					return true;
			} else if (value == null)
				return true;
		}
		
		return false;
	}
	

	
	
	
	public static int indexOf(Object arr[], Object value) {
		return indexOf(arr, value, 0);
	}

	public static int indexOf(Object arr[], Object value, int fromIndex) {
		for (int i=fromIndex; i < arr.length; i++) {
			if (arr[i] != null) {
				if (arr[i].equals(value))
					return i;
			} else if (value == null)
				return i;
		}
		
		return -1;
	}
	

	public static final void print(Collection elements) {
		print(System.out, "", elements);
	}

	public static final void print(String prefix, Collection elements) {
		print(System.out, prefix, elements);
	}
	
	public static final void print(PrintStream pout, Collection elements) {
		print(pout, "", elements);
	}

	public static final void print(PrintStream pout, String prefix, Collection elements) {
		for (Iterator i = elements.iterator(); i.hasNext();) {
			Object element = (Object) i.next();
			pout.println(prefix + element);
		}
	}
	
	public static final void printWithIndex(Collection elements) {
		printWithIndex(System.out, ":", elements);
	}

	public static final void printWithIndex(String prefix, Collection elements) {
		printWithIndex(System.out, prefix, elements);
	}
	
	public static final void printWithIndex(PrintStream pout, Collection elements) {
		printWithIndex(pout, ":", elements);
	}

	public static final void printWithIndex(PrintStream pout, String prefix, Collection elements) {
		int pos = 0;
		
		for (Iterator i = elements.iterator(); i.hasNext();) {
			Object element = (Object) i.next();
			pout.println((pos++) + " " + prefix + element);
		}
	}
	
	public static final void print(Object elements[]) {
		print(System.out, "", elements);
	}

	public static final void print(String prefix, Object elements[]) {
		print(System.out, prefix, elements);
	}
	
	public static final void print(PrintStream pout, Object elements[]) {
		print(pout, "", elements);
	}

	public static final void print(PrintStream pout, String prefix, Object elements[]) {
		for(int i=0; i < elements.length; i++) {
			pout.println(prefix + ": " + elements[i]);
		}
	}
	
	public static final void printWithIndex(Object elements[]) {
		printWithIndex(System.out, "", elements);
	}

	public static final void printWithIndex(String prefix, Object elements[]) {
		printWithIndex(System.out, prefix, elements);
	}
	
	public static final void printWithIndex(PrintStream pout, Object elements[]) {
		printWithIndex(pout, " ", elements);
	}

	public static final void printWithIndex(PrintStream pout, String prefix, Object elements[]) {
		for(int i=0; i < elements.length; i++) {
			pout.println(i + " " + prefix + ": " + elements[i]);
		}
	}
	
	public static final boolean inRange(Object arr[], int pos) {
		return ((pos >= 0) && (pos < arr.length));
	}
	
	public static final boolean isBothNull(Object obj1, Object obj2) {
		return (obj1 == null) && (obj2 == null);
	}

	
	public static final boolean isBothNonNull(Object obj1, Object obj2) {
		return (obj1 != null) && (obj2 != null);
	}
	
	public static final boolean equals(Object obj1, Object obj2) {
		if (obj1 == null) {
			return (obj2 == null);
		} else {
			if (obj2 != null)
				return obj1.equals(obj2);
			else 
				return false;
		}
	}
	
	public static final boolean equals(List list1, List list2) {
		if (list1.size() != list2.size())
			return false;
		
		return equals(list1.iterator(), list2.iterator());
	}
	
		
	public static final boolean equals(Iterator i, Iterator j) {
		for (; i.hasNext() && j.hasNext(); ) {
			if (!equals(i.next(), j.next()) )
				return false;;
		}
		
		return true;
	}
	
	public static final Object[] retypeArray(Object objs[], Class newType) {
		if (objs == null)
			return null;
		
		Object results[] = (Object[]) Array.newInstance(newType, objs.length);
		
		System.arraycopy(objs, 0, results, 0, objs.length);
		return results;
	}
	
	public static final boolean toBoolean(Object obj) {
		if (obj instanceof Boolean) {
			Boolean b = (Boolean) obj;
			return b.booleanValue();
		} else if (obj instanceof String) {
			String s = (String) obj;
			return s.equalsIgnoreCase("true");
		} else
			throw new IllegalArgumentException("Not convertable to a boolean");
	}
	
	public static final int toInt(Object obj) {
		if (obj instanceof Number) {
			Number number = (Integer) obj;
			return number.intValue();
		} else if (obj instanceof String) {
			String s = (String) obj;
			return Integer.parseInt(s);
		} else
			throw new IllegalArgumentException("Not convertable to a int");
	}
	
	public static final long toLong(Object obj) {
		if (obj instanceof Number) {
			Number number = (Integer) obj;
			return number.longValue();
		} else if (obj instanceof String) {
			String s = (String) obj;
			return Long.parseLong(s);
		} else
			throw new IllegalArgumentException("Not convertable to a long");
	}
	
	public static final double toDouble(Object obj) {
		if (obj instanceof Number) {
			Number number = (Integer) obj;
			return number.doubleValue();
		} else if (obj instanceof String) {
			String s = (String) obj;
			return Double.parseDouble(s);
		} else
			throw new IllegalArgumentException("Not convertable to a double");
	}

	public static final boolean[] toBoolean(Object objs[]) {
		boolean results[] = new boolean[objs.length];
		
		for (int i=0; i < objs.length; i++) 
			results[i] = toBoolean(objs[i]);
		
		return results;
	}
	
	public static final int[] toInts(Object objs[]) {
		int results[] = new int[objs.length];
		
		for (int i=0; i < objs.length; i++) 
			results[i] = toInt(objs[i]);
		
		return results;
	}
	
	public static final long[] toLongs(Object objs[]) {
		long results[] = new long[objs.length];
		
		for (int i=0; i < objs.length; i++) 
			results[i] = toLong(objs[i]);

		return results;
	}
	
	public static final double[] toDoubles(Object objs[]) {
		double results[] = new double[objs.length];
		
		for (int i=0; i < objs.length; i++) 
			results[i] = toDouble(objs[i]);

		
		return results;
	}
	
	
	public static final int[] remove(int orig[], int toRemove[]) {
		int newSize = 0;
		int temp[] = new int[orig.length];
		
		
		iLoop: for (int i=0; i < orig.length; i++) {
			for (int j=0; j < toRemove.length; j++) {
				if (orig[i] == toRemove[j]) 
					continue iLoop;
			}
			
			temp[newSize++] = orig[i];
		}
		
		int result[] = new int[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return result;
	}
	
	public static final int[] union(int arr1[], int arr2[]) {
		int temp[] = new int[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, temp, 0, arr1.length);
		int newSize = arr1.length;;
		
		iLoop: for (int i=0; i < arr2.length; i++) {
			for (int j=0; j < arr1.length; j++) {
				if (temp[j] == arr2[i]) {
					continue iLoop;
				}
			}
			
			temp[newSize++] = arr2[i];
		}
		
		int result[] = new int[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return unique(result);
	}
	
	public static final int[] intersection(int arr1[], int arr2[]) {
		int temp[] = new int[Math.max(arr1.length,arr2.length)];

		int newSize = 0;
		
		iLoop: for (int i=0; i < arr1.length; i++) {
			for (int j=0; j < arr2.length; j++) {
				if (arr1[i] == arr2[j]) {
					temp[newSize++] = arr2[i];
					continue iLoop;
				}
			}
		}
		
		int result[] = new int[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return unique(result);
	}
	
	public static final int[] unique(int arr[]) {
		int temp[] = new int[arr.length];
		int newSize = 0;
		
		iLoop: for (int i=0; i < arr.length; i++) {
			for (int j=0; j < newSize; j++) {
				if (arr[i] == temp[j])
					continue iLoop;
			}
			
			temp[newSize++] = arr[i];
		}
		
		int result[] = new int[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		return result;
	}
	
	public static final boolean isUnique(int arr[]) {
		for (int i=0; i < arr.length; i++) {
			for (int j=i; j < arr.length; j++) {
				if (arr[i] == arr[j])
					return false;
			}
		}
	
		return true;
	}
	
	public static final boolean equivalent(int arr1[], int arr2[]) {
		if (isBothNull(arr1, arr2))
			return true;
		else if (isBothNonNull(arr1, arr2)) {
			if (arr1.length != arr2.length)
				return false;
			else {
				for (int i=0; i < arr1.length; i++) {
					if (arr1[i] != arr2[i])
						return false;
				}
				
				return true;
			}
			
		} else
			return false;
	}
	
	
	public static final long[] remove(long orig[], long toRemove[]) {
		int newSize = 0;
		long temp[] = new long[orig.length];
		
		
		iLoop: for (int i=0; i < orig.length; i++) {
			for (int j=0; j < toRemove.length; j++) {
				if (orig[i] == toRemove[j]) 
					continue iLoop;
			}
			
			temp[newSize++] = orig[i];
		}
		
		long result[] = new long[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return result;
	}
	
	public static final long[] union(long arr1[], long arr2[]) {
		long temp[] = new long[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, temp, 0, arr1.length);
		int newSize = arr1.length;;
		
		iLoop: for (int i=0; i < arr2.length; i++) {
			for (int j=0; j < arr1.length; j++) {
				if (temp[j] == arr2[i]) {
					continue iLoop;
				}
			}
			
			temp[newSize++] = arr2[i];
		}
		
		long result[] = new long[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return unique(result);
	}
	
	public static final long[] intersection(long arr1[], long arr2[]) {
		long temp[] = new long[Math.max(arr1.length,arr2.length)];

		int newSize = 0;
		
		iLoop: for (int i=0; i < arr1.length; i++) {
			for (int j=0; j < arr2.length; j++) {
				if (arr1[i] == arr2[j]) {
					temp[newSize++] = arr2[i];
					continue iLoop;
				}
			}
		}
		
		long result[] = new long[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		
		return unique(result);
	}
	
	public static final long[] unique(long arr[]) {
		long temp[] = new long[arr.length];
		int newSize = 0;
		
		iLoop: for (int i=0; i < arr.length; i++) {
			for (int j=0; j < newSize; j++) {
				if (arr[i] == temp[j])
					continue iLoop;
			}
			
			temp[newSize++] = arr[i];
		}
		
		long result[] = new long[newSize];
		System.arraycopy(temp, 0, result, 0, newSize);
		return result;
	}
	
	public static final boolean isUnique(long arr[]) {
		for (int i=0; i < arr.length; i++) {
			for (int j=i; j < arr.length; j++) {
				if (arr[i] == arr[j])
					return false;
			}
		}
	
		return true;
	}
	
	public static final boolean equivalent(long arr1[], long arr2[]) {
		if (isBothNull(arr1, arr2))
			return true;
		else if (isBothNonNull(arr1, arr2)) {
			if (arr1.length != arr2.length)
				return false;
			else {
				for (int i=0; i < arr1.length; i++) {
					if (arr1[i] != arr2[i])
						return false;
				}
				
				return true;
			}
			
		} else
			return false;
	}
	
	public static final Class getPrototypeElementType(Collection values) {
		if (values.size() == 0)
			return null;
		
		for (Iterator i = values.iterator(); i.hasNext();) {
			Object value = (Object) i.next();
			
			if (value != null)
				return value.getClass();
		}
		
		return null;
	}


	
	public static int ensureIndex(Collection collection, int index) {
		return ensureSize(collection, index+1, null);
	}

	public static int ensureIndex(Collection collection, int index, Object filler) {
		return ensureSize(collection, index+1, filler);
	}
	
	public static int ensureSize(Collection collection, int size) {
		return ensureSize(collection, size, null);
	}

	public static final int ensureSize(Collection collection, int size, Object filler) {
		int oldSize = collection.size();

		for (int i=oldSize; i < size; i++)
			collection.add(filler);
		
		return oldSize;
	}
	
	public static final void resizingSet(ArrayList arrayList, int index, Object value) {
		resizingSet(arrayList, index, value, null);
	}

	public static final void resizingSet(ArrayList arrayList, int index, Object value, Object filler) {
		if (index >= arrayList.size())
			ensureSize(arrayList, index+1, filler);
		
		arrayList.set(index, value);
	}



}
