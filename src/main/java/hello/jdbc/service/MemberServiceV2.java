package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

  private final DataSource dataSource;
  private final MemberRepositoryV2 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    Connection connection = dataSource.getConnection();
    try {
      connection.setAutoCommit(false); // note: 트랜잭션 시작 (기본이 autucommit true이기때문에 false로 설정하면 트랜잭션 시작이라고 봐도 된다.)

      bizLogic(connection, fromId, toId, money);
      connection.commit();
    } catch (Exception e) {
      connection.rollback();
      throw new IllegalStateException(e);
    } finally {
      release(connection);
    }
  }

  private void bizLogic(Connection connection, String fromId, String toId, int money) throws SQLException {
    Member fromMember = memberRepository.findById(connection, fromId);
    Member toMember = memberRepository.findById(connection, toId);

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validation(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void release(Connection connection) throws SQLException {
    if (connection != null) {
      connection.setAutoCommit(true); // note: 커넥션 풀을 사용하면 세션은 계속 유지되기때문에 autoCommit을 다시 true로 안돌리면 커넥션을 재사용할때 autoCommit이 false인 상태가 되어있다.
      connection.close();
      try {
      } catch (Exception e) {
        log.info("error", e);
      }
    }
  }

  private static void validation(Member toMember) {
    if (toMember.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }


}
