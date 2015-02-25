import play.GlobalSettings;
import play.api.mvc.Results;
import play.libs.F;
import play.libs.Scala;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import scala.Tuple2;
import scala.collection.Seq;

import static play.core.j.JavaResults.BadRequest;
import static play.core.j.JavaResults.InternalServerError;
import static play.core.j.JavaResults.NotFound;

import java.util.ArrayList;
import java.util.List;

public class Global extends GlobalSettings {
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
        final private play.api.mvc.Result wrappedResult;

        public CORSResult(Http.RequestHeader header, Results.Status status) {
            List<Tuple2<String, String>> list = new ArrayList<Tuple2<String, String>>();

            String origin = header.getHeader("Origin");
            if(origin == null){
                origin = "*";
            }

            Tuple2<String, String> t = new Tuple2<String, String>("Access-Control-Allow-Origin", origin);
            list.add(t);
            t = new Tuple2<String, String>("Access-Control-Allow-Credentials", "true");
            list.add(t);
            Seq<Tuple2<String, String>> seq = Scala.toSeq(list);
            wrappedResult = status.withHeaders(seq);
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
        return F.Promise.<Result>pure(new CORSResult(request, InternalServerError()));
    }

    /*
    * Adds the required CORS header "Access-Control-Allow-Origin" when a route was not found
    */
    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return F.Promise.<Result>pure(new CORSResult(request, NotFound()));
    }
}
