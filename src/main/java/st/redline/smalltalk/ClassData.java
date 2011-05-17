/*
Redline Smalltalk is licensed under the MIT License

Redline Smalltalk Copyright (c) 2010 James C. Ladd

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Please see DEVELOPER-CERTIFICATE-OF-ORIGIN if you wish to contribute a patch to Redline Smalltalk.
*/
package st.redline.smalltalk;

import java.util.Hashtable;
import java.util.Map;

public class ClassData extends RData {

	private final Map<String, RMethod> methodDictionary;

	private String name;
	private boolean bootstrapped;
	private RObject category;
	private Map<String, String> instanceVariables;
	private Map<String, String> classVariables;
	private Map<String, String> classInstanceVariables;
	private Map<String, String> poolDictionaries;

	public ClassData(Map<String, RMethod> methodDictionary, RObject container) {
		super(container);
		this.methodDictionary = methodDictionary;
	}

	public boolean isClass() {
		return true;
	}

	public boolean isBootstrapped() {
		return bootstrapped;
	}

	public void bootstrapped(boolean bootstrapped) {
		this.bootstrapped = bootstrapped;
	}

	public String primitiveName() {
		return name;
	}

	public void primitiveName(String name) {
		this.name = name;
	}

	public boolean hasInstanceVariableNamed(String key) {
		return instanceVariables != null && instanceVariables.containsKey(key);
	}

	public boolean hasClassVariableNamed(String key) {
		return classVariables != null && classVariables.containsKey(key);
	}

	public boolean hasClassInstanceVariableNamed(String key) {
		return classInstanceVariables != null && classInstanceVariables.containsKey(key);
	}

	public boolean hasPoolDictionaryNamed(String key) {
		return poolDictionaries != null && poolDictionaries.containsKey(key);
	}

	public void primitiveAddInstanceVariableNamed(RObject variable) {
		String value = variable.data.primitiveValue().toString();
		RObject superclass = this.container.oop[RObject.SUPERCLASS_OFFSET];
		while (superclass != null) {
			if (superclass.data.hasInstanceVariableNamed(value))
				throw new IllegalStateException("Superclass " + superclass + " already defined instance variable '" + value + "'.");
			superclass = superclass.oop[RObject.SUPERCLASS_OFFSET];
		}
		if (instanceVariables == null)
			instanceVariables = new Hashtable<String, String>();
		instanceVariables.put(value, value);
	}

	public void primitiveAddClassVariableNamed(RObject variable) {
		String value = variable.data.primitiveValue().toString();
		RObject superclass = this.container.oop[RObject.SUPERCLASS_OFFSET];
		while (superclass != null) {
			if (superclass.data.hasClassVariableNamed(value))
				throw new IllegalStateException("Superclass " + superclass + " already defined class variable '" + value + "'.");
			superclass = superclass.oop[RObject.SUPERCLASS_OFFSET];
		}
		if (classVariables == null)
			classVariables = new Hashtable<String, String>();
		classVariables.put(value, value);
	}

	public void primitiveAddClassInstanceVariableNamed(RObject variable) {
		String value = variable.data.primitiveValue().toString();
		RObject superclass = this.container.oop[RObject.SUPERCLASS_OFFSET];
		while (superclass != null) {
			if (superclass.data.hasClassInstanceVariableNamed(value))
				throw new IllegalStateException("Superclass " + superclass + " already defined class instance variable '" + value + "'.");
			superclass = superclass.oop[RObject.SUPERCLASS_OFFSET];
		}
		if (classInstanceVariables == null)
			classInstanceVariables = new Hashtable<String, String>();
		classInstanceVariables.put(value, value);
	}

	public void primitiveAddPoolNamed(RObject pool) {
		String value = pool.data.primitiveValue().toString();
		RObject superclass = this.container.oop[RObject.SUPERCLASS_OFFSET];
		while (superclass != null) {
			if (superclass.data.hasPoolDictionaryNamed(value))
				throw new IllegalStateException("Superclass " + superclass + " already defined pool variable '" + value + "'.");
			superclass = superclass.oop[RObject.SUPERCLASS_OFFSET];
		}
		if (poolDictionaries == null)
			poolDictionaries = new Hashtable<String, String>();
		poolDictionaries.put(value, value);
	}

	public void primitiveCategory(RObject category) {
		this.category = category;
	}

	public Object primitiveValue() {
		throw classesDontHavePrimitiveValues();
	}

	public void primitiveValue(Object value) {
		throw classesDontHavePrimitiveValues();
	}

	public RMethod methodAt(String selector) {
		return methodDictionary.get(selector);
	}

	public void methodAtPut(String selector, RMethod method) {
		methodDictionary.put(selector, method);
	}

	private IllegalStateException classesDontHavePrimitiveValues() {
		return new IllegalStateException("Class objects don't have primitive values.");
	}
}
