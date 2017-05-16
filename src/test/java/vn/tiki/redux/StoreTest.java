package vn.tiki.redux;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.TestObserver;
import java.util.Collections;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Giang Nguyen on 5/16/17.
 */
public class StoreTest {

  private Store tested;

  @Before
  public void setUp() throws Exception {
    final Map<String, Object> initialState = Collections.emptyMap();

    final Reducer add = new Reducer() {
      @Override public String name() {
        return "add";
      }

      @Override public Object apply(Object state, Object result) throws Exception {
        if (result instanceof InitialResult) {
          return 0;
        } else if (result instanceof AddResult) {
          return ((Integer) state) + ((AddResult) result).value;
        }
        return state;
      }
    };
    Reducer multiply = new Reducer() {
      @Override public String name() {
        return "multiply";
      }

      @Override public Object apply(Object state, Object result) throws Exception {
        if (result instanceof InitialResult) {
          return 1;
        } else if (result instanceof MultiplyResult) {
          return ((Integer) state) * ((MultiplyResult) result).value;
        }
        return state;
      }
    };

    final Reducer[] reducers = new Reducer[] { add, multiply };

    final Effect addEffect = new Effect() {
      @Override public Observable<Object> apply(Observable<Object> action$,
          Function0<Map<String, Object>> getState) {
        return action$
            .filter(new Predicate<Object>() {
              @Override public boolean test(Object o) throws Exception {
                return o.equals("ADD");
              }
            })
            .map(new Function<Object, Object>() {
              @Override public Object apply(Object o) throws Exception {
                return new AddResult(2);
              }
            });
      }
    };
    final Effect multiplyEffect = new Effect() {
      @Override public Observable<Object> apply(Observable<Object> action$,
          Function0<Map<String, Object>> getState) {
        return action$
            .filter(new Predicate<Object>() {
              @Override public boolean test(Object o) throws Exception {
                return o.equals("MULTIPLY");
              }
            })
            .map(new Function<Object, Object>() {
              @Override public Object apply(Object o) throws Exception {
                return new MultiplyResult(3);
              }
            });
      }
    };

    final Effect[] effects = new Effect[] { addEffect, multiplyEffect };

    tested = new Store(initialState, reducers, effects);
    tested.startBinding();
  }

  @After
  public void tearDown() throws Exception {
    tested.stopBinding();
  }

  @Test
  public void should_initial_reducers() throws Exception {
    final TestObserver<Map<String, Object>> testObserver = TestObserver.create();
    tested.state$()
        .subscribe(testObserver);

    final Map<String, Object> state = testObserver.values().get(0);
    assertEquals(0, state.get("add"));
    assertEquals(1, state.get("multiply"));
  }

  @Test
  public void should_update_by_actions() throws Exception {
    final TestObserver<Map<String, Object>> testObserver =
        TestObserver.create();
    tested.state$()
        .subscribe(testObserver);

    tested.dispatch("ADD");
    Map<String, Object> state = testObserver.values().get(1);
    assertEquals(2, state.get("add"));
    assertEquals(1, state.get("multiply"));

    tested.dispatch("MULTIPLY");
    state = testObserver.values().get(2);
    assertEquals(2, state.get("add"));
    assertEquals(3, state.get("multiply"));
  }

  private static class AddResult {
    final int value;

    private AddResult(int value) {
      this.value = value;
    }
  }

  private static class MultiplyResult {
    final int value;

    private MultiplyResult(int value) {
      this.value = value;
    }
  }
}