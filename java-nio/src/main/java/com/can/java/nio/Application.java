package com.can.java.nio;

import com.can.java.nio.util.FileUtil;

import java.io.IOException;

/**
 * <pre>
 * 应用启动类
 * </pre>
 *
 * @author lcn29
 * @date 2022-01-19  17:19
 */
public class Application {

	public static void main(String[] args) {
		System.out.println("OK");
		test();
	}

	private static void test() {

		String fileName = "C:\\Users\\lcn\\Desktop\\log.log";

		try {
			FileUtil.readFile(fileName, (dataByte) -> {
				String dataString = new String(dataByte);
				System.out.println(dataString);
				return true;
			});
		} catch (IOException exception) {
			System.out.println("异常" + exception.getMessage());
		}


	}
}
