package com.upic.acm.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import com.upic.acm.deal.DealCode.DealEnum;
/**
 * 
 * @author DTZ
 *
 */
public class UpicJdkCompiler {
	private final static Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();
	// JDK编译器
	private final JavaCompiler compiler;
	// 诊断侦听器；如果为 null，则使用编译器的默认方法报告诊断信息
	private final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
	// 自定义文件管理器；如果为 null，则使用编译器的标准文件管理器
	private final JavaFileManagerImpl javaFileManager;
	// 版本参数
	private volatile List<String> options;

	// 自定义类加载器
	private final InnerUpicClassLoader classLoader;
	private static final String EXT_JAVA = ".java";

	public UpicJdkCompiler() {
		// 初始化加载当前环境编译器，注意：需要再../bin/jre下面加入tool.jar
		compiler = ToolProvider.getSystemJavaCompiler();
		// 设置目标版本
		options = new ArrayList<String>();
		options.add("-target");
		options.add("1.8");
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		classLoader = new InnerUpicClassLoader(loader);
		// 为此工具获取一个标准文件管理器实现的新实例
		StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
		// 装饰设计模式
		javaFileManager = new JavaFileManagerImpl(manager, classLoader);
	}

	private static URI returnUri(String baseName) {
		try {
			return new URI(baseName);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object doCompile(String name, String sourceCode, DealEnum compilerEnum) throws Throwable {
		int i = name.lastIndexOf('.');
		// 获得包名
		String packageName = i < 0 ? "" : name.substring(0, i);
		// 获得类名
		String className = i < 0 ? name : name.substring(i + 1);
		
		// 抛出类名不符合的异常
//		if (!className.equals(mainClassName)) {
//			throw new Exception("类名有误，必须为Main");
//		}
		
		JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
		javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, className + EXT_JAVA,
				javaFileObject);
		// 检查版本等是否正确。 使用给定组件和参数创建编译任务的 future。
		Boolean result = compiler
				.getTask(null, javaFileManager, diagnosticCollector, options, null, Arrays.asList(javaFileObject))
				.call();
		if (result == null || !result) {
			throw new IllegalStateException(
					"Compilation failed. class: " + name + ", diagnostics: " + diagnosticCollector);
		}
		Object obj =null;
		switch (compilerEnum) {
		case BYTE:
			obj = doCompile0(name);
			break;

		case CLAZZ:
			obj = doCompile1(name);
			break;
		default:
			break;
		}
		return obj;
	}

	private byte[] doCompile0(String name) throws Throwable {
		return classes.get(name) != null ? ((JavaFileObjectImpl) classes.get(name)).getByteCode() : null;
	}

	private Class<?> doCompile1(String name) throws ClassNotFoundException {
		return classLoader.loadClass(name);
	}

	/**
	 * 存放字节数组流（代码）和代码文件
	 * 
	 * @author DTZ
	 *
	 */
	private static final class JavaFileObjectImpl extends SimpleJavaFileObject {

		private ByteArrayOutputStream bytecode;

		private final CharSequence source;

		public JavaFileObjectImpl(final String baseName, final CharSequence source) {
			super(returnUri(baseName + EXT_JAVA), Kind.SOURCE);
			this.source = source;
		}

		JavaFileObjectImpl(final String name, final Kind kind) {
			super(returnUri(name), kind);
			source = null;
		}

		/**
		 * 父类加载此方法 获取source
		 */
		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
			if (source == null) {
				throw new UnsupportedOperationException("source == null");
			}
			return source;
		}

		@Override
		public InputStream openInputStream() {
			return new ByteArrayInputStream(getByteCode());
		}

		@Override
		public OutputStream openOutputStream() {
			return bytecode = new ByteArrayOutputStream();
		}

		public byte[] getByteCode() {
			return bytecode.toByteArray();
		}
	}

	/**
	 * 自定义内部类类加载器
	 * 
	 * @author DTZ
	 *
	 */
	private final class InnerUpicClassLoader extends ClassLoader {

//		private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

		InnerUpicClassLoader(final ClassLoader parentClassLoader) {
			super(parentClassLoader);
		}

		Collection<JavaFileObject> files() {
			return Collections.unmodifiableCollection(classes.values());
		}

		@Override
		protected Class<?> findClass(final String qualifiedClassName) throws ClassNotFoundException {
			JavaFileObject file = classes.get(qualifiedClassName);
			if (file != null) {
				byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
				return defineClass(qualifiedClassName, bytes, 0, bytes.length);
			}
			try {
				return Thread.currentThread().getContextClassLoader().loadClass(qualifiedClassName);
			} catch (ClassNotFoundException nf) {
				return super.findClass(qualifiedClassName);
			}
		}

		void add(final String qualifiedClassName, final JavaFileObject javaFile) {
			classes.put(qualifiedClassName, javaFile);
		}

		@Override
		protected synchronized Class<?> loadClass(final String name, final boolean resolve)
				throws ClassNotFoundException {
			
			return super.loadClass(name, resolve);
		}

		@Override
		public InputStream getResourceAsStream(final String name) {
			if (name.endsWith(".class")) {
				String qualifiedClassName = name.substring(0, name.length() - ".class".length()).replace('/', '.');
				JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(qualifiedClassName);
				if (file != null) {
					return new ByteArrayInputStream(file.getByteCode());
				}
			}
			return super.getResourceAsStream(name);
		}
	}

	private static final class JavaFileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {
		// 自定义类加载器
		private final InnerUpicClassLoader classLoader;
		// 根据location、packageName、relativeName解析程URI存放再map中，特通过location、packageName、relativeName解析程URI获取JavaFileObject
		private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

		public JavaFileManagerImpl(JavaFileManager fileManager, InnerUpicClassLoader classLoader) {
			super(fileManager);
			this.classLoader = classLoader;
		}

		@Override
		public FileObject getFileForInput(Location location, String packageName, String relativeName)
				throws IOException {
			FileObject o = fileObjects.get(uri(location, packageName, relativeName));
			if (o != null)
				return o;
			return super.getFileForInput(location, packageName, relativeName);
		}

		public void putFileForInput(StandardLocation location, String packageName, String relativeName,
				JavaFileObject file) {
			fileObjects.put(uri(location, packageName, relativeName), file);
		}

		private URI uri(Location location, String packageName, String relativeName) {
			return returnUri(location.getName() + '/' + packageName + '/' + relativeName);
		}

		/**
		 * 把要编译的文件内容存放到类加载器当中 此文件管理器可以将 sibling
		 * 视为对放置输出位置的提示（可选）。没有指定此提示的确切语义。除非提供了类文件输出目录，否则 Sun 的编译器（例如
		 * javac）将把类文件放到与原始源文件相同的目录下。要实现此行为，调用此方法时 javac 可以提供原始源文件作为 sibling。
		 * 
		 * 编译器自动会加载此方法获取文件位置
		 */
		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, JavaFileObject.Kind kind,
				FileObject outputFile) throws IOException {
			JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
			classes.put(qualifiedName, file);
			return file;
		}

		@Override
		public ClassLoader getClassLoader(JavaFileManager.Location location) {
			return fileManager.getClassLoader(location);
		}

		@Override
		public String inferBinaryName(Location loc, JavaFileObject file) {
			if (file instanceof JavaFileObjectImpl)
				return file.getName();
			return super.inferBinaryName(loc, file);
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds,
				boolean recurse) throws IOException {
			return fileManager.list(location, packageName, kinds, recurse);
		}
	}


}