package ch.ethz.netsec.fimeier.iptables.helper;

public class Pair<K, V> {

	private K key;
	private V value1;

	
	public K getKey() {
		return key;
	};
	public V getValue1() {
		return value1;
	};

	public Pair(K _key, V _value1) {
		key = _key;
		value1 = _value1;
	}
}