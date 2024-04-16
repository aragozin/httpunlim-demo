package info.ragozin.demo.httpunlim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.client.RestTemplate;

public class HttpUnlimClient {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpUnlimClient.class);
	
	private final String serverUrl;
	private final ClientHttpRequestFactory factory;
	
	public HttpUnlimClient(ClientHttpRequestFactory factory, String serverUrl) {
		this.serverUrl = serverUrl.endsWith("/") ? serverUrl : serverUrl + "/";
		this.factory = factory;
		
	}
	
	public void postUnlimited(String path, InputStream source) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		RestTemplate template = new RestTemplate(factory);
		
		RequestEntity<Resource> request = new RequestEntity<>(new InputStreamResource(source), HttpMethod.POST, URI.create(serverUrl + path));
		ResponseEntity<String> resp = template.exchange(request, String.class);
		
		if (!resp.getStatusCode().is2xxSuccessful()) {
			throw new IOException("HTTP status: " + resp.getStatusCode());
		}
	}

	public void postUnlimited(String path, OutputStreamCallback dataProducer) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		RestTemplate template = new RestTemplate(factory);
		template.getMessageConverters().add(new AbstractGenericHttpMessageConverter<OutputStreamCallback>() {

			@Override
			public boolean canRead(Type type, Class<?> clazz, MediaType mediaType) {
				return false;
			}

			@Override
			public OutputStreamCallback read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
					throws IOException, HttpMessageNotReadableException {
				throw new UnsupportedOperationException();
			}

			@Override
			protected void writeInternal(OutputStreamCallback callback, Type type, HttpOutputMessage outputMessage)
					throws IOException, HttpMessageNotWritableException {
				callback.process(outputMessage.getBody());			
			}

			@Override
			protected OutputStreamCallback readInternal(Class<? extends OutputStreamCallback> clazz, HttpInputMessage inputMessage)
					throws IOException, HttpMessageNotReadableException {
				throw new UnsupportedOperationException();
			}
		});
		
		RequestEntity<OutputStreamCallback> request = new RequestEntity<>(dataProducer, HttpMethod.POST, URI.create(serverUrl + path));
		ResponseEntity<String> resp = template.exchange(request, String.class);
		
		if (!resp.getStatusCode().is2xxSuccessful()) {
			throw new IOException("HTTP status: " + resp.getStatusCode());
		}
	}

	public void getUnlimited_broken(String path) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
	
		RestTemplate template = new RestTemplate(factory);
		
		RequestEntity<Void> request = new RequestEntity<>(HttpMethod.GET, URI.create(serverUrl + path));
		ResponseEntity<Resource> resp = template.exchange(request, Resource.class);
		
		readOut(resp.getBody().getInputStream());		
	}

	
	public void getUnlimited(String path) throws IOException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
	
		RestTemplate template = new RestTemplate(factory);
		
		template.execute(URI.create(serverUrl + path), HttpMethod.GET, 
			req -> {}, 
			rsp -> {
				readOut(rsp.getBody());
				return null;
		});
	}

	
	private void readOut(InputStream body) throws IOException {
		byte[] buf = new byte[16 << 10];
		long t = 0;
		while (true) {
			int n = body.read(buf);
			if (n < 0) {
				break;
			}
			t += n;
		}
		LOGGER.info("Received " + t + " bytes");
	}
	
	public interface OutputStreamCallback {
		
		public void process(OutputStream os) throws IOException;
		
	}
}
