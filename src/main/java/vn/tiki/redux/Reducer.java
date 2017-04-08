package vn.tiki.redux;

import io.reactivex.functions.BiFunction;

/**
 * Created by Giang Nguyen on 4/8/17.
 */

public interface Reducer<State> extends BiFunction<State, Object, State> {
}
