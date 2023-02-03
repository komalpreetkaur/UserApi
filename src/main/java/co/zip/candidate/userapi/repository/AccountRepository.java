package co.zip.candidate.userapi.repository;

import co.zip.candidate.userapi.entity.Account;
import co.zip.candidate.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUserId(Long userId);
}
