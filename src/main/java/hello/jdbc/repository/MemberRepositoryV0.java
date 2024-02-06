package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRepositoryV0 {

  public Member save(Member member) throws SQLException {
    String sql = "insert into member(member_id, money) values (?, ?)";

    Connection con = null;
    PreparedStatement pstmt = null;

    con = getConnection();
    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, member.getMemberId());
      pstmt.setInt(2, member.getMoney());
      pstmt.executeUpdate();
      return member;
    } catch (SQLException e) {
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, null);
    }
  }

  public Member findById(String memberId) throws SQLException {
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
      log.error("db error", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    // note: 리소스를 클로즈할때는 리소스 획득한 순서의 역순으로 close해야한다. 또한 중간에 close하다가 예외가 발생할 수 있기때문에 각 close할때마다 try-catch로 감싸야한다.
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        log.error("rs close error", e);
      }
    }
    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        log.error("stmt close error", e);
      }
    }
    if (con != null) {
      try {
        con.close();
      } catch (SQLException e) {
        log.error("con close error", e);
      }
    }
  }

  private static Connection getConnection() {
    return DBConnectionUtil.getConnection();
  }

}
