/* Redline Smalltalk, Copyright (c) James C. Ladd. All rights reserved. See LICENSE in the root of this distribution */
package st.redline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static st.redline.RedlineTestHelper.assertIntegerEquals;
import static st.redline.RedlineTestHelper.integerClass;
import static st.redline.RedlineTestHelper.isSmalltalkFalse;
import static st.redline.RedlineTestHelper.isSmalltalkTrue;
import static st.redline.RedlineTestHelper.one;
import static st.redline.RedlineTestHelper.send;
import static st.redline.RedlineTestHelper.three;
import static st.redline.RedlineTestHelper.two;
import static st.redline.RedlineTestHelper.stic;

import java.lang.reflect.Field;
import java.util.Map;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Test;

import st.redline.compiler.Program;
import st.redline.compiler.SmalltalkLexer;
import st.redline.compiler.SmalltalkParser;

public class IntegerTest {

	@Test
	public void testProtoObject() throws Exception {
		assertEquals("st.redline.Integer", integerClass.toString());

		Class<?> clazz = Class.forName("st.redline.ProtoObject");
        Field dataField = clazz.getDeclaredField("data");
        dataField.setAccessible(true);
        ProtoObjectData poExampleClassData = (ProtoObjectData) dataField.get(integerClass);
		
		assertTrue(poExampleClassData.isClass());
		
		clazz = Class.forName("st.redline.ProtoObjectData$ClassData");
		Field methodsField = clazz.getDeclaredField("methods");
		methodsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<String, ProtoMethod> methods = (Map<String, ProtoMethod>) methodsField.get(poExampleClassData);

		assertTrue(methods.keySet().contains("+"));
		assertTrue(methods.keySet().contains("-"));
		assertTrue(methods.keySet().contains("<"));
		assertTrue(methods.keySet().contains("\\>"));
		assertTrue(methods.keySet().contains(">="));
		assertTrue(methods.keySet().contains("<="));
		assertTrue(methods.keySet().contains("="));

	}
	
	@Test
	public void testAdd() throws Exception {	
		assertIntegerEquals(3, send(one, "+", two));
	}

	@Test
	public void testSub() throws Exception {
		assertIntegerEquals(1, send(three, "-", two));
	}
	
	@Test
	public void testLessThan() throws Exception {
		assertTrue(isSmalltalkTrue(send(one, "<", two)));
		assertTrue(isSmalltalkFalse(send(two, "<", one)));
	}
	
	@Test
	public void testGreaterThan() throws Exception {
		assertTrue(isSmalltalkFalse(send(one, "\\>", two)));
		assertTrue(isSmalltalkTrue(send(two, "\\>", one)));
	}
	
	@Test
	public void testLessEquals() throws Exception {
		assertTrue(isSmalltalkTrue(send(one, "<=", two)));
		assertTrue(isSmalltalkTrue(send(one, "<=", one)));
	}
	
	@Test
	public void testGreaterEquals() throws Exception {
		assertTrue(isSmalltalkTrue(send(two, ">=", one)));
		assertTrue(isSmalltalkTrue(send(two, ">=", two)));
	}
	
	@Test
	public void testEquals() throws Exception {
		assertTrue(isSmalltalkTrue(send(two, "=", two)));
		assertTrue(isSmalltalkFalse(send(two, "=", three)));
	}

	
	@Test
	public void testExamine() throws Exception {
		Preprocessor preprocessor = new Preprocessor();
		Source source = preprocessor.parse(new SourceFile(new SourceFileFinder("st.redline.Integer").findSourceFile()));
//		String sourceCode = new SourceFileReader().read(new SourceFile(new SourceFileFinder("st.redline.Integer").findSourceFile()));
		SmalltalkLexer smalltalkLexer = new SmalltalkLexer(new ANTLRStringStream(source.source()));
		SmalltalkParser smalltalkParser = new SmalltalkParser(new CommonTokenStream(smalltalkLexer));
		Object o = smalltalkParser.array();
		
		System.out.println();
	}
	
}
