package vn.tiki.redux;

/**
 * Created by Giang Nguyen on 4/8/17.
 */

public interface Reducer {

  String name();

  Object apply(Object state, Object result) throws Exception;
}
