package co.zip.candidate.userapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountModel {

    @NotNull
    @Email
    private String userEmailId;

    private BigDecimal credit;
}
