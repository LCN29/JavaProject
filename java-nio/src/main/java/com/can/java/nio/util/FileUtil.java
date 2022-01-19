package com.can.java.nio.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.Function;

/**
 * <pre>
 * 文件工具类
 * </pre>
 *
 * @author lcn29
 * @date 2022-01-19  20:25
 */
public class FileUtil {

	private final static String READ_MODLE = "r";

	private final static String WRITE_MODLE = "w";

	private final static String READ_WRITE_MODEL = "rw";

	private final static int DEFAULT_READ_BYTE_LIMIT = 1024;

	/**
	 * 读取文件
	 *
	 * @param filePath 文件路径
	 * @param function 处理函数
	 * @throws IOException
	 */
	public static void readFile(String filePath, Function<byte[], Boolean> function) throws IOException {
		readFile(filePath, function, DEFAULT_READ_BYTE_LIMIT);
	}

	/**
	 * 读取文件
	 *
	 * @param filePath      文件路径
	 * @param function      处理函数
	 * @param readByteLimit 每次读取的字节数
	 * @throws IOException
	 */
	public static void readFile(String filePath, Function<byte[], Boolean> function, int readByteLimit) throws IOException {

		try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODLE)) {

			FileChannel channel = file.getChannel();
			ByteBuffer buf = ByteBuffer.allocate(readByteLimit);
			while (channel.read(buf) != -1) {
				buf.flip();
				// 返回值为 true, 表示继续读取
				if (!function.apply(buf.array())) {
					break;
				}
				buf.clear();
			}

		}
	}
}
