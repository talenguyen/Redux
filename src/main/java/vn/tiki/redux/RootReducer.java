package vn.tiki.redux;

public class RootReducer<State> implements Reducer<State> {
  private final Reducer<State>[] reducers;

  public RootReducer(Reducer<State>[] reducers) {
    this.reducers = reducers;
  }

  @Override public State apply(State state, Object o) throws Exception {
    State nextState = state;
    for (Reducer<State> reducer : reducers) {
      nextState = reducer.apply(nextState, o);
    }
    return nextState;
  }
}