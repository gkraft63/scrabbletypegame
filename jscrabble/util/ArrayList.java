package jscrabble.util;

public class ArrayList {
    private Object[] elements;
    private int size;
    
    public Object clone() {
        ArrayList list = new ArrayList(elements.length);
        System.arraycopy(elements, 0, list.elements, 0, list.size = size);
        return list;
    }
    
    public ArrayList(int initialCapacity) {
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        this.elements = new Object[initialCapacity];
    }
    
    public ArrayList() {
        this(10);
    }
    
    
    public void trimToSize() {
        int oldCapacity = elements.length;
        if(size < oldCapacity) {
            Object oldData[] = elements;
            elements = new Object[size];
            System.arraycopy(oldData, 0, elements, 0, size);
        }
    }
    
    public void ensureCapacity(int minCapacity) {
        int oldCapacity = elements.length;
        if(minCapacity > oldCapacity) {
            Object oldData[] = elements;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if(newCapacity < minCapacity)
                newCapacity = minCapacity;
            elements = new Object[newCapacity];
            System.arraycopy(oldData, 0, elements, 0, size);
        }
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public Object get(int index) {
        RangeCheck(index);
        
        return elements[index];
    }
    
    public Object set(int index, Object element) {
        RangeCheck(index);
        
        Object oldValue = this.elements[index];
        this.elements[index] = element;
        return oldValue;
    }
    
    public void add(Object o) {
        ensureCapacity(size + 1);
        
        elements[size++] = o;
    }
    
    public void add(int index, Object element) {
        if(index > size || index < 0)
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);
        
        ensureCapacity(size+1);
        
        System.arraycopy(elements, index, elements, index + 1, size - index);
        this.elements[index] = element;
        size++;
    }
    
    public void clear() {
        // Let gc do its work
        for(int i = 0; i < size; i++)
            elements[i] = null;
        
        size = 0;
    }
    
    
    private void RangeCheck(int index) {
        if (index >= size || index < 0)
            throw new IndexOutOfBoundsException(
                    "Index: "+index+", Size: "+size);
    }
    
    
    public void sort(Comparator c) {
        Object[] aux = (Object[]) elements.clone();
        mergeSort(aux, elements, 0, size, c);
    }
    
    private static void mergeSort(Object[] src, Object[] dest, int low, int high, Comparator c) {
        int length = high - low;
        
        // Insertion sort on smallest arrays
        if(length < 7) {
            for(int i=low; i<high; i++)
                for(int j=i; j>low && c.compare(dest[j-1], dest[j])>0; j--) {
                    Object swap = dest[j];
                    dest[j] = dest[j-1];
                    dest[j-1] = swap;
                }
            return;
        }
        
        // Recursively sort halves of dest into src
        int mid = (low + high)/2;
        mergeSort(dest, src, low, mid, c);
        mergeSort(dest, src, mid, high, c);
        
        // If list is already sorted, just copy from src to dest.  This is an
        // optimization that results in faster sorts for nearly ordered lists.
        if(c.compare(src[mid-1], src[mid]) <= 0) {
            System.arraycopy(src, low, dest, low, length);
            return;
        }
        
        // Merge sorted halves (now in src) into dest
        for(int i = low, p = low, q = mid; i < high; i++) {
            if(q>=high || p<mid && c.compare(src[p], src[q])<=0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }
    
    public Object[] toArray(Object a[]) {
        if (a.length < size)
            a = (Object[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        
        System.arraycopy(elements, 0, a, 0, size);
        if(a.length > size)
            a[size] = null;
        
        return a;
    }
}







