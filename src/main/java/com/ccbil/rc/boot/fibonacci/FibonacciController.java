package com.ccbil.rc.boot.fibonacci;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class FibonacciController {
	
	AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();

    @RequestMapping("/fib")
    public Long calculate(@RequestParam(value = "n", defaultValue = "1") Long n) {
    	if(n <= 2) {
    		return 1l;
    	} else {
    		CompletionStage<Long> fib1 = requestFib(n-1);
    		CompletionStage<Long> fib2 = requestFib(n-2);
    		
    		try {
				return fib1.thenCombine(fib2, (n1, n2) -> n1 + n2)
				.toCompletableFuture()
				.get();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
    	}
    }

	private CompletionStage<Long> requestFib(long n) {
		CompletableFuture<Response> future = 
				asyncHttpClient.prepareGet("http://localhost:8080/fib?n="+n).execute().toCompletableFuture();
		
		return future.thenApply(response ->
			Long.parseLong(response.getResponseBody()));
	}
	
}
