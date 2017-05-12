package vn.tiki.redux;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Giang Nguyen on 5/12/17.
 */
public class RootReducerTest {
  private RootReducer<Integer> tested;

  @SuppressWarnings("unchecked") @Before
  public void setUp() throws Exception {
    final Reducer<Integer> add = new Reducer<Integer>() {
      @Override public Integer apply(Integer integer, Object o) {
        return integer + (Integer) o;
      }
    };
    final Reducer<Integer> multiply = new Reducer<Integer>() {
      @Override public Integer apply(Integer integer, Object o) {
        return integer * (Integer) o;
      }
    };
    final Reducer<Integer>[] reducers = new Reducer[] { add, multiply };
    tested = new RootReducer<Integer>(reducers);
  }

  @Test
  public void should_combine_result() throws Exception {
    assertEquals(4, (int) tested.apply(0, 2));
  }
}