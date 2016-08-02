import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.Logger;
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

import models.User;
import static play.core.j.JavaResults.*;

public class Global extends GlobalSettings {
    private static final Codec utf8 = Codec.javaSupported("utf-8");
    
    /** The _key name_ in application.conf for an admin user to create in new installs. */
    private static final String USERNAME_KEY = "exceed.admin.username";
    /** The _key name_ in application.conf for admin password to create in a fresh install. */
    private static final String USERPASS_KEY = "exceed.admin.password";
    /** Cludgy limit on username and password length. */
    private static final int MAX_USER_CREDENTIAL = 40;


    /**
     * This method is invoked when application is started.
     * Since Play initially has an empty user's database, 
     * use this method to add a single admin user at start.
     * A user is only added if the user's table is empty;
     * if the table is populated then nothing is done.
     * The admin username and admin password are read from application.conf.
     * After the admin user is created you can (and should)
     * remove it from application.conf.
     * @param app reference to this Application
     */
    @Override
    public void onStart(Application app) {
      Logger.info("Application started");
      int userCount = User.find.findRowCount();
      if (userCount > 0) return;
      // do we have default username/password for admin user in application.conf?
      Configuration config = play.Play.application().configuration();
      String username = config.getString(USERNAME_KEY);
      String password = config.getString(USERPASS_KEY);
      if (username == null || username.isEmpty() || password == null || password.isEmpty()) return;
      // Make sure its a reasonable username (This check should really be in User.)
      if (username.length() > MAX_USER_CREDENTIAL || ! username.matches("\\w*")) {
    	  Logger.error("Invalid username in application.conf. Must be alphanumeric <= 40 chars: "+username);
    	  return;
      }
      if (password.length() > MAX_USER_CREDENTIAL) {
    	  Logger.error("Invalid password in application.conf. Must be at most "+MAX_USER_CREDENTIAL+
    			  " characters."); // don't log the password, even though its not valid
    	  return;
      }
      // The User class will do the work of encrypting the password
      User admin = new User();
      admin.username = username;
      admin.setPassword(password);  // invoke setter so encryption is done
      admin.name = "Admin";
      admin.type = User.TYPES.ORGANIZER;
      admin.save();
    }
    
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
