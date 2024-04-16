package info.ragozin.demo.httpunlim;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RngInputStream extends InputStream {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RngInputStream.class);

	private final long limit;
	private final Random rnd;
	private long pos = 0;
	
	public RngInputStream(long seed, long limit) {
		this.rnd = new Random(seed);
		this.limit = limit;
	}

	@Override
	public int read() throws IOException {
		if (pos >= limit) {
			return -1;
		} else {
			pos++;
			checkLog();
			return 0xFF & rnd.nextInt();
		}
	}

	private void checkLog() {
		if ((pos % (100l << 20)) == 0) {
			LOGGER.info("Generated: " + (pos >> 20) + "m");
		}	
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (pos >= limit) {
			return -1;
		}
		int l = len;
		if (pos + l > limit) {
			l = (int) (limit - pos);
		}
		for (int n = off; n < off + l; ++n) {
			pos++;
			checkLog();
			b[n] = (byte) rnd.nextInt();
		}
		return l;
	}

	@Override
	public int available() throws IOException {
		return (int) Math.min(Integer.MAX_VALUE, limit - pos);
	}
}
