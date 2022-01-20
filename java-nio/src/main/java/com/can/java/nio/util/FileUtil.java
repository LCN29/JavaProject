package com.can.java.nio.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Objects;
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

	private final static String READ_WRITE_MODEL = "rw";

	private final static int DEFAULT_HANDLE_BYTE_LIMIT = 1024;

	/**
	 * 读取文件
	 *
	 * @param filePath 文件路径
	 * @param function 处理函数
	 * @throws IOException
	 */
	public static void readFile(String filePath, Function<byte[], Boolean> function) throws IOException {
		readFile(filePath, function, DEFAULT_HANDLE_BYTE_LIMIT);
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

		try (RandomAccessFile file = new RandomAccessFile(filePath, READ_MODLE);
			 FileChannel channel = file.getChannel()) {

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

	/**
	 * 将字节内容直接写入到文件
	 *
	 * @param filePath 文件路径
	 * @param data     写入的内容
	 * @throws IOException
	 */
	@Deprecated
	public static void writeSimpleFile(String filePath, byte[] data) throws IOException {

		try (RandomAccessFile file = new RandomAccessFile(filePath, READ_WRITE_MODEL);
			 FileChannel channel = file.getChannel()) {

			ByteBuffer buf = ByteBuffer.allocate(data.length);
			buf.clear();
			buf.put(data);
			buf.flip();
			channel.write(buf);
		}
	}

	/**
	 * 将字节内容分批写入到文件
	 *
	 * @param filePath     文件路径
	 * @param dataProvider 数据提供函数
	 * @throws IOException
	 */
	@Deprecated
	public static void writeBigFile(String filePath, Function<Integer, byte[]> dataProvider) throws IOException {

		try (RandomAccessFile file = new RandomAccessFile(filePath, READ_WRITE_MODEL);
			 FileChannel channel = file.getChannel()) {

			int hadHandlerByteCount = 0;
			byte[] dataByte = dataProvider.apply(hadHandlerByteCount);
			if (Objects.isNull(dataByte) || dataByte.length == 0) {
				return;
			}

			ByteBuffer buf = ByteBuffer.allocate(DEFAULT_HANDLE_BYTE_LIMIT);
			buf.clear();

			while (Objects.nonNull(dataByte)) {

				int dateByteLen = dataByte.length;

				hadHandlerByteCount += dateByteLen;

				if (dateByteLen <= DEFAULT_HANDLE_BYTE_LIMIT) {
					buf.put(dataByte);
					buf.flip();
					channel.write(buf);
					buf.clear();
					buf.flip();
					dataByte = dataProvider.apply(hadHandlerByteCount);
					continue;
				}

				int offset = 0;
				int nextWriteLenght = DEFAULT_HANDLE_BYTE_LIMIT;
				while (offset < dateByteLen) {
					buf.put(dataByte, offset, nextWriteLenght);
					buf.flip();
					channel.write(buf);
					buf.clear();
					buf.flip();

					offset += nextWriteLenght;
					nextWriteLenght += Math.min(dateByteLen - nextWriteLenght, DEFAULT_HANDLE_BYTE_LIMIT);
				}
				dataByte = dataProvider.apply(hadHandlerByteCount);
			}

		}
	}


}
