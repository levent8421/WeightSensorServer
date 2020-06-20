package com.berrontech.dsensor.dataserver.weight.utils;

public class KeyValueList<K, V> {
    private K[] keys;
    private V[] values;

    public K[] getKeys() {
        return keys;
    }

    public V[] getValues() {
        return values;
    }

    public KeyValueList(K[] keys, V[] values) {
        this.keys = keys;
        this.values = values;
    }

    public K toKey(V value)
    {
        int idx = valueIndex(value);
        return getKey(idx);
    }

    public V toValue(K key)
    {
        int idx = keyIndex(key);
        return getValue(idx);
    }

    public K getKey(int idx)
    {
        if (idx >= 0 && idx < getKeys().length) {
            return getKeys()[idx];
        }
        return null;
    }

    public V getValue(int idx)
    {
        if (idx >= 0 && idx < getValues().length) {
            return getValues()[idx];
        }
        return null;
    }

    public int keyIndex(K key)
    {
        if (getKeys() == null ) {
            return -1;
        }
        for (int idx = 0; idx < getKeys().length; idx++)
        {
            if (key.equals(getKeys()[idx])) {
                return idx;
            }
        }
        return -1;
    }

    public int valueIndex(V value)
    {
        if (getValues() == null ) {
            return -1;
        }
        for (int idx = 0; idx < getValues().length; idx++)
        {
            if (value.equals(getValues()[idx])) {
                return idx;
            }
        }
        return -1;
    }
}
