package co.zip.candidate.userapi.exception;

public class AccountNotCreatedException extends RuntimeException {

    public AccountNotCreatedException(Long id) {
        super("Due to balance less than $1000, account not created for user with id " + id );
    }
}
