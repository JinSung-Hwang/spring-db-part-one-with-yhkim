package hello.jdbc.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.ConnectException;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class CheckedAppTest {
  // note: 체크 예외를 사용하면 단점
  // note:   체크 예외가 여러 계층으로 퍼지게되어서 코드가 지저분해지게 된다.
  // note:   다른 계층의 예외로 인해서 예외 의존성이 생겨버린다. 즉 특정 기술의 예외가 다른 계층에서 의존하게된다.

  @Test
  void checked() {
    Controller controller = new Controller();
    assertThatThrownBy(() -> controller.request())
        .isInstanceOf(Exception.class);
  }

  static class Controller {
    Service service = new Service();
    public void request() throws SQLException, ConnectException {
      service.login();
    }
  }

  static class Service {
    Repository repository = new Repository();
    NetworkClient networkClient = new NetworkClient();

    public void login() throws SQLException, ConnectException {
      repository.call();
      networkClient.call();
    }

  }
  static class NetworkClient {
    public void call() throws ConnectException {
      throw new ConnectException("연결 실패");
    }

  }
  static class Repository {
    public void call() throws SQLException {
      throw new SQLException("ex");
    }
  }

}
