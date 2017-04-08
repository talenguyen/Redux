package vn.tiki.redux;

import io.reactivex.Observable;

/**
 * Created by Giang Nguyen on 3/24/17.
 */

public interface Effect<State> {

  Observable<Object> apply(Observable<Object> action$, Function0<State> getState);
}
