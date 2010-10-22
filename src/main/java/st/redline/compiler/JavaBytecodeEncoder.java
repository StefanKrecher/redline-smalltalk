package st.redline.compiler;

import org.objectweb.asm.*;

import java.util.List;

public class JavaBytecodeEncoder extends ClassLoader implements Opcodes {

	public static final String DEFAULT_FILE_PACKAGE = "st/redline/";
	public static final String DEFAULT_JAVA_PACKAGE = "st.redline.";
	private static final String[] METHOD_SIGNATURE = {
		"()Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;"
	};

	private static final String[] SEND_SIGNATURE = {
		"(Ljava/lang/String;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;",
		"(Ljava/lang/String;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;Lst/redline/ProtoObject;)Lst/redline/ProtoObject;"
	};

	private final ClassWriter classWriter;
	private final ClassWriter classClassWriter;
	private String subclass;
	private String qualifiedSubclass;
	private String classQualifiedSubclass;
	private String qualifiedSuperclass;
	private String classQualifiedSuperclass;
	private String sourcePath;

	public JavaBytecodeEncoder() {
		classWriter = new TracingClassWriter(ClassWriter.COMPUTE_MAXS);
		classClassWriter = new TracingClassWriter(ClassWriter.COMPUTE_MAXS);
	}

	public void defineClass(ClassDefinition classDefinition, String sourcePath) {
		this.subclass = classDefinition.subclass();
		this.sourcePath = sourcePath;
		qualifiedSubclass = DEFAULT_FILE_PACKAGE + subclass;
		classQualifiedSubclass = DEFAULT_FILE_PACKAGE + subclass + "$mClass";
		qualifiedSuperclass = DEFAULT_FILE_PACKAGE + classDefinition.superclass();
		if (subclass.equals("Object"))
			classQualifiedSuperclass = DEFAULT_FILE_PACKAGE + "Class";
		else
			classQualifiedSuperclass = DEFAULT_FILE_PACKAGE + classDefinition.superclass() + "$mClass";
		defineClass(sourcePath);
		defineDefaultConstructor(classDefinition.lineNumber());
		defineClassRegistration();
		defineHelperMethods();
	}

	private void defineHelperMethods() {
		defineMethodFinder(classWriter, qualifiedSubclass, qualifiedSuperclass);
		defineMethodFinder(classClassWriter, classQualifiedSubclass, classQualifiedSuperclass);
	}

	private void defineMethodFinder(ClassWriter classWriter, String qualifiedSubclass, String qualifiedSuperclass) {
		MethodVisitor mv = classWriter.visitMethod(ACC_PROTECTED, "findMethod", "(Ljava/lang/String;)Ljava/lang/reflect/Method;", null, null);
		mv.visitCode();
		emitMessage(mv, "findMethod: in " + qualifiedSubclass);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKESPECIAL, "st/redline/ProtoObject", "findMethod", "(Ljava/lang/String;)Ljava/lang/reflect/Method;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private void defineClassRegistration() {
		MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		emitMessage(mv, "Registering: " + subclass + " " + classQualifiedSubclass);
		mv.visitLdcInsn(subclass);
		mv.visitTypeInsn(NEW, classQualifiedSubclass);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, classQualifiedSubclass, "<init>", "()V");
		mv.visitMethodInsn(INVOKESTATIC, "st/redline/Smalltalk", "register", "(Ljava/lang/String;Lst/redline/ProtoObject;)V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 0);
		mv.visitEnd();
	}

	private void defineClass(String sourcePath) {
		defineClass(sourcePath, classWriter, qualifiedSubclass, qualifiedSuperclass);
		defineClass(sourcePath, classClassWriter, classQualifiedSubclass, classQualifiedSuperclass);
	}

	private void defineClass(String sourcePath, ClassWriter classWriter, String subclass, String superclass) {
		classWriter.visit(V1_5, ACC_PUBLIC + ACC_SUPER, subclass, null, superclass, null);
		classWriter.visitSource(sourcePath, null);
		classWriter.visitInnerClass(qualifiedSubclass + "$mClass", qualifiedSubclass, "mClass", ACC_PUBLIC + ACC_STATIC);
	}

