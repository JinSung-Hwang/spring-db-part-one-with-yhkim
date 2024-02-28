package hello.jdbc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepository;
import hello.jdbc.repository.MemberRepositoryV4_1;
import hello.jdbc.repository.MemberRepositoryV4_2;
import hello.jdbc.repository.MemberRepositoryV5;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 예외 누수 문제 해결
 * SQLException 제거
 *
 * MemberRepository 인터페이스 의존
 */
@Slf4j
@SpringBootTest
class MemberServiceV4Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private MemberServiceV4 memberService;

  @TestConfiguration
  static class TestConfig {
    private final DataSource dataSource;
    TestConfig(DataSource dataSource) { // note: 스프링에서 스프링부트로 넘어오면서 dataSource와 transactionManager를 자동으로 등록해주기 때문에 MemberService3_3Test처럼 빈등록을하지 않아도 된다.
      this.dataSource = dataSource;
    }

    @Bean
    MemberRepository memberRepository() {
//      return new MemberRepositoryV4_1(dataSource);
//      return new MemberRepositoryV4_2(dataSource);
      return new MemberRepositoryV5(dataSource);
    }

    @Bean
     MemberServiceV4 memberService() {
      return new MemberServiceV4(memberRepository());
    }
  }

  @AfterEach
  void after() {
    memberRepository.delete(MEMBER_A);
    memberRepository.delete(MEMBER_B);
    memberRepository.delete(MEMBER_EX);
  }
  @Test
  void checkAop() {
    log.info("memberService class = {}", memberService.getClass());
    log.info("memberRepository class = {}", memberRepository.getClass());
    assertThat(AopUtils.isAopProxy(memberService)).isTrue();
    assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
  }

  @Test
  @DisplayName("정상 이체")
  void accountTransfer() {
    // Given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);

    // When
    log.info("START TX");
    memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);
    log.info("END TX");

    // Then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberB.getMemberId());
    assertThat(findMemberA.getMoney()).isEqualTo(8000);
    assertThat(findMemberB.getMoney()).isEqualTo(12000);
  }

  @Test
  @DisplayName("이체중 예외 발생")
  void accountTransferEx() {
    // Given
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberEx = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberEx);

    // When
    assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
        .isInstanceOf(IllegalStateException.class);


    // Then
    Member findMemberA = memberRepository.findById(memberA.getMemberId());
    Member findMemberB = memberRepository.findById(memberEx.getMemberId());
    assertThat(findMemberA.getMoney()).isEqualTo(10000);
    assertThat(findMemberB.getMoney()).isEqualTo(10000);
  }


}