package hello.jdbc.repository.ex;

public class MyDuplicateKeyException extends MyDbException { // note: 런타임 익셉션을 바로 상속 받을 수도 있지만 DB에러를 카테고리화 시키기 위해서 MyDbException을 상속 받았다.

  public MyDuplicateKeyException() {
    super();
  }

  public MyDuplicateKeyException(String message) {
    super(message);
  }

  public MyDuplicateKeyException(String message, Throwable cause) {
    super(message, cause);
  }

  public MyDuplicateKeyException(Throwable cause) {
    super(cause);
  }
}