	public byte[][] definedClassBytes() {
		classWriter.visitEnd();
		classClassWriter.visitEnd();
		return new byte[][] { classWriter.toByteArray(), classClassWriter.toByteArray() };
	}

	public Class definedClass() {
		byte[][] code = definedClassBytes();
		defineClass(DEFAULT_JAVA_PACKAGE + subclass + "$mClass", code[1], 0, code[1].length);
		return defineClass(DEFAULT_JAVA_PACKAGE + subclass, code[0], 0, code[0].length);
	}

	private void defineClassConstructor(ClassDefinition classDefinition) {
		MethodVisitor mv = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
//		defineClassInitialization(mv, classDefinition);
//		emitUnarySend(mv, classDefinition.unarySend());
//		emitKeywordSend(mv, classDefinition.keywordSends());
//		mv.visitInsn(POP);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private void defineDefaultConstructor(int lineNumber) {
		defineDefaultConstructor(lineNumber, classWriter, qualifiedSuperclass, qualifiedSubclass);
		defineDefaultConstructor(lineNumber, classClassWriter, classQualifiedSuperclass, classQualifiedSubclass);
	}

	private void defineDefaultConstructor(int lineNumber, ClassWriter classWriter, String superclass, String subclass) {
		MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		emitMessage(mv, "Constructing: " + subclass);
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(lineNumber, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclass, "<init>", "()V");
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + subclass + ";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private void emitMessage(MethodVisitor mv, String message) {
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(1, l0);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitLdcInsn(message);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
	}

	private void emitKeywordSend(MethodVisitor mv, List<KeywordSend> keywordSends) {
		emitKeywordSendSignature(mv, keywordSends);
		emitKeywordSendArguments(mv, keywordSends);
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(keywordSends.get(0).keyword().beginLine, l0);
		mv.visitMethodInsn(INVOKEVIRTUAL, "st/redline/ProtoObject", "prim$end", SEND_SIGNATURE[keywordSends.size()]);
	}

	private void emitKeywordSendArguments(MethodVisitor mv, List<KeywordSend> keywordSends) {
		for (KeywordSend keywordSend : keywordSends)
			for (MessageSend messageSend : keywordSend.arguments())
				emitMessageSend(mv, messageSend);
	}

	private void emitKeywordSendSignature(MethodVisitor mv, List<KeywordSend> keywordSends) {
		StringBuffer keywords = new StringBuffer(128);
		for (KeywordSend keywordSend : keywordSends)
			keywords.append(keywordSend.keyword().toString());
		mv.visitLdcInsn(keywords.toString());
	}

	private void emitMessageSend(MethodVisitor mv, MessageSend messageSend) {
		if (messageSend instanceof UnarySend) {
			emitUnarySend(mv, (UnarySend) messageSend);
		} else {
			throw new RuntimeException("Need to handle MessageSend of type: " + messageSend.getClass());
		}
	}

	private void emitUnarySend(MethodVisitor mv, UnarySend unarySend) {
		emitPrimary(mv, unarySend.primary());
		for (Token selector : unarySend.selectors())
			emitUnaryCall(mv, selector);
	}

	private void emitUnaryCall(MethodVisitor mv, Token selector) {
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(selector.beginLine, l0);
		mv.visitLdcInsn(selector.toString());
		mv.visitMethodInsn(INVOKEVIRTUAL, "st/redline/ProtoObject", "prim$end", SEND_SIGNATURE[0]);
	}

	private void emitPrimary(MethodVisitor mv, Primary primary) {
		if (primary instanceof PrimaryVariable) {
			emitPrimary(mv, (PrimaryVariable) primary);
		} else if (primary instanceof PrimaryLiteral) {
			emitPrimary(mv, (PrimaryLiteral) primary);
		} else {
			throw new RuntimeException("Need to handle PRIMARY of type: " + primary.getClass());
		}
	}

	private void emitPrimary(MethodVisitor mv, PrimaryVariable primaryVariable) {
		Variable variable = primaryVariable.variable();
		if (variable.isClass()) {
			emitClassLoopkup(mv, variable);
		} else {
			throw new RuntimeException("Need to handle other types of VARIABLE: " + variable.getClass() + " " + variable.toString());
		}
	}

