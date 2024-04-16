package info.ragozin.demo.httpunlim;

import java.io.IOException;
import java.io.OutputStream;

public class VoidOutputStream extends OutputStream {
	
	private long size = 0;

	@Override
	public void write(int b) throws IOException {
		size++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		size += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		size += len;
	}
	
	public long getSize() {
		return size;
	}
}
