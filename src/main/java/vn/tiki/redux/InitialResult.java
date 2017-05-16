package vn.tiki.redux;

/**
 * Created by Giang Nguyen on 5/16/17.
 */
public class InitialAction {
  private static InitialAction ourInstance = new InitialAction();

  public static InitialAction getInstance() {
    return ourInstance;
  }

  private InitialAction() {
  }
}
