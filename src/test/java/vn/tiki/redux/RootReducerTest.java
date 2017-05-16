package vn.tiki.redux;

import java.util.Collections;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Giang Nguyen on 5/12/17.
 */
public class RootReducerTest {
  private RootReducer tested;

  @SuppressWarnings("unchecked") @Before
  public void setUp() throws Exception {
    final Reducer add = new Reducer() {
      @Override public Object apply(Object o, Object o2) throws Exception {
        if (o == null) {
          return o2;
        }
        return (Integer) o + (Integer) o2;
      }

      @Override public String name() {
        return "add";
      }
    };
    final Reducer multiply = new Reducer() {
      @Override public Object apply(Object o, Object o2) throws Exception {
        if (o == null) {
          return o2;
        }
        return (Integer) o * (Integer) o2;
      }

      @Override public String name() {
        return "multiply";
      }
    };
    final Reducer[] reducers = new Reducer[] { add, multiply };
    tested = new RootReducer(reducers);
  }

  @Test
  public void should_deliver_result_to_right_reducer() throws Exception {

    Map<String, Object> nextState = tested.apply(Collections.<String, Object>emptyMap(), 2);
    assertEquals(2, nextState.get("add"));
    assertEquals(2, nextState.get("multiply"));

    nextState = tested.apply(nextState, 3);
    assertEquals(5, nextState.get("add"));
    assertEquals(6, nextState.get("multiply"));
  }
}