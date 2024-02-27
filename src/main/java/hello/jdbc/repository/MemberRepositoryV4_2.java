package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;

/**
 * SQLExceptionTranslator 추가
 */
@Slf4j
public class MemberRepositoryV4_2 implements MemberRepository {
  private final DataSource dataSource;
  private final SQLExceptionTranslator exTranslator;

  public MemberRepositoryV4_2(DataSource dataSource) {
    this.dataSource = dataSource;
    this.exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource); // note: 에러 코드를 기반으로 스프링 에러 추상화 객체를 주입한다.
  }

  @Override
  public Member save(Member member) {
    String sql = "insert into member(member_id, money) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, member.getMemberId());
      pstmt.setInt(2, member.getMoney());
      pstmt.executeUpdate();
      return member;
    } catch (SQLException e) {
      throw exTranslator.translate("save", sql, e);
    } finally {
      close(con, pstmt, null);
    }
  }

  @Override
  public Member findById(String memberId) {
    String sql = "select * from member where member_id = ?";

    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);

      rs = pstmt.executeQuery();
      if (rs.next()) { // note: rs를 한번은 next()호출해야 데이터가 있는 row로 커서가 이동된다.
        Member member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setMoney(rs.getInt("money"));
        return member;
      } else {
        throw new NoSuchElementException("member not found member=" + memberId);
      }

    } catch (SQLException e) {
      throw exTranslator.translate("findById", sql, e);
    } finally {
      close(con, pstmt, rs);
    }
  }

  @Override
  public void update(String memberId, int money) {
    String sql = "update member set money=? where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);
      int resultSize = pstmt.executeUpdate();
      System.out.println("resultSize = " + resultSize);
    } catch (SQLException e) {
      throw exTranslator.translate("update", sql, e);
    } finally {
      close(con, pstmt, null);
    }
  }

  @Override
  public void delete(String memberId) {
    String sql = "delete from member where member_id=?";

    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw exTranslator.translate("delete", sql, e);
    } finally {
      close(con, pstmt, null);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    // note: 중요함! 트랜잭션 동기화를 사용하려면 DatqSourceUtils를 사용해야 한다.
    DataSourceUtils.releaseConnection(con, dataSource);
  }

  private Connection getConnection() throws SQLException {
    // note: 중요함! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야한다.
    Connection con = DataSourceUtils.getConnection(dataSource); // note: 안쪽 코드에서 TransactionSynchronizationManager.getResource 가 호출되는데 결국엔 트랜잭션동기화매너지에서 커넥션을 가져오는것이다.
    // note: DataSourceUtils.getConnection(dataSource)는 만약 transactionSyncronizationManager에 커넥션이 없으면 새로운 커넥션을 생성하고 있으면 threadLocal에 있는 커넥션을 반환한다.
    log.info("get connection={}, class={}", con, con.getClass());
    return con;
  }

}
