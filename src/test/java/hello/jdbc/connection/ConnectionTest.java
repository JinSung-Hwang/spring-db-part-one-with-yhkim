package hello.jdbc.connection;

import static hello.jdbc.connection.ConnectionConst.*;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public class ConnectionTest {

  @Test
  void driverManager() throws SQLException {
    Connection con1 = DriverManager.getConnection(URL, USER, PASSWORD);
    Connection con2 = DriverManager.getConnection(URL, USER, PASSWORD);
    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
  }

  @Test
  void dataSourceDriverManager() throws SQLException {
    // note: DriverManagerDataSource는 커넥션 생성과 커넥션을 가져오는 부분이 분리되어있다.
    // note: 이런 방식의 장점은 getConnection을 호출할때마다 설정 정보를 넣어주지 않아도 된다.
    // note: 예를 들어 repository가 10개이면 repository 마다 connection 정보를 넣지 않아도 된다.
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
    useDataSource(dataSource);
  }

  @Test
  void dataSourceConnectPool() throws SQLException, InterruptedException {
    // 커넥션 풀링
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USER);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10);
    dataSource.setPoolName("MyPool");

    useDataSource(dataSource);
    Thread.sleep(2000);
  }

  private void useDataSource(DataSource dataSource) throws SQLException {
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    log.info("connection={}, class={}", con1, con1.getClass());
    log.info("connection={}, class={}", con2, con2.getClass());
  }

}
