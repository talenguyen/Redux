package vn.tiki.redux;

final class Preconditions {
  Preconditions() {
    throw new AssertionError("No instances.");
  }

  static void checkNotNull(Object value) {
    if (value == null) {
      throw new NullPointerException();
    }
  }

  static void checkNotNull(Object value, String message) {
    if (value == null) {
      throw new NullPointerException(message);
    }
  }

  static void checkNotEmpty(Object[] array, String message) {
    if (array.length == 0) {
      throw new IllegalArgumentException(message);
    }
  }
}