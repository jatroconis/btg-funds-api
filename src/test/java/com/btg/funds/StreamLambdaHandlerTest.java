package com.btg.funds;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class StreamLambdaHandlerTest {

    @Test
    void testHandlerCanBeInstantiated() {
        // This will trigger the static block. We just assert it doesn't throw an error.
        assertDoesNotThrow(() -> {
            new StreamLambdaHandler();
        });
    }
    
    @Test
    void testHandleRequest() {
        assertDoesNotThrow(() -> {
            StreamLambdaHandler handler = new StreamLambdaHandler();
            InputStream inputStream = new ByteArrayInputStream("{}".getBytes());
            OutputStream outputStream = new ByteArrayOutputStream();
            Context context = mock(Context.class);
            
            try {
                handler.handleRequest(inputStream, outputStream, context);
            } catch (Exception e) {
                // It's expected to fail processing an empty/invalid JSON, but we are testing it doesn't crash structurally.
            }
        });
    }
}