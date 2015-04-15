package controllers;

import play.filters.csrf.AddCSRFToken;
import play.mvc.Controller;
import play.mvc.Result;

public class BaseController extends Controller {
    public static Status internalServerError() {
        return internalServerError(views.html.errors.error500.render());
    }
    public static Status notFound(String error) {
        return notFound(views.html.errors.error404.render(error));
    }
    public static Status forbidden() {
        return forbidden(views.html.errors.error403.render());
    }
}
