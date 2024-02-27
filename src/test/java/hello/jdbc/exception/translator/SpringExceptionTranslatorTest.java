package hello.jdbc.exception.translator;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USER;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

/**
 * note: 스프링을 이용한 예외 추상화
 *   DB 종류마다 같은 에러 원읻도 다양하게 에러는 발생시킨다.
 *   스프링은 이런 예외들을 추상화하여 SQLErrorCodeSQLExceptionTranslator로 일정한 형태로 예외를 전환시켜준다.
 *   이렇게 하면 특정 DB에는 종속되지 않지만 스프링에만 종속되는 코드를 작성 할 수 있다.
 *   이렇게되면 스프링을 바꾸지 않는 한 DB가 변경되어도 코드가 변경되지는 않는다.
 */
@Slf4j
public class SpringExceptionTranslatorTest {

  DataSource dataSource;

  @BeforeEach
  void init() {
    dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
  }

  @Test
  void sqlExceptionErrorCode() {
    String sql = "select bad grammer";

    try {
      Connection con = dataSource.getConnection();
      PreparedStatement stmt = con.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);
      int errorCode = e.getErrorCode();
      log.info("errorCode={}", errorCode);
      log.info("error", e);
    }
  }

  @Test
  void exceptionTranslator () {
    String sql = "select bad grammer";

    try {
      Connection con = dataSource.getConnection();
      PreparedStatement stmt = con.prepareStatement(sql);
      stmt.executeUpdate();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);

      // note: org.springframework.jdbc.support.sql-error-codes.xml 이라는 파일이 DB에러 코드에 대한 맵핑정보를 가지고 있어서 exceptionTranslator.translate가 가능히다.
      SQLErrorCodeSQLExceptionTranslator exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
      DataAccessException resultEx = exceptionTranslator.translate("select", sql, e); // note: desc, 사용한 sql, exception을 넣으면 된다.
      log.info("resultEx", resultEx);
      assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
      throw resultEx;
    }
  }

}
