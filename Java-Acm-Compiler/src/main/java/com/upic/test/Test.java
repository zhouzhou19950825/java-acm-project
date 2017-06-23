package com.upic.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import com.upic.acm.data.DealData;
import com.upic.acm.data.InitData;
import com.upic.acm.dealCode.support.DealCodeCenter;
import com.upic.acm.result.DealResult;
import com.upic.acm.start.DealStart;

public class Test {
	private static final Pattern DELETE_SPACEBAR = Pattern.compile("\\s{1,}");

	public static void main(String[] args) throws InterruptedException {

		StringBuilder sb = new StringBuilder();
		sb.append("import java.util.Scanner;");
		sb.append("public class Main{").append("\n");
		sb.append("public  Main(){}").append("\n");
		sb.append("    public static void main(String[]args){").append("\n");
		sb.append("Scanner s=new Scanner(System.in);");
		sb.append("int a[]=new int[6];");
		sb.append("for(int i=0;i<6;i++){a[i]=s.nextInt()+1;}").append("\n");
		sb.append("for(int j=0;j<6;j++){System.out.println(a[j]);}").append("\n");
		sb.append("    }").append("\n");
		sb.append("}").append("\n");
		
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append("import java.util.Scanner;");
		sb1.append("public class Main{").append("\n");
		sb1.append("public  Main(){}").append("\n");
		sb1.append("    public static void main(String[]args){").append("\n");
		sb1.append("Scanner s=new Scanner(System.in);");
		sb1.append("int a[]=new int[6];");
		sb1.append("for(int i=0;i<6;i++){a[i]=s.nextInt()+2;}").append("\n");
		sb1.append("for(int j=0;j<6;j++){System.out.println(a[j]);}").append("\n");
		sb1.append("    }").append("\n");
		sb1.append("}").append("\n");
		List<Thread> list=new ArrayList<Thread>();
		for(int i=0;i<20;i++){
			if(i%2==0){
				list.add(new Thread(new TestA(sb),"t"+i));
			}else{
				list.add(new Thread(new TestA(sb1),"t"+i));
			}
		}
//		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		for(Thread t:list){
			t.start();
//			newCachedThreadPool.execute(t);
		}
//		newCachedThreadPool.shutdown();
		
	}

	
}
class TestA implements Runnable {

	private StringBuilder sb;

	public TestA(StringBuilder sb) {
		this.sb = sb;
	}

	@Override
	public void run() {
		List<InitData> initData = new ArrayList<>();
		init(initData);
		DealData d=new DealData(false, initData);
		// 初始化启动类
		// 获取结果
		DealResult deal = DealStart.getResult( null, d, sb.toString());
		if (deal.getCode() == 0) {
			System.out.println(Thread.currentThread().getName() + "解析成功，正确率为:" + deal.getSuccess() * 100 + "%");
		} else if (deal.getCode() == 1) {
			System.out.println(Thread.currentThread().getName() + "解析失败，错误信息:" + deal.getError());
		}
	}
	private static void init(List<InitData> initData) {
		String test = "1 2 3 4 5 6";
		String result = "2\r\n3\r\n4\r\n5\r\n6\r\n7\r\n";
		InitData i = new InitData(test, result);

		String test1 = "1   3  5  7 9   11";
		String result1 = "2\r\n4\r\n6\r\n8\r\n10\r\n12\r\n";
		InitData i2 = new InitData(test1, result1);

		initData.add(i);
		initData.add(i2);

	}
}