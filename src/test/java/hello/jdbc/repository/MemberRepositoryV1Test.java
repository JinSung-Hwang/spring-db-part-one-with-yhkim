package hello.jdbc.repository;

import static hello.jdbc.connection.ConnectionConst.PASSWORD;
import static hello.jdbc.connection.ConnectionConst.URL;
import static hello.jdbc.connection.ConnectionConst.USER;
import static org.assertj.core.api.Java6Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class MemberRepositoryV1Test {

  MemberRepositoryV1 repository;

  @BeforeEach
  void beforeEach() {
    // 기본 DriverManager - 항상 새로운 커넥션을 획득
//    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);

    // 커넥션 풀링
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USER);
    dataSource.setPoolName(PASSWORD);
    repository = new MemberRepositoryV1(dataSource);
  }

  @Test
  void crud() throws SQLException, InterruptedException {
    // save
    Member member = new Member("memberV10", 10000);
    repository.save(member);

    // findById
    Member findMember = repository.findById(member.getMemberId());
    System.out.println("findMember = " + findMember);
    assertThat(findMember).isEqualTo(member);

    // update: money 10000 -> 20000
    repository.update(member.getMemberId(), 20000);
    Member updateMember = repository.findById(member.getMemberId());
    assertThat(updateMember.getMoney()).isEqualTo(20000);

    //delete
    repository.delete(member.getMemberId());
    Assertions.assertThatThrownBy(() -> repository.findById(member.getMemberId()))
        .isInstanceOf(NoSuchElementException.class);

    TimeUnit.SECONDS.sleep(1);
  }


}