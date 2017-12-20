package framework;

public class List {
/**
 * Simple data object to hold a parameter name
 * and it's corresponding value, both as strings.
 */
	private String name = null;
	private String value = null;
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}
	public List(String aName, String aValue){
		this.name = aName;
		this.value = aValue;
	}
}
