package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.ConnectException;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class UncheckedAppTest {

  @Test
  void checked() {
    Controller controller = new Controller();
    assertThatThrownBy(() -> controller.request())
        .isInstanceOf(Exception.class);
  }

  @Test
  void printEx() {
    Controller controller = new Controller();
    try {
      controller.request();
    } catch (Exception e) {
//      e.printStackTrace();
      log.info("ex", e);
    }
  }

  static class Controller {
    Service service = new Service();
    public void request() {
      service.login();
    }
  }

  static class Service {
    Repository repository = new Repository();
    NetworkClient networkClient = new NetworkClient();

    public void login() {
      repository.call();
      networkClient.call();
    }

  }
  static class NetworkClient {
    public void call() {
      throw new RunTimeConnectException("연결 실패");
    }

  }
  static class Repository {
    public void call() {
      try {
        runSQL();
      } catch (SQLException e) {
        throw new RunTimeSQLException(e); // note: 예외를 전환할때는 꼭 기존 예외를 포함해서 넣어야한다. 그래야 최초 에러인 rootCause를 확인할 수 있다. 아니면 log를 찍어서 기록해둬야한다.
      }
    }

    public void runSQL() throws SQLException {
      throw new SQLException("ex");
    }
  }

  static class RunTimeConnectException extends RuntimeException {
    public RunTimeConnectException(String message) {
      super(message);
    }
  }

  static class RunTimeSQLException extends RuntimeException {
    public RunTimeSQLException(Throwable cause) { // note: throwable에 데이터를 넣으면 이전 예외를 포함할 수 있다.
      super(cause);
    }
  }

}
