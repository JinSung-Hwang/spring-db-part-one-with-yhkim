package hello.jdbc.repository;

import static org.assertj.core.api.Java6Assertions.assertThat;

import hello.jdbc.domain.Member;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

  MemberRepositoryV0 repository = new MemberRepositoryV0();
  
  @Test
  void crud() throws SQLException {
    // save
    Member member = new Member("memberV4", 10000);
    repository.save(member);

    // findById
    Member findMember = repository.findById(member.getMemberId());
    System.out.println("findMember = " + findMember);
    assertThat(findMember).isEqualTo(member);
  }


}