package cn.zthz.tool.common;

import java.io.Serializable;
import java.util.Map;

public class Tuple<K, V> implements Serializable {
	private static final long serialVersionUID = -4753490983319380188L;
	public K key;
	public V value;

	public Tuple() {
		super();
	}

	public Tuple(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public String join(String w){
		return key+w+value;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			Tuple<K, V> target = (Tuple<K, V>) obj;
			return target.key.equals(target.key) && target.value.equals(value);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return key.hashCode() + value.hashCode();
	}

	public Map.Entry<K, V> toEntry() {
		final Tuple<K, V> self = this;
		return new Map.Entry<K, V>() {

			@Override
			public K getKey() {
				return self.key;
			}

			@Override
			public V getValue() {
				return self.value;
			}

			@Override
			public V setValue(V value) {
				return self.value = value;
			}
		};
	}

	public static <K , V> Tuple<K , V> trans(Map.Entry<K, V> entry) {
		return new Tuple<K, V>(entry.getKey(), entry.getValue());
	}
	public void fromEntry(Map.Entry<K, V> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	/**
	 * @return the key
	 */
	public K getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(K key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public V getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(V value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return key.toString() +":"+value.toString();
	}

}
