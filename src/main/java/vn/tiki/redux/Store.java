package vn.tiki.redux;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.Map;

/**
 * Created by Giang Nguyen on 3/23/17.
 */
public class Store {

  private final RootReducer reducer;
  private final PublishSubject<Object> action$;
  private final BehaviorSubject<Map<String, Object>> state$;
  private final Observable<Object> result$;
  private final Function0<Map<String, Object>> getState = new Function0<Map<String, Object>>() {
    @Override public Map<String, Object> involve() throws Exception {
      return currentState();
    }
  };
  private Disposable disposable;

  Store(Map<String, Object> initialState,
      Reducer[] reducers,
      Effect[] effects) {
    this.reducer = new RootReducer(reducers);
    this.action$ = PublishSubject.create();
    this.state$ = BehaviorSubject.createDefault(initialState);
    this.result$ = Observable.fromArray(effects)
        .flatMap(new Function<Effect, ObservableSource<?>>() {
          @Override public ObservableSource<?> apply(Effect effect) throws Exception {
            return effect.apply(action$, getState);
          }
        })
        .startWith(new InitialResult());
  }

  public static Builder builder() {
    return new Builder();
  }

  private Map<String, Object> currentState() {
    return state$.getValue();
  }

  public Observable<Map<String, Object>> state$() {
    return state$;
  }

  public void dispatch(Object action) {
    action$.onNext(action);
  }

  public void startBinding() {
    if (disposable != null) {
      // binding is started already
      return;
    }
    disposable = result$
        .scan(currentState(), reducer)
        .doOnError(new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            throwable.printStackTrace();
          }
        })
        .subscribe(new Consumer<Map<String, Object>>() {
          @Override public void accept(Map<String, Object> state) throws Exception {
            state$.onNext(state);
          }
        });
  }

  public void stopBinding() {
    if (disposable != null) {
      disposable.dispose();
      disposable = null;
    }
  }

  public static class Builder {
    private Map<String, Object> initialState;
    private Reducer[] reducers;
    private Effect[] effects;

    Builder() {
      // private constructor
    }

    public Builder initialState(Map<String, Object> initialState) {
      this.initialState = initialState;
      return this;
    }

    public final Builder reducers(Reducer... reducers) {
      this.reducers = reducers;
      return this;
    }

    public final Builder effects(Effect... effects) {
      this.effects = effects;
      return this;
    }

    public Store make() {
      Preconditions.checkNotNull(initialState, "initialState must not be null");
      Preconditions.checkNotNull(reducers, "reducers must not be null");
      Preconditions.checkNotEmpty(reducers, "reducers must not be empty");
      Preconditions.checkNotNull(effects);
      Preconditions.checkNotEmpty(effects, "effects must not be empty");
      return new Store(initialState, reducers, effects);
    }
  }
}
