package controllers;

/**
 *
 * @author willy
 */
import play.*;
import play.mvc.*;

@Check("admin")
@With(Secure.class)
public class Posts extends CRUD{
    
}
