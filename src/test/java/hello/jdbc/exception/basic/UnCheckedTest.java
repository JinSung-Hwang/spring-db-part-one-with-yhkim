package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.jdbc.exception.basic.CheckedTest.MyCheckedException;
import hello.jdbc.exception.basic.UnCheckedTest.MyUncheckedException.Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UnCheckedTest {

  @Test
  void unchecked_catch() {
    Service service = new Service();
    service.callCatch();
  }

  @Test
  void unchecked_throw() {
    Service service = new Service();
    assertThatThrownBy(() -> service.callThrow())
        .isInstanceOf(MyUncheckedException.class);
  }

  static class MyUncheckedException extends RuntimeException {
    public MyUncheckedException(String message) {
      super(message);
    }

    /**
     * note: Unchecked(RuntimeException) 예외는
     * note: 예외를 잡거나, 던지지 않아도 된다.
     * note: 예외를 잡지 않으면 자동으로 밖으로 던진다.
     */
    static class Service {
      Repository repository = new Repository();

      public void callCatch() {
        try {
          repository.call();
        } catch (MyUncheckedException e) {
          log.info("예외 처리, message = {}", e.getMessage(), e);
        }
      }

      /**
       * note: 예외를 잡지 않아도 된다. 자연스럽게 상위로 넘어간다.
       * note: 체크 예외와 다르게 throws 예외 선언을 하지 않아도 된다.
       */
      public void callThrow() {
        repository.call();
      }
    }

    static class Repository {
//      public void call() throws MyCheckedException { // note: RunTimeException도 이렇게 throws를 던져도 된다. 이러면 컴파일 에러는 안나지만 IDE가 발생하게 된다. 하지만 보통은 생략하여 사용한다.
      public void call() {
        throw new MyUncheckedException("ex");
      }
    }
  }

}
