package com.upic.classLoader;

/**
 * @author DTZ
 */
public class UpicClassLoader extends ClassLoader {
	private static UpicClassLoader loader;
	private static final Object OBJ=new Object();
	
	public static UpicClassLoader getUpicClassLoader(){
		if(loader==null){
			synchronized (OBJ) {
				if(loader==null){
					loader=new UpicClassLoader();
				}
			}
		}
		return loader;
	}
	private UpicClassLoader() {
		super(UpicClassLoader.class.getClassLoader());
	}

	/**
	 * 类似于jsp 属于热部署
	 * @param classByte
	 * @param resolve
	 * @param name
	 * @return
	 */
	public Class<?> loadByte(byte[] classByte,boolean resolve,String name) {
		Class<?> clazz = null;
        clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve)
                resolveClass(clazz);
            return (clazz);
        }
		return defineClass(null, classByte, 0, classByte.length);
	}
	
//	public Class<?> loadByte(byte[] classByte) {
//		return defineClass(null, classByte, 0, classByte.length);
//	}
}
