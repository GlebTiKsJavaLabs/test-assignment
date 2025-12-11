/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */
package ua.kpi.comsys.test2.implementation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ua.kpi.comsys.test2.NumberList;

/**
 * @author Sukhoruchkin Hlib IA-34
 * 19th variant
 */
public class NumberListImpl implements NumberList {
    private static final int MAIN_BASE = 16;
    private static final int ADDITIONAL_BASE = 2;

    private static class Node {
        byte value;
        Node next;

        Node(byte value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private int base;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.base = MAIN_BASE;
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file
     *     - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        if (!file.exists() || !file.isFile()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (IOException e) {
            return;
        }
        String value = sb.toString().trim();
        if (value.isEmpty()) {
            return;
        }
        try {
            initFromDecimalString(value);
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value
     *     - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        String v = value.trim();
        if (v.isEmpty()) {
            return;
        }
        try {
            initFromDecimalString(v);
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file
     *     - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        }
        String dec = toDecimalString();
        try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
            pw.print(dec);
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Failed to write file", e);
        }
    }

    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 19;
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     * <p>
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger value = toBigInteger();
        int targetBase = (this.base == MAIN_BASE) ? ADDITIONAL_BASE : MAIN_BASE;
        return new NumberListImpl(value, targetBase);
    }

    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     * <p>
     * Does not impact the original list.
     *
     * @param arg
     *     - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) {
            throw new IllegalArgumentException("arg is null");
        }
        BigInteger a = this.toBigInteger();
        BigInteger b;
        if (arg instanceof NumberListImpl) {
            b = ((NumberListImpl) arg).toBigInteger();
        } else {
            b = listToBigInteger(arg, MAIN_BASE);
        }
        BigInteger result = a.and(b);
        return new NumberListImpl(result, this.base);
    }

    private static BigInteger listToBigInteger(List<Byte> digits, int base) {
        if (digits == null) {
            throw new IllegalArgumentException("digits is null");
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(base);
        for (Byte boxedDigit: digits) {
            if (boxedDigit == null) {
                throw new IllegalArgumentException("Null digit in list");
            }
            int d = boxedDigit & 0xFF;
            if (d < 0 || d >= base) {
                throw new IllegalArgumentException("Digit " + d + " is out of range for base " + base);
            }
            result = result.multiply(b).add(BigInteger.valueOf(d));
        }
        return result;
    }

    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        BigInteger value = toBigInteger();
        return value.toString(10);
    }

    @Override
    public String toString() {
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(size);
        Node current = head;
        for (int i = 0; i < size; i++) {
            int d = current.value & 0xFF;
            char ch = Character.forDigit(d, base);
            if (ch == -1) {
                throw new IllegalStateException("Invalid digit " + d + " for base " + base);
            }
            sb.append(Character.toUpperCase(ch));
            current = current.next;
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<?> other = (List<?>) o;
        if (other.size() != this.size) {
            return false;
        }
        Iterator<Byte> it1 = this.iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            Object e1 = it1.next();
            Object e2 = it2.next();
            if (e1 == null ? e2 != null : !e1.equals(e2)) {
                return false;
            }
        }
        return !it1.hasNext() && !it2.hasNext();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (size == 0) {
            return false;
        }
        if (!(o instanceof Byte)) {
            return false;
        }
        byte target = ((Byte) o).byteValue();
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.value == target) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int remaining = size;

            @Override
            public boolean hasNext() {
                return remaining > 0;
            }

            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                byte v = current.value;
                current = current.next;
                remaining--;
                return v;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node current = head;
        for (int i = 0; i < size; i++) {
            arr[i] = Byte.valueOf(current.value);
            current = current.next;
        }
        return arr;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(Byte e) {
        if (e == null) {
            throw new NullPointerException("null element");
        }
        byte digit = e.byteValue();
        checkDigitRange(digit);
        appendNode(digit);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (size == 0) {
            return false;
        }
        if (!(o instanceof Byte)) {
            return false;
        }
        byte target = ((Byte) o).byteValue();
        Node current = head;
        Node prev = tail;
        for (int i = 0; i < size; i++) {
            if (current.value == target) {
                removeNode(prev, current);
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        for (Object o: c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        boolean modified = false;
        for (Byte b: c) {
            add(b);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        boolean modified = false;
        int currentIndex = index;
        for (Byte b: c) {
            add(currentIndex++, b);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        if (size == 0) {
            return false;
        }
        boolean modified = false;
        Node current = head;
        Node prev = tail;
        int visited = 0;
        int originalSize = size;
        while (visited < originalSize && size > 0) {
            Node next = current.next;
            if (c.contains(Byte.valueOf(current.value))) {
                removeNode(prev, current);
                modified = true;
            } else {
                prev = current;
            }
            current = next;
            visited++;
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            throw new NullPointerException("collection is null");
        }
        if (size == 0) {
            return false;
        }
        boolean modified = false;
        Node current = head;
        Node prev = tail;
        int visited = 0;
        int originalSize = size;
        while (visited < originalSize && size > 0) {
            Node next = current.next;
            if (!c.contains(Byte.valueOf(current.value))) {
                removeNode(prev, current);
                modified = true;
            } else {
                prev = current;
            }
            current = next;
            visited++;
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    @Override
    public Byte get(int index) {
        Node node = nodeAt(index);
        return Byte.valueOf(node.value);
    }

    @Override
    public Byte set(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("null element");
        }
        byte digit = element.byteValue();
        checkDigitRange(digit);
        Node node = nodeAt(index);
        byte old = node.value;
        node.value = digit;
        return Byte.valueOf(old);
    }

    @Override
    public void add(int index, Byte element) {
        if (element == null) {
            throw new NullPointerException("null element");
        }
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        byte digit = element.byteValue();
        checkDigitRange(digit);
        if (size == 0) {
            appendNode(digit);
            return;
        }
        if (index == size) {
            appendNode(digit);
            return;
        }
        Node newNode = new Node(digit);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            tail.next = head;
            size++;
            return;
        }
        Node prev = nodeAt(index - 1);
        newNode.next = prev.next;
        prev.next = newNode;
        size++;
    }

    @Override
    public Byte remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == 1) {
            byte old = head.value;
            clear();
            return Byte.valueOf(old);
        }
        if (index == 0) {
            byte old = head.value;
            head = head.next;
            tail.next = head;
            size--;
            return Byte.valueOf(old);
        }
        Node prev = nodeAt(index - 1);
        Node toRemove = prev.next;
        byte old = toRemove.value;
        prev.next = toRemove.next;
        if (toRemove == tail) {
            tail = prev;
        }
        size--;
        return Byte.valueOf(old);
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        byte target = ((Byte) o).byteValue();
        Node current = head;
        for (int i = 0; i < size; i++) {
            if (current.value == target) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        byte target = ((Byte) o).byteValue();
        Node current = head;
        int last = -1;
        for (int i = 0; i < size; i++) {
            if (current.value == target) {
                last = i;
            }
            current = current.next;
        }
        return last;
    }

    private class ListItr implements ListIterator<Byte> {
        private int cursor;
        private int lastRet = -1;

        ListItr(int index) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            this.cursor = index;
        }

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public Byte next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            Byte elem = get(cursor);
            lastRet = cursor;
            cursor++;
            return elem;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public Byte previous() {
            if (!hasPrevious()) {
                throw new java.util.NoSuchElementException();
            }
            cursor--;
            Byte elem = get(cursor);
            lastRet = cursor;
            return elem;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            NumberListImpl.this.remove(lastRet);
            if (lastRet < cursor) {
                cursor--;
            }
            lastRet = -1;
        }

        @Override
        public void set(Byte e) {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }
            NumberListImpl.this.set(lastRet, e);
        }

        @Override
        public void add(Byte e) {
            NumberListImpl.this.add(cursor, e);
            cursor++;
            lastRet = -1;
        }
    }

    @Override
    public ListIterator<Byte> listIterator() {
        return new ListItr(0);
    }

    @Override
    public ListIterator<Byte> listIterator(int index) {
        return new ListItr(index);
    }

    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex + ", toIndex: " + toIndex + ", Size: " + size);
        }
        NumberListImpl result = new NumberListImpl();
        result.base = this.base;
        if (fromIndex == toIndex) {
            return result;
        }
        Node current = nodeAt(fromIndex);
        for (int i = fromIndex; i < toIndex; i++) {
            result.add(Byte.valueOf(current.value));
            current = current.next;
        }
        return result;
    }

    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        if (index1 == index2) {
            return true;
        }
        Node n1 = nodeAt(index1);
        Node n2 = nodeAt(index2);
        byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }

    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1; j++) {
                Node next = current.next;
                if ((current.value & 0xFF) > (next.value & 0xFF)) {
                    byte tmp = current.value;
                    current.value = next.value;
                    next.value = tmp;
                }
                current = next;
            }
        }
    }

    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            Node current = head;
            for (int j = 0; j < size - 1; j++) {
                Node next = current.next;
                if ((current.value & 0xFF) < (next.value & 0xFF)) {
                    byte tmp = current.value;
                    current.value = next.value;
                    next.value = tmp;
                }
                current = next;
            }
        }
    }

    @Override
    public void shiftLeft() {
        if (size <= 1) {
            return;
        }
        head = head.next;
        tail = tail.next;
    }

    @Override
    public void shiftRight() {
        if (size <= 1) {
            return;
        }
        Node prev = head;
        while (prev.next != tail) {
            prev = prev.next;
        }
        head = tail;
        tail = prev;
    }

    private void initFromDecimalString(String decimal) {
        decimal = decimal.trim();
        BigInteger bi;
        try {
            bi = new BigInteger(decimal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal number: " + decimal, e);
        }
        if (bi.signum() < 0) {
            throw new IllegalArgumentException("Negative numbers are not supported");
        }
        fromBigInteger(bi, this.base);
    }

    private NumberListImpl(BigInteger value, int base) {
        this();
        fromBigInteger(value, base);
    }

    private void fromBigInteger(BigInteger value, int base) {
        clear();
        this.base = base;
        if (value.equals(BigInteger.ZERO)) {
            appendNode((byte) 0);
            return;
        }
        String str = value.toString(base);
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int digit = Character.digit(ch, base);
            if (digit < 0) {
                throw new IllegalArgumentException("Invalid digit '" + ch + "' for base " + base);
            }
            appendNode((byte) digit);
        }
    }

    private BigInteger toBigInteger() {
        if (size == 0) {
            return BigInteger.ZERO;
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(this.base);
        Node current = head;
        for (int i = 0; i < size; i++) {
            int digit = current.value & 0xFF;
            result = result.multiply(b).add(BigInteger.valueOf(digit));
            current = current.next;
        }
        return result;
    }

    private void appendNode(byte digit) {
        checkDigitRange(digit);
        Node newNode = new Node(digit);
        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.next = newNode;
        } else {
            newNode.next = head;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    private void removeNode(Node prev, Node node) {
        if (size == 0 || node == null) {
            return;
        }
        if (size == 1) {
            head = null;
            tail = null;
            size = 0;
            return;
        }
        prev.next = node.next;
        if (node == head) {
            head = node.next;
        }
        if (node == tail) {
            tail = prev;
        }
        size--;
    }

    private Node nodeAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    private void checkDigitRange(byte digit) {
        if (digit < 0 || digit >= base) {
            throw new IllegalArgumentException("Digit " + digit + " is out of range for base " + base);
        }
    }
}
