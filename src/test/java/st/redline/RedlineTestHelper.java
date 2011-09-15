/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

public class RedlineTestHelper {
	
	public static final Stic stic = new Stic(new CommandLine(new String[]{"-v"}));
	public static final ProtoObject integerClass = createIntegerClass();;
	public static final ProtoObject one = createIntegerInstance(1);
	public static final ProtoObject two = createIntegerInstance(2);
	public static final ProtoObject three = createIntegerInstance(3);

	public static ProtoObject createIntegerClass() {
		try {
			return stic.invoke("st.redline.Integer");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static ProtoObject createIntegerInstance() {
		try {
			return ProtoObject.primitiveSend(integerClass, "new", integerClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ProtoObject createIntegerInstance(Integer value) {
		ProtoObject po = createIntegerInstance();
		po.javaValue(new BigDecimal(value));
		return po;
	}
	
	public static ProtoObject send(ProtoObject integer1, String messageSelector, ProtoObject integer2) {
		ProtoObject result = ProtoObject.primitiveSend(integer1, integer2, messageSelector, integerClass);
		return result;
	}

	public static void assertIntegerEquals(Integer expected, ProtoObject result) {
		assertEquals(new BigDecimal(expected), (BigDecimal)result.javaValue());
	}
	
	public static Boolean isSmalltalkTrue(ProtoObject protoObject) {
		String value = protoObject.cls().toString();
		return value.equalsIgnoreCase("True");
	}

	public static Boolean isSmalltalkFalse(ProtoObject protoObject) {
		String value = protoObject.cls().toString();
		return value.equalsIgnoreCase("False");
	}
	

}
