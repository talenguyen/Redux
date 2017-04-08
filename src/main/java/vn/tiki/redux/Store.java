package vn.tiki.redux;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Giang Nguyen on 3/23/17.
 */
public class Store<State> {

  private final Reducer<State> reducer;
  private final PublishSubject<Object> action$;
  private final BehaviorSubject<State> state$;
  private final Observable<Object> result$;
  private final Function0<State> getState = new Function0<State>() {
    @Override public State involve() throws Exception {
      return currentState();
    }
  };
  private Disposable disposable;

  Store(State initialState,
      Reducer<State> reducer,
      Effect<State>[] effects) {
    this.reducer = reducer;
    this.action$ = PublishSubject.create();
    this.state$ = BehaviorSubject.createDefault(initialState);
    this.result$ = Observable.fromArray(effects)
        .flatMap(new Function<Effect<State>, ObservableSource<Object>>() {
          @Override public ObservableSource<Object> apply(Effect<State> effect)
              throws Exception {
            return effect.apply(action$, getState);
          }
        });
  }

  public static <State> Builder<State> builder() {
    return new Builder<State>();
  }

  private State currentState() {
    return state$.getValue();
  }

  public Observable<State> state$() {
    return state$;
  }

  public void dispatch(Object action) {
    action$.onNext(action);
  }

  void startBinding() {
    if (disposable != null) {
      // binding is started already
      return;
    }
    disposable = result$
        .scan(currentState(), reducer)
        .subscribe(new Consumer<State>() {
          @Override public void accept(State state) throws Exception {
            state$.onNext(state);
          }
        });
  }

  void stopBinding() {
    if (disposable != null) {
      disposable.dispose();
      disposable = null;
    }
  }

  public static class Builder<State> {
    private State initialState;
    private Reducer<State> reducer;
    private Effect<State>[] effects;

    Builder() {
      // private constructor
    }

    public Builder<State> initialState(State initialState) {
      Preconditions.checkNotNull(initialState, "initialState must not be null");
      this.initialState = initialState;
      return this;
    }

    public Builder<State> reducer(Reducer<State> reducer) {
      Preconditions.checkNotNull(reducer, "reducer must not be null");
      this.reducer = reducer;
      return this;
    }

    public final Builder<State> effects(Effect<State>... effects) {
      Preconditions.checkNotNull(effects);
      Preconditions.checkNotEmpty(effects, "effects must not be empty");
      this.effects = effects;
      return this;
    }

    public Store<State> make() {
      return new Store<State>(initialState, reducer, effects);
    }
  }
}
