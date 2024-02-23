package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import java.sql.SQLException;

public interface MemberRepositoryEx {
  // note: 이렇게 Repository의 인터페이스를 만들어도 특정 메서드에서 SQLException을 던지면 특정 기술에 의존하게 된다. (SQLException은 JDBC의 예외이다.)
  // note: 이 Repository 인터페이스를 구현하는 구현체의 메소드는 SQLException을 동일하게 던져야 하기때문이다.
  // note: 체크예외는 인터페이스를 만들어도 의존성이 생겨 다형성을 활용하지 못하게 될 수 있다.
  // note: 따라서 체크 예외는 정말 필요한 상황에서 제대로 사용해야 한다.
  Member save(Member member) throws SQLException;
  Member findById(String memberId) throws SQLException;
  void update(String memberId, int money) throws SQLException;
  void delete(String memberId) throws SQLException;
}
