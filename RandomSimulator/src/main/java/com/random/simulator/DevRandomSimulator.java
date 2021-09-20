package com.random.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;;

/**
 * DevRandomSimulator is the program, which simulates $cat /dev/random
 * functionality. DevRandomSimulator program produces the [dev_random_replica]
 * file at the location of your choice and constantly re-generating its content
 * with new entropy data.
 * 
 * NOTE: Content of this file is being replaced with the entropy data coming
 * from 3 different random algorithms: 1) Entropy data based upon
 * System.nanoTime() seed 2) Entropy data based upon SecureRandom-related seed
 * 3) Entropy data based upon UUID.randomUUID().getMostSignificantBits() seed
 * 
 * NOTE: results from all 3 entropy data sources are combined and re-shuffled.
 * 
 * Please use following commands to view produced results:
 * 
 * ${FILE_DIR}/> cat dev_random_replica or ${FILE_DIR}/> cat dev_random_replica
 * | base64
 * 
 * @author igorbukhvalov
 *
 */
public class DevRandomSimulator {

	enum RANDOM {
		SECURE_RANDOM, NANOSEC, UUID_RANDOM;
	}

	private static final int _2 = 2;
	private static final int _10 = 10;
	private static final int _512 = 512;
	private static final int _8 = 8;

	public static void main(String[] args) throws IOException, InterruptedException, NoSuchAlgorithmException {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input directory location for the [dev_random_replica] file creation");
		String filelocation = scanner.nextLine();

		File dev_random_replica = new File(filelocation + File.separator + "dev_random_replica");
		RandomAccessFile randomAccessFile = new RandomAccessFile(dev_random_replica, "rwd");

		int counter = 0;
		try {
			while (true) {

				List<byte[]> complete = new ArrayList<byte[]>();

				List<byte[]> listNano = generateRandom(_512, RANDOM.NANOSEC);
				List<byte[]> listSecureRandom = generateRandom(_512, RANDOM.SECURE_RANDOM);
				List<byte[]> listUUIDRandom = generateRandom(_512, RANDOM.UUID_RANDOM);

				complete.addAll(listNano);
				complete.addAll(listSecureRandom);
				complete.addAll(listUUIDRandom);

				// to shuffle entropy data
				Collections.shuffle(complete);

				for (byte[] b : complete) {
					if (counter % _2 == 0)
						randomAccessFile.seek(0);
					randomAccessFile.write(b);
					System.out.print(Base64.getEncoder().encodeToString(b) + " ");
				}

				Thread.sleep(_10);
				System.out.println("");
				counter++;

			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found" + e);
		} catch (IOException ioe) {
			System.out.println("Exception while writing file " + ioe);
		} finally {

			try {
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
	}

	/**
	 * generateRandom method
	 * 
	 * generating random data based upon random type
	 * 
	 * @param randomPoolSize
	 * @param randomType
	 * @return
	 */
	public static List<byte[]> generateRandom(Integer randomPoolSize, RANDOM randomType) {

		List<byte[]> randomData = new ArrayList<byte[]>();
		Random random = new Random();
		long totalSize = 0L;
		byte[] buffer = null;

		while (totalSize <= randomPoolSize) {
			buffer = new byte[_8];
			switch (randomType) {
			case NANOSEC:
				random.setSeed(System.nanoTime());
				break;
			case SECURE_RANDOM:
				random.setSeed(getLongSeed());
				break;
			case UUID_RANDOM:
				random.setSeed(UUID.randomUUID().getMostSignificantBits());
				break;
			default:
				random.setSeed(getLongSeed());
				break;
			}
			random.nextBytes(buffer);
			totalSize += buffer.length;
			randomData.add(buffer);
		}
		return randomData;
	}

	private static long getLongSeed() {
		SecureRandom sec = new SecureRandom();
		byte[] sbuf = sec.generateSeed(_8);
		ByteBuffer bb = ByteBuffer.wrap(sbuf);
		return bb.getLong();
	}

}