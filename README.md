# 项目说明
    说明：这个工具包主要解决编写对java代码的测试（ACM），只要提供用户代码以及测试数据，就可以返回相应的正确性，方便了开发。
    文档只是简单的介绍，可以下载看源码和调试，目前还只是0.1.0版本
    
- **类结构**
- **CodeCompiler**
- **DealCode（核心业务处理）**
-  **AbstraceDealCodeCenter**
-  **DealCodeCenter**
-  **DealResult**
-  **存在的弊端**


-------------------

##结构

 >接口:
 
   CodeCompiler(对文本代码的编译)
   
   DealCode(核心业务的处理)
   
   DoReplace(对字节码的替换)

>工具类:

ByteUtils(字节码工具)

ClassModifier(操作类替换字节码)

UpicJdkCompiler(JDK编译)

UpicClassLoader(类编译与加载

HackSystem(取代System.out)

>启动类：

DealStart(启动以及初始化数据)

>数据类：

DealData

InitData

>结果类：

DealResult

>测试类:

Test
原来线程实现:

### 快速启动

```
mvn compile exec:java -Dexec.mainClass=com.upic.test.Test
```
### 说明
    开了20个线程分别用正确的代码以及数据和错误的代码数据去测试

## CodeCompiler
方法：

```
public Object compile(String resource, String className);
```
    className：是由系统生成，在DealStart类当中，部分代码
代码

```
protected static final ConcurrentHashMap<String, Boolean> EVENT = new ConcurrentHashMap<String, Boolean>();
	private static final int LENGTH = 25;
	private static final String CLASSNAME = "Main";
	private static Object obj=new Object();
	static {
		for (int i = 0; i < LENGTH; i++) {
			EVENT.put(CLASSNAME + i, false);
		}
	}
```
>根据文档、类名进行编译，他有个实现类AbsractCodeCompiler

有两个抽象方法，代码:

```
public abstract Class<?> returnClass(String resource,String className);

	public abstract byte[] returnByte(String resource,String className);
```
说明：
>根据不同的需要返回不同的类型（Class以及byte[]）
>
>有两个子类：DoCompiler与JavassistCompiler，分别是用jdk编译类和javassist工具类的编译，目前只写了jdk的分支。

##DealCode（核心业务处理）

方法：
参数：代码文本和数据类
```
public DealResult deal(String resouece,DealData dealData);
```


实现类 AbstraceDealCodeCenter（两个抽象方法）
>是根据编译时候的方式不同的两个方法，分别针对类的处理和字节码的处理

```
public abstract DealResult dealByte(String resouece, DealData dealData);

public abstract DealResult dealClass(String resouece, DealData dealData);
```

## DealCodeCenter
>AbstraceDealCodeCenter的子类DealCodeCenter

### 核心方法


```
@Override
	public DealResult dealByte(String resouece, DealData dealData) {

		DealResult deal = new DealResult();
		try {
			if (dealData.getInitData().isEmpty()) {
				throw new NullPointerException("测试数据为空");
			}
			//类名替换
			String canUseName = DealStart.getCanUseName();
			System.out.println(canUseName);
			byte[] compile = (byte[]) codeCompiler.compile(resouece,canUseName);
			if (compile == null) {
				throw new NullPointerException("编译错误");
			}
			// 替换代码 败笔（需要修改）
			byte[] compileReplace = (byte[]) replaceCode.replace(compile);
			// 获得可以测试的类了
			Class<?> resultClass = doCompilerByByte(compileReplace,canUseName);
			if (resultClass == null) {
				throw new NullPointerException("编译错误");
			}
			// 对数据进行评分、卸载类、返回结果
			return dealResult0(resultClass,canUseName,dealData,deal);
		} catch (Exception e) {
			deal.setCode(1);
			deal.setError(e.getMessage());
			return deal;
		}
	}
```

>因为代码需要验证是通过早已准备好的数据，数据类InitData中有initData就是输入数据，这个根据DealData中的参数 newLine(是否为换行数据)，因为sacanner类有nextLine()与next*(),一个是换行数据，一个是单行数据，根据不同需求会做处理

```
//对数据的整理
	private void dealNewLine(String regex, String appnedWhat,List<InitData> initData) {
		for (int i = 0; i < initData.size(); i++) {
			String[] split = initData.get(i).getInitData().split(regex);
			StringBuffer sb = new StringBuffer();
			InitData replace = null;
			for (int j = 0; j < split.length; j++) {
				if (j == split.length - 1) {
					sb.append(split[j]);
					continue;
				}
				sb.append(split[j]).append(appnedWhat);
			}
			replace = initData.get(i).replace(sb.toString(), initData.get(i).getResultData());
			initData.set(i, replace);
		}
	}
```

最后类加载：类似于jsp的热部署

```
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
```



```
mvn compile exec:java -Dexec.mainClass=com.upic.lanbdaSteam.PredicateFilter
```



### DealResult
>贯穿全文，不管是异常还是正确，都是通过这个类传达给用户。


## 还需改进
>1、对文本代码的严格检查，包括不友好的入侵（写段代码删除服务器所有文件等类似问题）。
>2、关于启动类的Map数量的控制和扩展性（因为现在只放了25个，如果并发打的时候不好处理）
>3、线程是否安全（并发性问题）
>4、完善javassist分支
>5、文档需要完善，目前只是简单的介绍

需要大家多多指教。
