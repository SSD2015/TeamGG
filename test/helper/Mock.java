package helper;

import play.mvc.Http;

import java.util.*;

import static org.mockito.Mockito.mock;

public class Mock {
    public static void mockRequest() {
        Map<String, String> flashData = Collections.emptyMap();
        Map<String, Object> argData = Collections.emptyMap();
        Long id = 2L;
        play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
        Http.Request request = mock(Http.Request.class);
        Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
        Http.Context.current.set(context);
    }
}
