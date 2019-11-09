package ch.ethz.netsec.fimeier.iptables.helper;

public class Tripple<K, V, P> {
	
	private K key;
	private V value1;
	private P value2;

	
	public K getKey() {
		return key;
	};
	public V getValue1() {
		return value1;
	};
	public P getValue2() {
		return value2;
	};
	public Tripple(K _key, V _value1, P _value2) {
		key = _key;
		value1 = _value1;
		value2 = _value2;
	}
}