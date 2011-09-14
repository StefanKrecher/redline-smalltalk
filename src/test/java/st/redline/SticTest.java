package st.redline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SticTest {

	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testClassAndInstanceMethods() throws Exception {
		CommandLine commandLine = new CommandLine(new String[] { "-v",
				"st.redline.Example" });
		Stic stic = new Stic(commandLine);
		ProtoObject poExampleClass = stic.invoke((String) commandLine.arguments().get(0));

		assertEquals("st.redline.Example", poExampleClass.toString());

		ProtoObjectData poExampleClassData = poExampleClass.getData();

		assertTrue(poExampleClassData.isClass());

		ProtoMethod[] methods = poExampleClassData.methods();
		List<String> methodNames = new ArrayList<String>();
		for (ProtoMethod method : methods) {
			methodNames.add(method.toString().substring(method.toString().indexOf("$") + 1,
					method.toString().indexOf("@")));
		}

		assertTrue(methodNames.contains("testTrue"));
		assertTrue(methodNames.contains("testFalse"));
		// assertTrue(methodNames.contains("yourself")); // fails!

		ProtoObject poExampleInstance = ProtoObject.primitiveSend(poExampleClass, "new", poExampleClass);

		assertFalse(poExampleInstance.getData().isClass());
		
		ProtoObject poTrue = ProtoObject.primitiveSend(poExampleInstance, "testTrue", poExampleInstance);
		assertEquals("True", poTrue.cls().toString());

		ProtoObject poFalse = ProtoObject.primitiveSend(poExampleInstance, "testFalse", poExampleInstance);
		assertEquals("False", poFalse.cls().toString());

		ProtoObject poYourself = ProtoObject.primitiveSend(poExampleClass, "yourself", poExampleClass);

		assertTrue(poYourself.isClass());
		assertEquals("st.redline.Example", poYourself.toString());
		
		System.out.println(poExampleClass);
	}
	
	@Test
	@Ignore
	public void testDummy() {

	}
}
