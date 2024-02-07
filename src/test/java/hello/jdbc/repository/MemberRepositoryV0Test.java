package hello.jdbc.repository;

import static org.assertj.core.api.Java6Assertions.assertThat;

import hello.jdbc.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

  MemberRepositoryV0 repository = new MemberRepositoryV0();
  
  @Test
  void crud() throws SQLException {
    // save
    Member member = new Member("memberV7", 10000);
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
  }


}