	private void emitPrimary(MethodVisitor mv, PrimaryLiteral primaryLiteral) {
		Literal literal = primaryLiteral.literal();
		if (literal instanceof SymbolLiteral) {
			emitLiteral(mv, (SymbolLiteral) literal);
		} else if (literal instanceof StringLiteral) {
			emitLiteral(mv, (StringLiteral) literal);
		} else {
			throw new RuntimeException("Need to handle other types of LITERAL: " + literal.getClass());
		}
	}

	private void emitLiteral(MethodVisitor mv, SymbolLiteral symbolLiteral) {
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(symbolLiteral.lineNumber(), l0);
		mv.visitLdcInsn(symbolLiteral.toString());
		mv.visitInsn(ICONST_1);
		mv.visitMethodInsn(INVOKESTATIC, "st/redline/ProtoObject", "objectFromPrimitive", "(Ljava/lang/String;Z)Lst/redline/ProtoObject;");
	}

	private void emitLiteral(MethodVisitor mv, StringLiteral stringLiteral) {
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(stringLiteral.lineNumber(), l0);
		mv.visitLdcInsn(stringLiteral.toString());
		mv.visitInsn(ICONST_0);
		mv.visitMethodInsn(INVOKESTATIC, "st/redline/ProtoObject", "objectFromPrimitive", "(Ljava/lang/String;Z)Lst/redline/ProtoObject;");
	}

	private void emitClassLoopkup(MethodVisitor mv, Variable variable) {
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(variable.lineNumber(), l0);
		mv.visitLdcInsn(variable.toString());
		mv.visitMethodInsn(INVOKESTATIC, "st/redline/Smalltalk", "classNamed", "(Ljava/lang/String;)Lst/redline/ProtoObject;");
	}

	public void defineMethods(String sourcePath, int classDefinitionLineNumber, List<Method> methods, boolean classMethods) {
		for (Method method : methods)
			defineMethod(sourcePath, classDefinitionLineNumber, method, classMethods);
	}

	private void defineMethod(String sourcePath, int classDefinitionLineNumber, Method method, boolean classMethods) {
		MessagePattern messagePattern = method.messagePattern();
		String methodName = messagePattern.pattern();
		MethodVisitor mv;
		if (classMethods)
			mv = classClassWriter.visitMethod(ACC_PUBLIC, methodName, METHOD_SIGNATURE[messagePattern.argumentCount()], null, null);
		else
			mv = classWriter.visitMethod(ACC_PUBLIC, methodName, METHOD_SIGNATURE[messagePattern.argumentCount()], null, null);
		mv.visitCode();
		if (method.pragmas()[0] != null)
			emitPragmas(mv, method.pragmas()[0]);
		emitTemporaries(mv, method.temporaries());
		if (method.pragmas()[1] != null)
			emitPragmas(mv, method.pragmas()[1]);
		emitStatements(mv, method.statements());
		// return this, although we should never reach these statements.
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private void emitStatements(MethodVisitor mv, Statements statements) {
		for (Expression expression : statements.expressions())
			emitExpression(mv, expression);
	}

	private void emitExpression(MethodVisitor mv, Expression expression) {
		System.out.println("TODO - Expression");
	}

	private void emitTemporaries(MethodVisitor mv, List<Variable> variables) {
		System.out.println("TODO - Temporaries");
	}

	private void emitPragmas(MethodVisitor mv, Pragma pragma) {
		System.out.println("TODO - Pragmas");
	}

	private void emitNumber(MethodVisitor mv, int number) {
		switch (number) {
			case 0:
				mv.visitInsn(ICONST_0);
				break;
			case 1:
				mv.visitInsn(ICONST_1);
				break;
			case 2:
				mv.visitInsn(ICONST_2);
				break;
			case 3:
				mv.visitInsn(ICONST_3);
				break;
			case 4:
				mv.visitInsn(ICONST_4);
				break;
			case 5:
				mv.visitInsn(ICONST_5);
				break;
			default:
				if (number < 128)
					mv.visitIntInsn(BIPUSH, number);
				else
					mv.visitIntInsn(SIPUSH, number);
		}
	}
}