package vn.tiki.redux;

import io.reactivex.Observable;
import java.util.Map;

/**
 * Created by Giang Nguyen on 3/24/17.
 */
public interface Effect {

  Observable<Object> apply(Observable<Object> action$, Function0<Map<String, Object>> getState);
}
