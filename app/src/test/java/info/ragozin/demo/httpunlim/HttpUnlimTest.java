package info.ragozin.demo.httpunlim;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
public class HttpUnlimTest {
	
	@LocalServerPort
	private int localPort;
	
	private ClientHttpRequestFactory httpFactory() {
		return new JdkClientHttpRequestFactory();
//		return new SimpleClientHttpRequestFactory();
	}
	
	@Test
	public void unlimGetStream_broken() throws IOException {
		HttpUnlimClient client = new HttpUnlimClient(httpFactory(), "http://127.0.0.1:" + localPort);
		
		client.getUnlimited_broken("binary?size=" + (1 << 20));		
	}

	@Test
	public void unlimGetStream_broken_oom() throws IOException {
		HttpUnlimClient client = new HttpUnlimClient(httpFactory(), "http://127.0.0.1:" + localPort);
		
		client.getUnlimited_broken("binary?size=" + (1 << 30));		
	}

	@Test
	public void unlimGetStream() throws IOException {
		HttpUnlimClient client = new HttpUnlimClient(httpFactory(), "http://127.0.0.1:" + localPort);
		
		client.getUnlimited("binary?size=" + (1 << 30));		
	}
	
	@Test
	public void unlimPost() throws IOException {
		HttpUnlimClient client = new HttpUnlimClient(httpFactory(), "http://127.0.0.1:" + localPort);
		
		client.postUnlimited("blackhole", new RngInputStream(1, 1l << 30));		
	}
	
	@Test
	public void unlimPost2() throws IOException {
		HttpUnlimClient client = new HttpUnlimClient(httpFactory(), "http://127.0.0.1:" + localPort);
		
		client.postUnlimited("blackhole", os -> IOUtils.copy(new RngInputStream(1, 1l << 30), os));		
	}		
}
