import play.GlobalSettings;
import play.api.mvc.Codec;
import play.api.mvc.Results;
import play.libs.F;
import play.libs.Scala;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.Tuple2;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

import static play.core.j.JavaResults.*;

public class Global extends GlobalSettings {
    private static final Codec utf8 = Codec.javaSupported("utf-8");

    private class ActionWrapper extends Action.Simple {
        public ActionWrapper(Action<?> action) {
            this.delegate = action;
        }

        @Override
        public F.Promise<Result> call(Http.Context ctx) throws java.lang.Throwable {
            F.Promise<Result> result = this.delegate.call(ctx);
            Http.Response response = ctx.response();
            String origin = ctx.request().getHeader("Origin");
            if(origin == null){
                origin = "*";
            }
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            return result;
        }
    }

    @Override
    public Action<?> onRequest(Http.Request request,
                               java.lang.reflect.Method actionMethod) {
        return new ActionWrapper(super.onRequest(request, actionMethod));
    }

    private static class CORSResult implements Result {
        private final play.api.mvc.Result wrappedResult;
        private final Results.Status status;
        private final Http.RequestHeader header;

        public CORSResult(Http.RequestHeader header, Results.Status status) {
            this.header = header;
            this.status = status;
            wrappedResult = getResult().withHeaders(getHeader());
        }

        protected play.api.mvc.Result getResult(){
            return new play.mvc.Results.Status(status, "", utf8).toScala();
        }

        protected Seq<Tuple2<String, String>> getHeader(){
            List<Tuple2<String, String>> list = new ArrayList<Tuple2<String, String>>();

            String origin = header.getHeader("Origin");
            if(origin == null){
                origin = "*";
            }

            Tuple2<String, String> t = new Tuple2<String, String>("Access-Control-Allow-Origin", origin);
            list.add(t);
            t = new Tuple2<String, String>("Access-Control-Allow-Credentials", "true");
            list.add(t);
            return Scala.toSeq(list);
        }

        public play.api.mvc.Result toScala() {
            return this.wrappedResult;
        }
    }

    /*
    * Adds the required CORS header "Access-Control-Allow-Origin" to bad requests
    */
    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
        return F.Promise.<Result>pure(new CORSResult(request, BadRequest()));
    }

    /*
    * Adds the required CORS header "Access-Control-Allow-Origin" to requests that causes an exception
    */
    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {
        if(isApiRequest(request)){
            return F.Promise.<Result>pure(new CORSResult(request, InternalServerError()));
        }else{
            return F.Promise.<Result>pure(new play.mvc.Results.Status(
                    InternalServerError(), views.html.errors.error500.render(), utf8
            ));
        }
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        if(isApiRequest(request)){
            return F.Promise.<Result>pure(new CORSResult(request, NotFound()));
        }else{
            return F.Promise.<Result>pure(new play.mvc.Results.Status(
                    NotFound(), views.html.errors.error404.render(
                    "We can't find the URL you're looking for"
            ), utf8
            ));
        }
    }

    private boolean isApiRequest(Http.RequestHeader request){
        return request.path().startsWith("/api/");
    }
}
