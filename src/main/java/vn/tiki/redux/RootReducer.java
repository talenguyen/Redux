package vn.tiki.redux;

import io.reactivex.functions.BiFunction;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

class RootReducer implements BiFunction<Map<String, Object>, Object, Map<String, Object>> {
  private final Reducer[] reducers;

  RootReducer(Reducer[] reducers) {
    this.reducers = reducers;
  }

  @Override public Map<String, Object> apply(Map<String, Object> state, Object o) throws Exception {
    System.out.println("result: " + o);
    Map<String, Object> nextState = new Hashtable<String, Object>(state);
    for (Reducer reducer : reducers) {
      final String key = reducer.name();
      final Object stateForKey = nextState.get(key);
      final Object nextStateForKey = reducer.apply(stateForKey, o);
      if (nextStateForKey != null && !nextStateForKey.equals(stateForKey)) {
        nextState.put(key, nextStateForKey);
      }
    }
    System.out.println("state: " + nextState);
    return Collections.unmodifiableMap(nextState);
  }
}