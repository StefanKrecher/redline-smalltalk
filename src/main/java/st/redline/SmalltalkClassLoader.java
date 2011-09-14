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
package st.redline;

// TODO.JCL - make this classloader delegate to another so we can replace the delegate at runtime to reload all classes on fly.

import java.util.List;

public class SmalltalkClassLoader extends ClassLoader {

	private final CommandLine commandLine;

	public SmalltalkClassLoader(java.lang.ClassLoader classLoader, CommandLine commandLine) {
		super(classLoader);
		this.commandLine = commandLine;
	}

	public static SmalltalkClassLoader instance() {
		return (SmalltalkClassLoader) Thread.currentThread().getContextClassLoader();
	}

	protected void bootstrap() {
		loadProtoObject().bootstrap();
	}

	private ProtoObject loadProtoObject() {
		try {
			return ((ProtoObject) loadClass("st.redline.ProtoObject").newInstance());
		} catch (Exception e) {
			throw RedlineException.withCause(e);
		}
	}

	public CommandLine commandLine() {
		return commandLine;
	}

	public Class findClass(String className) throws ClassNotFoundException {
		return findClass0(className);
	}

	public Class findClass0(String className) throws ClassNotFoundException {
		// System.out.println("findClass() " + className);
		SourceFile sourceFile = findSource(className);
		if (sourceFile == null)
			return tryFindSystemClass(className);
		return classFrom(sourceFile, className);
	}

	private Class tryFindSystemClass(String className) {
		try {
			return findSystemClass(className);
		} catch (ClassNotFoundException e) {
			throw RedlineException.withCause(e);
		}
	}

	private Class classFrom(SourceFile sourceFile, String className) {
		byte[] classBytes = compile(sourceFile);
		Class clazz = defineClass(null, classBytes, 0, classBytes.length);
		return clazz;
	}

	public Class defineClass(byte[] classBytes) {
		return defineClass(null, classBytes, 0, classBytes.length);
	}

	private byte[] compile(SourceFile sourceFile) {
		return createCompiler(sourceFile).compile();
	}

	private Compiler createCompiler(SourceFile sourceFile) {
		return new Compiler(sourceFile);
	}

	private SourceFile findSource(String className) {
		return sourceFileFinder(className).findSourceFile();
	}

	private SourceFileFinder sourceFileFinder(String className) {
		return new SourceFileFinder(className);
	}

	public List<SourceFile> findSources(String paths) {
		return sourceFilesFinder(paths).findSourceFiles();
	}

	private SourceFilesFinder sourceFilesFinder(String paths) {
		return new SourceFilesFinder(paths);
	}
}
