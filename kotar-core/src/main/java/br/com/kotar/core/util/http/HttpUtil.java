package br.com.kotar.core.util.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.core.env.Environment;

import br.com.kotar.core.util.Constants;
import br.com.kotar.core.util.StringUtil; 

public class HttpUtil {

	private static HttpUtil INSTANCE;
	
	private Environment env;
	
	static {
		INSTANCE = new HttpUtil();
	}
	
	public static String postHttpRequest(String httpUrl, String json, BasicHeader... headers) throws Exception{
		
		String jsonReturn = "";
		CloseableHttpClient httpClient = getCloseableHttpClient();

		try {
			
		    HttpPost request = new HttpPost(httpUrl);
		    StringEntity params = new StringEntity(json);
		    request.addHeader("content-type", "application/json");
		    
		    if (headers != null) {
		    	for (BasicHeader basicHeader : headers) {
		    		request.addHeader(basicHeader);
		    	}
		    }
		    request.setEntity(params);
		    HttpResponse response = httpClient.execute(request);
		    
		    jsonReturn = EntityUtils.toString(response.getEntity(), "UTF-8");
		    
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		    httpClient.close();
		}
		return jsonReturn;
	}


	public static String getHttpRequest(String httpUrl) throws Exception{
		
		String jsonReturn = "";
		CloseableHttpClient httpClient = getCloseableHttpClient();

		try {
		    HttpGet request = new HttpGet(httpUrl);

		    HttpResponse response = httpClient.execute(request);
		    
		    jsonReturn = EntityUtils.toString(response.getEntity(), "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		    httpClient.close();
		}
		return jsonReturn;
	}

	
	private static CloseableHttpClient getCloseableHttpClient() {
		
		HttpHost proxy = INSTANCE.getProxy();
		CloseableHttpClient httpClient;
		if (proxy == null) {
			int timeout = 30;
			RequestConfig config = RequestConfig.custom()
			  .setConnectTimeout(timeout * 1000)
			  .setConnectionRequestTimeout(timeout * 1000)
			  .setSocketTimeout(timeout * 1000).build();
			
			
			httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		}
		else {
						
			httpClient = HttpClientBuilder.create().setProxy(proxy).build();
		}
		return httpClient;
		
	}
	
	
	
	public HttpHost getProxy() {
		
		HttpHost retorno = null;
		
		if (Constants.HAS_PROXY == null && Constants.PROXY == null) {
			String host = env.getProperty("proxy.host");
			
			if (StringUtil.temValor(host)) {
				
				String porta = env.getProperty("proxy.port");
				if (!StringUtil.temValor(porta)) {
					porta = "3128";
				}
				retorno = new HttpHost(host, Integer.valueOf(porta));
				
				Constants.PROXY = retorno;
				Constants.HAS_PROXY = true;
			}
			else {
				Constants.HAS_PROXY = false;
			}
		}
		else {
			retorno = Constants.PROXY;
		}
		
		
		return retorno;
	}

	public static void init(Environment env) {
		if (INSTANCE.env == null) {
			INSTANCE.env = env;	
			INSTANCE.getProxy();
		}
	}
}
